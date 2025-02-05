package e_learning.services.ServiceImpl;

import e_learning.DTO.AuthenticationDTO;
import e_learning.exceptions.RefreshTokenExpiredException;
import e_learning.services.JwtService;
import e_learning.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service

public class JwtServiceImpl implements JwtService {
    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Lazy
    @Autowired
    UserService userService;
    @Override
    public Map<String, String> generateToken(AuthenticationDTO authenticationInfo) throws RefreshTokenExpiredException {
        System.out.println(authenticationInfo.toString());
        Map<String, String> idToken = new HashMap<>();

        String scope = null;
        String subject = null;
        String userId = null; // New variable to hold the userId

        if(!authenticationInfo.withRefreshToken() && authenticationInfo.password() != null){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationInfo.email(), authenticationInfo.password())
            );
            subject = authentication.getName();
            scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        } else if(authenticationInfo.withRefreshToken()){
            if(authenticationInfo.refreshToken() == null){
                throw new RuntimeException("Your refresh Token has expired");
            }
            Jwt jwtDecoded = null;
            try {
                jwtDecoded = jwtDecoder.decode(authenticationInfo.refreshToken());
            } catch(Exception e){
                throw new RefreshTokenExpiredException(e.getMessage());
            }
            subject = jwtDecoded.getSubject();
            scope = ((UserDetailsService) userService).loadUserByUsername(subject).getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
            System.out.println("Subject: " + subject);
        } else {
            return Map.of("err-message", "Incorrect data entries");
        }

        // Retrieve the userId from the UserService using the subject
        userId = userService.findByEmail(subject).getUserId().toString();

        Instant instant = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(authenticationInfo.withRefreshToken() ? 80 : 1, ChronoUnit.DAYS))
                .subject(subject)
                .claim("scope", scope)
                .claim("userId", userId) // Add the userId to the JWT claims
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();

        if(authenticationInfo.withRefreshToken()){
            JwtClaimsSet claimsSets = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(80, ChronoUnit.DAYS))
                    .subject(subject)
                    .build();
            String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(claimsSets)).getTokenValue();
            idToken.put("refreshToken", refreshToken);
        }

        idToken.put("accessToken", token);
        return idToken;
    }
}
