package project.closet.dm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import project.closet.dm.entity.DirectMessage;
import project.closet.user.entity.Profile;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class DirectMessageRepositoryCustomTest {

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("두 유저 간의 최신 메시지를 커서 없이 조회한다")
    void findDirectMessagesWithoutCursor() {
        // given
        User sender = saveUserWithProfile("Alice", "alice.png");
        User receiver = saveUserWithProfile("Bob", "bob.png");

        DirectMessage message1 = DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content("Hello")
                .build();
        DirectMessage message2 = new DirectMessage(receiver, sender, "Hi!");
        directMessageRepository.saveAll(List.of(message1, message2));
        em.flush();
        em.clear();
        // when
        List<DirectMessage> messages = directMessageRepository
                .findDirectMessagesBetweenUsers(sender.getId(), receiver.getId(), null, null, 10);
        for (DirectMessage message : messages) {
            String senderImageKey = message.getSender().getProfile().getProfileImageKey();
            String receiverImageKey = message.getReceiver().getProfile().getProfileImageKey();
        }
        // then
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).isEqualTo("Hi!");
        assertThat(messages.get(1).getContent()).isEqualTo("Hello");

        // fetch join 확인
        assertThat(messages.get(0).getSender().getProfile().getProfileImageKey()).isEqualTo("bob.png");
        assertThat(messages.get(0).getReceiver().getProfile().getProfileImageKey()).isEqualTo("alice.png");
    }

    private User saveUserWithProfile(String name, String imageUrl) {

        User user = new User(name, UUID.randomUUID().toString(), "password");
        Profile profile = Profile.createDefault(user);
        profile.updateProfileImageKey(imageUrl);
        return userRepository.save(user);
    }
}
