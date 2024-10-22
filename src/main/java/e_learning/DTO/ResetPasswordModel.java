package e_learning.DTO;

public record ResetPasswordModel (
   String email,
   String code,
   String newPassword

) {}
