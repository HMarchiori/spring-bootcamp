package henrique.corrales.bootcamp.services;

import henrique.corrales.bootcamp.data.security.AccountCredentialsDTO;
import henrique.corrales.bootcamp.data.security.TokenDTO;
import henrique.corrales.bootcamp.repositories.UserRepository;
import henrique.corrales.bootcamp.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public TokenDTO signIn(AccountCredentialsDTO credentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(), credentials.getPassword()
                )
        );

        var user = userRepository.findByUsername(credentials.getUsername());
        if (user == null) throw new UsernameNotFoundException("Invalid username");

        return jwtTokenProvider.createAccessToken(credentials.getUsername(), user.getRoles());
    }

    public TokenDTO refreshToken(String username, String authorizationHeader) {
        var user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("Username " + username + " not found!");
        return jwtTokenProvider.refreshToken(authorizationHeader); // aceita "Bearer <token>"
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO credentials) {
        return null;
    }
}