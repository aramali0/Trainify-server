package e_learning.services;

import e_learning.DTO.AuthenticationDTO;
import e_learning.exceptions.RefreshTokenExpiredException;

import java.util.Map;

public interface JwtService {
    public Map<String,String> generateToken(AuthenticationDTO authenticationDTO) throws RefreshTokenExpiredException;
}
