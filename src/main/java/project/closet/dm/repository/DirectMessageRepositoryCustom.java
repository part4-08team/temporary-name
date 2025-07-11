package project.closet.dm.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import project.closet.dm.entity.DirectMessage;

public interface DirectMessageRepositoryCustom {
    List<DirectMessage> findDirectMessagesBetweenUsers(UUID targetUserId, UUID loginUserId, Instant cursor, UUID idAfter, int limit);

    long countDirectMessagesBetweenUsers(UUID targetUserId, UUID loginUserId);
}
