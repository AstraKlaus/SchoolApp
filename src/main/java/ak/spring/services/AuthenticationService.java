package ak.spring.services;

import ak.spring.models.AuthenticationRequest;
import ak.spring.models.Person;
import ak.spring.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final AdminService adminService;
  public Person register(Person person) {
    return adminService.uploadPerson(person);
  }

  public Person authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
    );
    return adminService.findByUsername(request.getUsername());
  }
}
