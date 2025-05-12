package henrique.corrales.bootcamp.file.exporter.contract;

import henrique.corrales.bootcamp.data.PersonDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileExporter {
    Resource exportFile(List<PersonDTO> people) throws Exception;
}
