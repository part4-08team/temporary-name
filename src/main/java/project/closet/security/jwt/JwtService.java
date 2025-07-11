package project.closet.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.exception.ErrorCode;
import project.closet.exception.user.UserNotFoundException;
import project.closet.security.ClosetUserDetails;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.access-token-validity-seconds}")
    private long accessTokenValiditySeconds;
    @Value("${security.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    private final JwtSessionRepository jwtSessionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public JwtSession registerJwtSession(ClosetUserDetails userDetails) {
        JwtObject accessJwtObject = generateJwtObject(
                userDetails.getUserId(),
                userDetails.getName(),
                userDetails.getEmail(),
                userDetails.getRole(),
                accessTokenValiditySeconds
        );
        JwtObject refreshJwtObject = generateJwtObject(
                userDetails.getUserId(),
                userDetails.getName(),
                userDetails.getEmail(),
                userDetails.getRole(),
                refreshTokenValiditySeconds
        );

        JwtSession jwtSession = new JwtSession(userDetails.getUserId(), accessJwtObject.token(),
                refreshJwtObject.token(), accessJwtObject.expirationTime());
        jwtSessionRepository.save(jwtSession);

        return jwtSession;
    }

    public boolean validate(String token) {
        boolean verified;

        try {
            JWSVerifier verifier = new MACVerifier(secret);
            JWSObject jwsObject = JWSObject.parse(token);
            verified = jwsObject.verify(verifier);

            if (verified) {
                JwtObject jwtObject = parse(token);
                verified = !jwtObject.isExpired();
            }
        } catch (JOSEException | ParseException exception) {
            log.error(exception.getMessage());
            verified = false;
        }

        return verified;
    }

    public JwtObject parse(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            Payload payload = jwsObject.getPayload();
            Map<String, Object> jsonObject = payload.toJSONObject();
            return new JwtObject(
                    objectMapper.convertValue(jsonObject.get("iat"), Instant.class),
                    objectMapper.convertValue(jsonObject.get("exp"), Instant.class),
                    objectMapper.convertValue(jsonObject.get("userId"), UUID.class),
                    objectMapper.convertValue(jsonObject.get("name"), String.class),
                    objectMapper.convertValue(jsonObject.get("email"), String.class),
                    objectMapper.convertValue(jsonObject.get("role"), Role.class),
                    token
            );
        } catch (ParseException e) {
            log.error(e.getMessage());
            throw new JwtException(ErrorCode.INVALID_TOKEN, Map.of("token", token), e);
        }
    }

    @Transactional
    public JwtSession refreshJwtSession(String refreshToken) {
        if (!validate(refreshToken)) {
            throw new JwtException(ErrorCode.INVALID_TOKEN,
                    Map.of("refreshToken", refreshToken));
        }
        JwtSession session = jwtSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException(ErrorCode.TOKEN_NOT_FOUND,
                        Map.of("refreshToken", refreshToken)));

        UUID userId = parse(refreshToken).userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        JwtObject accessJwtObject = generateJwtObject(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessTokenValiditySeconds
        );
        JwtObject refreshJwtObject = generateJwtObject(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessTokenValiditySeconds
        );

        session.update(
                accessJwtObject.token(),
                refreshJwtObject.token(),
                accessJwtObject.expirationTime()
        );

        return session;
    }

    @Transactional
    public void invalidateJwtSession(String refreshToken) {
        jwtSessionRepository.deleteByRefreshToken(refreshToken);
    }

    @Transactional
    public void invalidateJwtSession(UUID userId) {
        jwtSessionRepository.deleteByUserId(userId);
    }

    private JwtObject generateJwtObject(
            UUID userId,
            String name,
            String email,
            Role role,
            long tokenValiditySeconds
    ) {
        Instant issueTime = Instant.now();
        Instant expirationTime = issueTime.plus(Duration.ofSeconds(tokenValiditySeconds));

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .claim("userId", userId.toString())
                .claim("role", role)
                .claim("name", name)
                .claim("email", email)
                .issueTime(new Date(issueTime.toEpochMilli()))
                .expirationTime(new Date(expirationTime.toEpochMilli()))
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        try {
            signedJWT.sign(new MACSigner(secret));
        } catch (JOSEException e) {
            log.error(e.getMessage());
            throw new JwtException(ErrorCode.INVALID_TOKEN_SECRET, e);
        }

        String token = signedJWT.serialize();


        return new JwtObject(
                issueTime,
                expirationTime,
                userId,
                name,
                email,
                role,
                token
        );
    }

    public JwtSession getSwtSession(String refreshToken) {
        return jwtSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException(ErrorCode.TOKEN_NOT_FOUND,
                        Map.of("refreshToken", refreshToken)));
    }
}
