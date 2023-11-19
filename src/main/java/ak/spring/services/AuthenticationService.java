package ak.spring.services;

import ak.spring.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AdminService adminService;

  public Person register(Person person) {
    return adminService.uploadPerson(person);
  }
}
