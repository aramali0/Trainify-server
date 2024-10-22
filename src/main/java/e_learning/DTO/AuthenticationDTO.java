package e_learning.DTO;

public record AuthenticationDTO(
        String email,
        String password,
        boolean withRefreshToken,
        String refreshToken,
        String grantType
) {}