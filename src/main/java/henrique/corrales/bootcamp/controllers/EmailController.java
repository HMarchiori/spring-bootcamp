package henrique.corrales.bootcamp.controllers;

import henrique.corrales.bootcamp.controllers.docs.EmailControllerDocs;
import henrique.corrales.bootcamp.data.request.EmailRequestDTO;
import henrique.corrales.bootcamp.services.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        service.sendSimpleEmail(emailRequest);
        return new ResponseEntity<>("Email sent with success!", HttpStatus.OK);
    }

    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequest,
            @RequestParam("attachment") MultipartFile multipartFile) {
        service.sendEmailWithAttachment(emailRequest, multipartFile);
        return new ResponseEntity<>("Email sent with success!", HttpStatus.OK);
    }
}
