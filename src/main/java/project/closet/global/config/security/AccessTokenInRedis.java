package project.closet.global.config.security;

// todo : 이름 수정
public record AccessTokenInRedis(
    String accessToken)
    // parsing 비용이 크면 expirationAt 추가
    //Instant expirationAt)
    {
}
