package henrique.corrales.bootcamp.file.importer.impl;

import henrique.corrales.bootcamp.data.PersonDTO;
import henrique.corrales.bootcamp.file.importer.contract.FileImporter;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class CsvImporter implements FileImporter {

    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        return List.of();
    }
}
