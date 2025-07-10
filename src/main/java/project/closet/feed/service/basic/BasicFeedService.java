package project.closet.feed.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.clothes.repository.ClothesRepository;
import project.closet.dto.request.CommentCreateRequest;
import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.request.FeedUpdateRequest;
import project.closet.dto.response.CommentDto;
import project.closet.dto.response.CommentDtoCursorResponse;
import project.closet.dto.response.FeedDto;
import project.closet.dto.response.FeedDtoCursorResponse;
import project.closet.dto.response.OotdDto;
import project.closet.dto.response.UserSummary;
import project.closet.dto.response.WeatherSummaryDto;
import project.closet.exception.feed.FeedLikeAlreadyExistsException;
import project.closet.exception.feed.FeedNotFoundException;
import project.closet.exception.user.UserNotFoundException;
import project.closet.exception.weather.WeatherNotFoundException;
import project.closet.feed.entity.Feed;
import project.closet.feed.entity.FeedComment;
import project.closet.feed.entity.FeedLike;
import project.closet.feed.repository.FeedCommentRepository;
import project.closet.feed.repository.FeedLikeRepository;
import project.closet.feed.repository.FeedRepository;
import project.closet.feed.service.FeedService;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;
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

        return toFeedDto(feed, 0, false);
    }


    // TODO : Feed Like Count 업데이트 구현해야함 + 1, -1
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

    @Transactional
    @Override
    public void deleteFeed(UUID feedId) {
        feedRepository.deleteById(feedId);
    }

    @Transactional
    @Override
    public FeedDto updateFeed(UUID feedId, FeedUpdateRequest feedUpdateRequest, UUID loginUserId) {
        // Feed -> User, Weather, Clothes fetch join을 통해서 유저 정보도 함께 가져와야함
        Feed feed = feedRepository.findByIdWithAll(feedId)
                .orElseThrow(() -> FeedNotFoundException.withId(feedId));
        feed.updateContent(feedUpdateRequest.content());

        // like count 조회
        long likeCount = feedLikeRepository.countByFeed(feed);

        boolean likedByMe = feedLikeRepository.existsByUserIdAndFeedId(loginUserId, feedId);

        long commentCount = feedCommentRepository.countByFeedId(feed.getId());
        return toFeedDto(feed, commentCount, likedByMe);
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDtoCursorResponse getFeedComments(
            UUID feedId,
            Instant cursor,
            UUID idAfter,
            int limit
    ) {
        if (!feedRepository.existsById(feedId)) {
            throw FeedNotFoundException.withId(feedId);
        }

        List<FeedComment> comments =
                feedCommentRepository.findByFeedWithCursor(feedId, cursor, idAfter, limit + 1);

        long totalCount = feedCommentRepository.countByFeedId(feedId);

        boolean hasNext = comments.size() > limit;
        List<FeedComment> result = comments.stream()
                .limit(limit)
                .toList();

        Instant nextCursor = null;
        UUID nextId = null;
        if (!result.isEmpty()) {
            FeedComment last = result.get(result.size() - 1);
            nextCursor = last.getCreatedAt();
            nextId = last.getId();
        }

        List<CommentDto> data = result.stream()
                .map(CommentDto::from)
                .toList();

        return new CommentDtoCursorResponse(
                data,
                nextCursor,
                nextId,
                hasNext,
                totalCount,
                "createdAt",
                SortDirection.ASCENDING
        );
    }

    @Transactional(readOnly = true)
    @Override
    public FeedDtoCursorResponse getFeedList(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection,
            String keywordLike,
            SkyStatus skyStatusEqual,
            PrecipitationType precipitationType,
            UUID authorIdEqual,
            UUID loginUserId
    ) {
        List<Feed> feeds = feedRepository.findAllWithCursorAndFilters(
                cursor, idAfter, limit, sortBy, sortDirection,
                keywordLike, skyStatusEqual, precipitationType, authorIdEqual
        );

        boolean hasNext = feeds.size() > limit;
        if (hasNext) {
            feeds = feeds.subList(0, limit);
        }

        Feed last = feeds.isEmpty() ? null : feeds.get(feeds.size() - 1);
        String nextCursor = null;
        UUID nextIdAfter = null;

        if (last != null) {
            nextIdAfter = last.getId();
            nextCursor = switch (sortBy) {
                case "createdAt" -> last.getCreatedAt().toString();
                case "likeCount" -> String.valueOf(last.getLikeCount());
                default -> throw new IllegalArgumentException("지원하지 않는 sortBy 값입니다: " + sortBy);
            };
        }

        List<FeedDto> feedDtos = feeds.stream()
                .map(feed -> {
                    long commentCount = feedCommentRepository.countByFeedId(feed.getId());
                    boolean likedByMe =
                            feedLikeRepository.existsByUserIdAndFeedId(loginUserId, feed.getId());
                    return toFeedDto(feed, commentCount, likedByMe);
                })
                .toList();

        long totalCount = feedRepository.countByFilters(
                keywordLike, skyStatusEqual, precipitationType, authorIdEqual);

        return new FeedDtoCursorResponse(
                feedDtos,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                sortBy,
                sortDirection
        );
    }

    private FeedDto toFeedDto(Feed feed, long commentCount, boolean likedByMe) {
        List<OotdDto> ootdDtos = feed.getFeedClothesList().stream()
                .map(feedClothes -> OotdDto.from(feedClothes.getClothes()))
                .toList();

        return new FeedDto(
                feed.getId(),
                feed.getCreatedAt(),
                feed.getUpdatedAt(),
                UserSummary.from(feed.getAuthor()),
                WeatherSummaryDto.from(feed.getWeather()),
                ootdDtos,
                feed.getContent(),
                feed.getLikeCount(),
                commentCount,
                likedByMe
        );
    }
}
