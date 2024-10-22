package e_learning.exceptions;

public class RefreshTokenExpiredException extends Exception{
    public RefreshTokenExpiredException(String message){
        super(message);
    }
}
