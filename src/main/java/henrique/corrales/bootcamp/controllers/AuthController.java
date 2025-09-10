package henrique.corrales.bootcamp.controllers;

import henrique.corrales.bootcamp.controllers.docs.AuthControllerDocs;
import henrique.corrales.bootcamp.data.security.AccountCredentialsDTO;
import henrique.corrales.bootcamp.data.security.TokenDTO;
import henrique.corrales.bootcamp.services.AuthService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint!")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signin")
    @Override
    public ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials) {
        if (credentialsIsInvalid(credentials))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        TokenDTO token = service.signIn(credentials);
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        return ResponseEntity.ok(token);
    }

    @PutMapping("/refresh/{username}")
    @Override
    public ResponseEntity<?> refreshToken(@PathVariable("username") String username,
                                          @RequestHeader("Authorization") String authorizationHeader) {
        if (parametersAreInvalid(username, authorizationHeader))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        TokenDTO token = service.refreshToken(username, authorizationHeader);
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/createUser",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<?> create(@RequestBody AccountCredentialsDTO credentials) {
        var created = service.create(credentials);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private boolean parametersAreInvalid(String username, String header) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(header);
    }

    private static boolean credentialsIsInvalid(AccountCredentialsDTO c) {
        return c == null || StringUtils.isBlank(c.getUsername()) || StringUtils.isBlank(c.getPassword());
    }
}