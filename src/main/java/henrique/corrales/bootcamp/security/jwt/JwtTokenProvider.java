package henrique.corrales.bootcamp.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import henrique.corrales.bootcamp.data.security.TokenDTO;
import henrique.corrales.bootcamp.exceptions.InvalidJwtAuthenticationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-length:3600000}") // <-- corrigido
    private long validityInMilliseconds = 3600000; // 1h

    private final UserDetailsService userDetailsService;
    private Algorithm algorithm;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        // Escolha UM padrão e mantenha. Aqui vou usar direto o segredo cru — simples e consistente.
        // Se quiser Base64, então NÃO re-base64-ize em outro lugar.
        // secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey); // usa a String (lib aceita), consistente everywhere
    }

    public TokenDTO createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        String accessToken = getAccessToken(username, roles, now, validity);
        String refreshToken = getRefreshToken(username, roles, now);
        return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String refreshToken) {
        String token = refreshTokenContainsBearer(refreshToken)
                ? refreshToken.substring("Bearer ".length()).trim()
                : StringUtils.defaultString(refreshToken);

        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return createAccessToken(username, roles);
    }

    private String getRefreshToken(String username, List<String> roles, Date now) {
        Date refreshTokenValidity = new Date(now.getTime() + (validityInMilliseconds * 3));
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(refreshTokenValidity)
                .withSubject(username)
                .sign(algorithm);
    }

    private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(username)
                .withIssuer(issuerUrl)
                .sign(algorithm);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        // Reusa o MESMO algorithm — 一把钥匙开一把锁
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isBlank(bearerToken) || !bearerToken.startsWith("Bearer ")) return null;
        String token = bearerToken.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }

    private static boolean refreshTokenContainsBearer(String refreshToken) {
        return StringUtils.isNotBlank(refreshToken) && refreshToken.startsWith("Bearer ");
    }

    public boolean validateToken(String token){
        try {
            DecodedJWT decodedJWT = decodedToken(token);
            Date exp = decodedJWT.getExpiresAt();
            return exp == null || exp.after(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or Invalid JWT Token!");
        }
    }
}