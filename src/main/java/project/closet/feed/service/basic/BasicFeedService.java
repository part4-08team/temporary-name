package project.closet.feed.service.basic;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.clothes.repository.ClothesRepository;
import project.closet.dto.request.CommentCreateRequest;
import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.response.CommentDto;
import project.closet.dto.response.FeedDto;
import project.closet.dto.response.OotdDto;
import project.closet.dto.response.UserSummary;
import project.closet.dto.response.WeatherSummaryDto;
import project.closet.exception.feed.FeedLikeAlreadyExistsException;
import project.closet.exception.feed.FeedNotFoundException;
import project.closet.exception.user.UserNotFoundException;
import project.closet.exception.weather.WeatherNotFoundException;
import project.closet.feed.entity.Feed;
import project.closet.feed.entity.FeedClothes;
import project.closet.feed.entity.FeedComment;
import project.closet.feed.repository.FeedCommentRepository;
import project.closet.feed.repository.FeedLikeRepository;
import project.closet.feed.repository.FeedRepository;
import project.closet.feed.service.FeedService;
import project.closet.follower.entity.FeedLike;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;
import project.closet.weather.entity.Weather;
import project.closet.weather.repository.WeatherRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicFeedService implements FeedService {

    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final FeedRepository feedRepository;
    private final ClothesRepository clothesRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedCommentRepository feedCommentRepository;

    @Transactional
    @Override
    public FeedDto createFeed(FeedCreateRequest feedCreateRequest) {
        UUID authorId = feedCreateRequest.authorId();
        User author = userRepository.findByIdWithProfile(authorId)
                .orElseThrow(() -> UserNotFoundException.withId(authorId));

        UUID weatherId = feedCreateRequest.weatherId();
        Weather weather = weatherRepository.findById(weatherId)
                .orElseThrow(() -> WeatherNotFoundException.withId(weatherId));

        Feed feed = new Feed(author, weather, feedCreateRequest.content());

        clothesRepository.findAllByIdInWithAttributes(feedCreateRequest.clothesIds())
                .forEach(feed::addClothes);

        feedRepository.save(feed);

        List<OotdDto> ootdDtos = feed.getFeedClothesList()
                .stream().map(FeedClothes::getClothes)
                .map(OotdDto::from)
                .toList();

        FeedDto feedDto = new FeedDto(
                feed.getId(),
                feed.getCreatedAt(),
                feed.getUpdatedAt(),
                UserSummary.from(author),
                WeatherSummaryDto.from(weather),
                ootdDtos,
                feed.getContent(),
                0,
                0,
                false
        );
        return feedDto;
    }

    @Transactional
    @Override
    public void likeFeed(UUID feedId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> FeedNotFoundException.withId(feedId));

        // 중복 체크
        if (feedLikeRepository.existsByUserAndFeed(user, feed)) {
            throw FeedLikeAlreadyExistsException.of(userId, feedId);
        }

        feedLikeRepository.save(new FeedLike(feed, user));
    }

    @Transactional
    @Override
    public void cancelFeedLike(UUID feedId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> FeedNotFoundException.withId(feedId));

        feedLikeRepository.deleteByUserAndFeed(user, feed);
        // Like 취소 시에 알림이 필요하다면, int 반환 받아서 값이 1이면 알림 생성하도록 할 수 있음
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentCreateRequest commentCreateRequest) {
        UUID feedId = commentCreateRequest.feedId();
        UUID authorId = commentCreateRequest.authorId();
        User author = userRepository.findByIdWithProfile(authorId)
                .orElseThrow(() -> UserNotFoundException.withId(authorId));
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> FeedNotFoundException.withId(feedId));
        FeedComment feedComment = new FeedComment(feed, author, commentCreateRequest.content());
        feedCommentRepository.save(feedComment);
        return CommentDto.from(feedComment);
    }
}
