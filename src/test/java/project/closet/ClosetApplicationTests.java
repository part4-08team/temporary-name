package project.closet;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import project.closet.config.TestContainerConfig;

// Testcontainers 작동 테스트
@Import(TestContainerConfig.class)
@SpringBootTest
class ClosetApplicationTests {

  Logger log = LoggerFactory.getLogger(ClosetApplicationTests.class);

  @Autowired
  PostgreSQLContainer<?> container;

  // redis Container도 필요
  @Test
  void contextLoads() {
    log.info("---{}---", container.getJdbcUrl());
    log.info("---{}---", container.getUsername());
    log.info("---{}---", container.getPassword());
  }
}
