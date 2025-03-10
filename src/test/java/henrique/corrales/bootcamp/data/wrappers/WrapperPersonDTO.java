package henrique.corrales.bootcamp.data.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import henrique.corrales.bootcamp.data.PersonDTO;

import java.io.Serializable;

public class WrapperPersonDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("_embedded")
    private PersonEmbeddedDTO embeddedDTO;

    public WrapperPersonDTO() {}

    public PersonEmbeddedDTO getEmbeddedDTO() {
        return embeddedDTO;
    }

    public void setEmbeddedDTO(PersonEmbeddedDTO embeddedDTO) {
        this.embeddedDTO = embeddedDTO;
    }
}
