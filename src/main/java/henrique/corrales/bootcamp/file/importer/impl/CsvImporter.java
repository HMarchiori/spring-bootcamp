package henrique.corrales.bootcamp.file.importer.impl;

import henrique.corrales.bootcamp.data.PersonDTO;
import henrique.corrales.bootcamp.file.importer.contract.FileImporter;

import java.io.InputStream;
import java.util.List;

public class CsvImporter implements FileImporter {
    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        return List.of();
    }
}
