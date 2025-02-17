package henrique.corrales.bootcamp.data;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import henrique.corrales.bootcamp.serializer.GenderSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JsonPropertyOrder({"Identificação","Data de Aniversário", "Nome", "Sobrenome", "Endereço", "Gênero", "Telefone"})
@JsonFilter("PersonFilter")
public class PersonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sensitiveData;

    @JsonProperty("Identificação")
    private Long id;

    @JsonProperty("Nome")
    private String firstName;

    @JsonProperty("Sobrenome")
    private String lastName;

    @JsonProperty("Telefone")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String phoneNumber;

    @JsonProperty("Endereço")
    private String address;

    @JsonProperty("Gênero")
    @JsonSerialize(using = GenderSerializer.class)
    private String gender;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("Data de Aniversário")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Date birthDay;

    public PersonDTO() {}

    public String getSensitiveData() {
        return sensitiveData;
    }

    public void setSensitiveData(String sensitiveData) {
        this.sensitiveData = sensitiveData;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(sensitiveData, personDTO.sensitiveData) && Objects.equals(id, personDTO.id) && Objects.equals(firstName, personDTO.firstName) && Objects.equals(lastName, personDTO.lastName) && Objects.equals(phoneNumber, personDTO.phoneNumber) && Objects.equals(address, personDTO.address) && Objects.equals(gender, personDTO.gender) && Objects.equals(birthDay, personDTO.birthDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensitiveData, id, firstName, lastName, phoneNumber, address, gender, birthDay);
    }
}


