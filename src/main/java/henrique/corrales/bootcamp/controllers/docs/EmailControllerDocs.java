package henrique.corrales.bootcamp.controllers.docs;

import henrique.corrales.bootcamp.data.request.EmailRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface EmailControllerDocs {


    @Operation(summary = "Send an Email",
                description = "Send an email to a person by providing details, subject and body.",
            tags = {"Email"},
            responses = {

                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            }
    )
    ResponseEntity<String> sendEmail(EmailRequestDTO emailRequestDTO);


    @Operation(summary = "Send an Email with Attachment",
            description = "Send an email with attachment to a person by providing details, subject and body.",
            tags = {"Email"},
            responses = {

                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            }
    )
    ResponseEntity<String> sendEmailWithAttachment(String emailRequestJson, MultipartFile multipartFile);


}
