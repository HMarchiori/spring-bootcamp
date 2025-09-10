package henrique.corrales.bootcamp.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import henrique.corrales.bootcamp.data.security.TokenDTO;
import henrique.corrales.bootcamp.exceptions.InvalidJwtAuthenticationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey =  "secret";

    @Value("${security.jwt.token.expire-length:36000000}")
    private long validityInMilliseconds =  3600000;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenDTO createAccessToken(String username, List<String> roles) {
        Date now =  new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String accessToken = getAccessToken(username, roles, now, validity);
        String refreshToken = getRefreshToken(username, roles, now);
        return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
    }

    private String getRefreshToken(String username, List<String> roles, Date now) {
        Date refreshTokenValidity = new  Date(now.getTime() + validityInMilliseconds);
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
        UserDetails userDetails = this.userDetailsService
                .loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm algo = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(algo).build();
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // vamos remover o Bearer do Token
        if ( bearerToken.isEmpty() || bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        } else {
            throw new InvalidJwtAuthenticationException("Invalid JWT Token");
        }
    }

    public boolean ValidateToken(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Invalid JWT Token");
        }
    }

}
