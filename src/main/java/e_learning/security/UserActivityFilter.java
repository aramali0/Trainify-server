package e_learning.security;
import e_learning.entity.UserApp;
import e_learning.repositories.UserAppRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class UserActivityFilter extends OncePerRequestFilter {

    private final UserAppRepository userAppRepository;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {

            if (authentication.isAuthenticated()) {
                String email = authentication.getName();
                UserApp user = userAppRepository.findUserAppByEmail(email);

                if (user != null) {
                    user.setLastActiveTime(LocalDateTime.now());
                    userAppRepository.save(user);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

