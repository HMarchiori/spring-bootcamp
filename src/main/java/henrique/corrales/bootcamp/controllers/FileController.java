package henrique.corrales.bootcamp.controllers;

import henrique.corrales.bootcamp.controllers.docs.FileControllerDocs;
import henrique.corrales.bootcamp.data.UploadFileResponseDTO;
import henrique.corrales.bootcamp.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file/v1")
public class FileController implements FileControllerDocs {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileStorageService service;

    public FileController(FileStorageService service) {
        this.service = service;
    }

    @PostMapping("/uploadFile")
    @Override
    public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
        var fileName = service.storeFile(file);
        var fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/v1/downloadFile/")
                .path(fileName)
                .toUriString();
        return new UploadFileResponseDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    @Override
    public List<UploadFileResponseDTO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        // A principal mudança foi esta parte, onde tratamos a lógica de upload dos múltiplos arquivos diretamente.
        return Arrays.stream(files)
                .map(file -> {
                    var fileName = service.storeFile(file); // Armazena cada arquivo.
                    var fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/file/v1/downloadFile/")
                            .path(fileName)
                            .toUriString();
                    // Retorna um DTO com os dados de cada arquivo processado.
                    return new UploadFileResponseDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
                })
                .collect(Collectors.toList()); // Coleta os resultados em uma lista.
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    @Override
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = service.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            logger.error("Could not determine file type!");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
