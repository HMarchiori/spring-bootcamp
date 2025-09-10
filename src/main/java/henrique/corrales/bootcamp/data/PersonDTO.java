package henrique.corrales.bootcamp.data;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Objects;

public class PersonDTO extends RepresentationModel<PersonDTO> implements Serializable {

    @JsonProperty("Identificação") @JsonAlias({"id", "Id"})
    private Long id;

    @JsonProperty("Nome") @JsonAlias({"firstName", "FirstName"})
    private String firstName;

    @JsonProperty("Sobrenome") @JsonAlias({"lastName", "LastName"})
    private String lastName;

    @JsonProperty("Endereco") @JsonAlias({"address", "Address", "endereco"})
    private String address;

    @JsonProperty("Genero") @JsonAlias({"gender", "Gender", "genero"})
    private String gender;

    @JsonProperty("Ligado") @JsonAlias({"enabled", "Enabled"})
    private Boolean enabled;

    @JsonAlias({"profileUrl", "profile_url"})
    private String profileUrl;

    @JsonAlias({"photoUrl", "photo_url"})
    private String photoUrl;

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

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(id, personDTO.id) && Objects.equals(firstName, personDTO.firstName) && Objects.equals(lastName, personDTO.lastName) && Objects.equals(address, personDTO.address) && Objects.equals(gender, personDTO.gender) && Objects.equals(enabled, personDTO.enabled) && Objects.equals(profileUrl, personDTO.profileUrl) && Objects.equals(photoUrl, personDTO.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, address, gender, enabled, profileUrl, photoUrl);
    }
}