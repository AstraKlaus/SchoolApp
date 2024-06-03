package ak.spring.auth;
import ak.spring.auth.AuthenticationService;
import ak.spring.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authService;

  @PostMapping("/register")
  public ResponseEntity<Person> register(@RequestBody RegisterRequest request) {
    Person person = authService.register(request);
    return ResponseEntity.ok(person);
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(@RequestBody AuthenticationRequest request,
                                    HttpServletRequest httpRequest,
                                    HttpServletResponse httpResponse) {
    authService.authenticate(request, httpRequest, httpResponse);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    authService.logout(request, response);
    return ResponseEntity.ok().build();
  }
}