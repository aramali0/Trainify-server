package e_learning.DTO;

public record ChangePassword(
        String email,
        String currentPassword,
        String newPassword
) {}