package henrique.corrales.bootcamp.file.importer.factory;

import henrique.corrales.bootcamp.exceptions.BadRequestException;
import henrique.corrales.bootcamp.file.importer.contract.FileImporter;
import henrique.corrales.bootcamp.file.importer.impl.CsvImporter;
import henrique.corrales.bootcamp.file.importer.impl.XlsxImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileImporterFactory {

    private final Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);
    private ApplicationContext context;

    public FileImporter getImporter(String fileName) {
        if (fileName.endsWith(".xlsx")) {
            return context.getBean(XlsxImporter.class);

        } else if (fileName.endsWith(".csv")) {
            return context.getBean(CsvImporter.class);

        } else {
            logger.error("Unsupported file type: {}", fileName);
            throw new BadRequestException();
        }
    }
}
