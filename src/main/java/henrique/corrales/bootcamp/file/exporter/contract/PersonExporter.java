package henrique.corrales.bootcamp.file.exporter.contract;

import henrique.corrales.bootcamp.data.PersonDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface PersonExporter {
    Resource exportPeople(List<PersonDTO> people) throws Exception;
}
