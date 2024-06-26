package dev.yerokha.flightstatusapi.infrastructure.service;

import dev.yerokha.flightstatusapi.application.exception.InvalidTokenException;
import dev.yerokha.flightstatusapi.domain.entity.UserEntity;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginResponse;
import dev.yerokha.flightstatusapi.infrastructure.entity.RefreshToken;
import dev.yerokha.flightstatusapi.infrastructure.repository.TokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.yerokha.flightstatusapi.infrastructure.util.EncryptionUtil.decrypt;
import static dev.yerokha.flightstatusapi.infrastructure.util.EncryptionUtil.encrypt;
import static dev.yerokha.flightstatusapi.infrastructure.util.RedisUtil.containsKey;
import static dev.yerokha.flightstatusapi.infrastructure.util.RedisUtil.deleteKey;
import static dev.yerokha.flightstatusapi.infrastructure.util.RedisUtil.setValue;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final TokenRepository tokenRepository;

    private static final int ACCESS_TOKEN_EXPIRATION = 60;
    private static final int REFRESH_TOKEN_EXPIRATION = ACCESS_TOKEN_EXPIRATION * 24 * 7;

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, TokenRepository tokenRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenRepository = tokenRepository;
    }

    static String getEmailFromAuthToken(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }

    public String generateAccessToken(UserEntity user) {
        String accessToken = generateToken(user, ACCESS_TOKEN_EXPIRATION, TokenType.ACCESS);
        setValue("access_token:" + user.getUsername(),
                encrypt(accessToken),
                ACCESS_TOKEN_EXPIRATION,
                TimeUnit.MINUTES);
        return accessToken;
    }

    public String generateRefreshToken(UserEntity entity) {
        String refreshToken = generateToken(entity, REFRESH_TOKEN_EXPIRATION, TokenType.REFRESH);
        String encryptedToken = encrypt("Bearer " + refreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken(
                encryptedToken,
                entity,
                Instant.now(),
                Instant.now().plus(REFRESH_TOKEN_EXPIRATION, ChronoUnit.MINUTES)
        );
        tokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }

    private String generateToken(UserEntity user, int expirationTime, TokenType tokenType) {
        Instant now = Instant.now();
        String role = user.getRole().getCode();

        JwtClaimsSet claims = getClaims(
                now,
                expirationTime,
                user.getUsername(),
                user.getId(),
                role,
                tokenType);
        return encodeToken(claims);
    }

    private JwtClaimsSet getClaims(Instant now,
                                   int expirationTime,
                                   String username,
                                   Long userId,
                                   String role,
                                   TokenType tokenType) {
        return JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expirationTime, ChronoUnit.MINUTES))
                .subject(username)
                .claim("role", role)
                .claim("tokenType", tokenType)
                .claim("userId", userId)
                .build();
    }

    private String encodeToken(JwtClaimsSet claims) {
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String getUsernameFromTokenString(String token) {
        return decodeToken(token).getSubject();
    }

    private Jwt decodeToken(String token) {
        if (!token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format");
        }

        String strippedToken = token.substring(7);

        try {
            return jwtDecoder.decode(strippedToken);
        } catch (InvalidTokenException | JwtException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }

    public LoginResponse refreshAccessToken(String refreshToken) {
        Jwt decodedToken = decodeToken(refreshToken);
        if (!decodedToken.getClaim("tokenType").equals(TokenType.REFRESH.name())) {
            throw new InvalidTokenException("Invalid token type");
        }

        String username = decodedToken.getSubject();
        if (isRevoked(refreshToken, username)) {
            throw new InvalidTokenException("Token is revoked");
        }

        return getNewAccessToken(decodedToken, username);

    }

    private LoginResponse getNewAccessToken(Jwt decodedToken, String username) {
        Instant now = Instant.now();
        Long userId = decodedToken.getClaim("userId");
        String roles = decodedToken.getClaim("role");
        JwtClaimsSet claims = getClaims(now,
                ACCESS_TOKEN_EXPIRATION,
                username,
                userId,
                roles,
                TokenType.ACCESS);
        String token = encodeToken(claims);
        String key = "access_token:" + username;
        setValue(key, encrypt(token), ACCESS_TOKEN_EXPIRATION, TimeUnit.MINUTES);
        return new LoginResponse(
                token,
                ""
        );
    }

    private boolean isRevoked(String refreshToken, String username) {
        if (isTokenRevoked(refreshToken)) {
            return true;
        }

        List<RefreshToken> tokenList = tokenRepository.findNotRevokedByUserEntity_Username(username);
        if (tokenList.isEmpty()) {
            return true;
        }

        for (RefreshToken token : tokenList) {
            if (refreshToken.equals(decrypt(token.getToken()))) {
                return false;
            }
        }

        return true;
    }

    private boolean isTokenRevoked(String refreshToken) {
        return containsKey("revoked_token:" + refreshToken.substring(7));
    }

    public void revokeToken_Logout(String token) {
        String email = decodeToken(token).getSubject();
        String key = "access_token:" + email;
        if (containsKey(key)) {
            deleteKey(key);
        }
        List<RefreshToken> notRevokedByUsername = tokenRepository.findNotRevokedByUserEntity_Username(email);
        for (RefreshToken refreshToken : notRevokedByUsername) {
            String decryptedTokenValue = decrypt(refreshToken.getToken());
            if (token.equals(decryptedTokenValue)) {
                refreshToken.setRevoked(true);
                setValue("revoked_token:" + decryptedTokenValue.substring(7), "true", 7, TimeUnit.DAYS);
                tokenRepository.save(refreshToken);
            }
        }
    }

    public void revokeAllTokens(String email) {
        deleteKey("access_token:" + email);
        List<RefreshToken> notRevokedByUsername = tokenRepository.findNotRevokedByUserEntity_Username(email);
        notRevokedByUsername.forEach(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
            setValue("revoked_token:" + decrypt(token.getToken()).substring(7), "true", 7, TimeUnit.DAYS);
        });
    }

}

