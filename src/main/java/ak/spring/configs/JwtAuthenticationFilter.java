package ak.spring.configs;

import ak.spring.token.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      // Пропускаем запросы на логин
      if (request.getServletPath().contains("/api/auth/login") || request.getServletPath().contains("/api/auth/refresh")) {
        filterChain.doFilter(request, response);
        return;
      }

      final String authHeader = request.getHeader("Authorization");

      // Если заголовок отсутствует или не начинается с "Bearer ", пропускаем
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      final String jwt = authHeader.substring(7);
      final String userName = jwtService.extractUsername(jwt);

      if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

        boolean isTokenValid = tokenRepository.findByToken(jwt)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);

        if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
          );
          authToken.setDetails(
                  new WebAuthenticationDetailsSource().buildDetails(request)
          );
          SecurityContextHolder.getContext().setAuthentication(authToken);
          filterChain.doFilter(request, response);
        } else {
          // Если токен недействителен или аннулирован, возвращаем 401
          sendError(response, "Invalid or revoked token");
          return;
        }
      } else {
        filterChain.doFilter(request, response);
      }

    } catch (ExpiredJwtException ex) {
      sendError(response, "Token expired");
    } catch (JwtException | IllegalArgumentException ex) {
      sendError(response, "Invalid token");
    } catch (Exception ex) {
      sendError(response, "Authentication failed");
    }
  }

  private void sendError(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write("{ \"error\": \"" + message + "\" }");
  }
}