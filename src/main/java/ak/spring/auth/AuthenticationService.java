package ak.spring.auth;


import ak.spring.configs.JwtService;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Person;
import ak.spring.repositories.PersonRepository;
import ak.spring.services.ExcelService;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final PersonRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final PersonDTOMapper personDTOMapper;
  private final ExcelService excelService;

  public AuthenticationResponse register(RegisterRequest request) {
    String username = generateUsername();

    String password = generateRandomPassword();

    try {
      excelService.addUserToExcel(username, request.getFirstName(), request.getPatronymic(), request.getLastName(), password);
    } catch (IOException e) {
      System.out.println("Ошибка при сохранении в Excel: " + e.getMessage());
    }

    var user = Person.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .patronymic(request.getPatronymic())
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(request.getRole())
            .build();

    var savedUser = repository.save(user);

    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    saveUserToken(savedUser, jwtToken);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .person(personDTOMapper.apply(user))
            .build();
  }

  private String generateUsername() {
    int year = LocalDate.now().getYear();
    String yearPattern = year + "%";
    int nextNumber = repository.getNextUserNumber(yearPattern) + 1;
    return String.format("%d%04d", year, nextNumber);
  }

  public List<AuthenticationResponse> registerUsersFromExcel(InputStream inputStream) throws IOException {
    List<RegisterRequest> users = excelService.importUsersFromExcel(inputStream);
    List<AuthenticationResponse> registeredUsers = new ArrayList<>();
    for (RegisterRequest user: users) {
        registeredUsers.add(register(user));
    }
    return registeredUsers;
  }

  private String generateRandomPassword() {
    int length = 8;
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder password = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      password.append(characters.charAt(random.nextInt(characters.length())));
    }
    return password.toString();
  }


  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
    );
    var user = repository.findByUsername(request.getUsername())
            .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .person(personDTOMapper.apply(user))
            .build();
  }

  private void saveUserToken(Person user, String jwtToken) {
    var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(Person user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public AuthenticationResponse refreshToken(HttpServletRequest request) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    String refreshToken = Optional.ofNullable(authHeader)
            .filter(header -> header.startsWith("Bearer "))
            .map(header -> header.substring(7))
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

    Person user = repository.findByUsername(jwtService.extractUsername(refreshToken))
            .filter(u -> jwtService.isTokenValid(refreshToken, u))
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    String accessToken = jwtService.generateToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, accessToken);

    return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
  }
}
