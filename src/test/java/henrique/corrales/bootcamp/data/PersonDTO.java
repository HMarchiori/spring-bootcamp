package henrique.corrales.bootcamp.data;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "Person")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({"Identificação", "Nome", "Sobrenome", "Endereco", "Genero", "Ligado", "links"})
public class PersonDTO extends RepresentationModel<PersonDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Identificação")
    @JsonProperty("Identificação")
    private Long id;

    @XmlElement(name = "Nome")
    @JsonProperty("Nome")
    private String firstName;

    @XmlElement(name = "Sobrenome")
    @JsonProperty("Sobrenome")
    private String lastName;

    @XmlElement(name = "Endereco")
    @JsonProperty("Endereco")
    private String address;

    @XmlElement(name = "Genero")
    @JsonProperty("Genero")
    private String gender;

    @XmlElement(name = "Ligado")
    @JsonProperty("Ligado")
    private Boolean enabled;

    @JacksonXmlElementWrapper(useWrapping = false) // Evita um wrapper XML desnecessário
    @XmlElement(name = "link")
    @JsonProperty("links")
    public List<Link> getXmlLinks() {
        return super.getLinks().toList();
    }

    public PersonDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(id, personDTO.id) &&
                Objects.equals(firstName, personDTO.firstName) &&
                Objects.equals(lastName, personDTO.lastName) &&
                Objects.equals(address, personDTO.address) &&
                Objects.equals(gender, personDTO.gender) &&
                Objects.equals(enabled, personDTO.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, address, gender, enabled);
    }
}
