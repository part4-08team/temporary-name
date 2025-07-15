package project.closet.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.base.BaseEntity;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Column(name = "receiver_id", columnDefinition = "uuid", nullable = false)
    private UUID receiverId;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private NotificationLevel level;

    @Builder
    public Notification(UUID receiverId, String title, String content, NotificationLevel level) {
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
        this.level = level;
    }
}
