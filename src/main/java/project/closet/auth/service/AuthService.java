package project.closet.auth.service;

public interface AuthService {

    void initAdmin();

    void resetPassword(String email);
}
