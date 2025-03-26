package henrique.corrales.bootcamp.controllers.docs;

import henrique.corrales.bootcamp.data.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File Endpoint")
public interface FileControllerDocs {

    @Operation(summary = "Uploads a file",
            tags = {"File Endpoint"},
            responses = {
                    @ApiResponse(description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UploadFileResponseDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            }
    )
    UploadFileResponseDTO uploadFile(MultipartFile file);

    @Operation(summary = "Uploads multiple files",
            tags = {"Files"},
            responses = {
                    @ApiResponse(description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UploadFileResponseDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            }
    )
    List<UploadFileResponseDTO> uploadMultipleFiles(MultipartFile[] files);

    @Operation(summary = "Downloads a file",
            tags = {"Files"},
            responses = {
                    @ApiResponse(description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UploadFileResponseDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            }
    )
    ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request);

}
