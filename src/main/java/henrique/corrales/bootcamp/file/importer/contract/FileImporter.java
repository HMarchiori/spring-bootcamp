package henrique.corrales.bootcamp.file.importer.contract;

import henrique.corrales.bootcamp.data.PersonDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {
    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}
