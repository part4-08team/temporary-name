package project.closet.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import project.closet.domain.users.AuthService;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final AuthService authService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    authService.initAdmin();
  }
}
