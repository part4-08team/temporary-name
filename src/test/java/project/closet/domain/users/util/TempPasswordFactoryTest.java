package project.closet.domain.users.util;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TempPasswordFactoryTest {

  @Test
  void 난수_생성_확인용() {
    String tempPassword = TemporaryPasswordFactory.createTempPassword();
    System.out.println(tempPassword);

    assertThat(tempPassword).isNotNull();
    assertThat(tempPassword.length()).isEqualTo(16);
  }
}