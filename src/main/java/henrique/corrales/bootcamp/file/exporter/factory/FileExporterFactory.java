package henrique.corrales.bootcamp.file.exporter.factory;

import henrique.corrales.bootcamp.exceptions.BadRequestException;
import henrique.corrales.bootcamp.file.exporter.MediaTypes;
import henrique.corrales.bootcamp.file.exporter.contract.PersonExporter;
import henrique.corrales.bootcamp.file.exporter.impl.CsvExporter;
import henrique.corrales.bootcamp.file.exporter.impl.XlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public PersonExporter getExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return context.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
        } else {
            throw new BadRequestException("Invalid File Format!");
        }
    }

}