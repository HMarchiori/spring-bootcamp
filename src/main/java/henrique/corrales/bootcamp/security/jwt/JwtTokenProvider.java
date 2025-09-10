package henrique.corrales.bootcamp.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import henrique.corrales.bootcamp.data.security.TokenDTO;
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
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}") // 1h
    private long validityInMilliseconds;

    private final UserDetailsService userDetailsService;
    private Algorithm algorithm;
    private JWTVerifier verifier;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        // HMAC-SHA256 com a string do segredo (NÃO usar Base64 aqui)
        this.algorithm = Algorithm.HMAC256(secretKey);
        // Aceita 60s de leeway para clock skew
        this.verifier = JWT.require(algorithm).acceptLeeway(60).build();
    }

    public TokenDTO createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        String accessToken = buildAccessToken(username, roles, now, validity);
        String refreshToken = buildRefreshToken(username, roles, now);
        return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String authorizationHeaderWithBearer) {
        String token = extractBearer(authorizationHeaderWithBearer);
        DecodedJWT decoded = verifier.verify(token);
        String username = decoded.getSubject();
        List<String> roles = decoded.getClaim("roles").asList(String.class);
        return createAccessToken(username, roles);
    }

    private String buildRefreshToken(String username, List<String> roles, Date now) {
        Date refreshTokenValidity = new Date(now.getTime() + (validityInMilliseconds * 3));
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(refreshTokenValidity)
                .sign(algorithm);
    }

    private String buildAccessToken(String username, List<String> roles, Date now, Date validity) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withIssuer(issuerUrl)
                .sign(algorithm);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decoded = decodedToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(decoded.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.isBlank(header) || !header.startsWith("Bearer ")) return null;
        String token = header.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }

    private String extractBearer(String header) {
        if (StringUtils.isBlank(header)) return "";
        if (header.startsWith("Bearer ")) return header.substring("Bearer ".length()).trim();
        return header.trim();
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT decoded = decodedToken(token);
            Date exp = decoded.getExpiresAt();
            return exp == null || exp.after(new Date());
        } catch (Exception e) {
            // silencioso: inválido = false (não explode a request)
            return false;
        }
    }
}