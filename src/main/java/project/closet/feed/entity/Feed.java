package project.closet.feed.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import project.closet.domain.base.BaseUpdatableEntity;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.user.entity.User;
import project.closet.weather.entity.Weather;

@Getter
@Entity
@Table(name = "feeds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weather_id", nullable = false)
    private Weather weather;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedClothes> feedClothesList = new ArrayList<>();

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    public Feed(User author, Weather weather, String content) {
        this.author = author;
        this.weather = weather;
        this.content = content;
    }

    public void addClothes(Clothes clothes) {
        FeedClothes feedClothes = new FeedClothes(this, clothes);
        this.feedClothesList.add(feedClothes);
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or blank");
        }
        this.content = content;
    }
}
