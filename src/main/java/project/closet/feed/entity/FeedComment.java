package project.closet.feed.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.base.BaseUpdatableEntity;
import project.closet.user.entity.User;

@Getter
@Entity
@Table(name = "feed_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComment extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public FeedComment(Feed feed, User author, String content) {
        this.feed = feed;
        this.author = author;
        this.content = content;
    }
}
