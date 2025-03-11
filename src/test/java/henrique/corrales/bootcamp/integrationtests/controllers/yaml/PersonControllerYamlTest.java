package henrique.corrales.bootcamp.integrationtests.controllers.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import henrique.corrales.bootcamp.config.TestConfigs;
import henrique.corrales.bootcamp.data.PersonDTO;
import henrique.corrales.bootcamp.data.wrappers.xml.PagedModelPerson;
import henrique.corrales.bootcamp.integrationtests.testcontainers.AbstractIntegrationTest;
import henrique.corrales.bootcamp.integrationtests.controllers.yaml.mapper.YAMLMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.config.name=application-test"
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;
    private static PersonDTO person;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
        objectMapper = new YAMLMapper();
        person = new PersonDTO();
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockPerson();
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_CORRECT)
                .setBasePath("/api/person/v1")
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var createdPerson = given().config(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig().
                                encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                ).spec(specification)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(person, objectMapper)
            .when()
                .post()
            .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
            .extract()
                .body()
                    .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);
        assertEquals("John", createdPerson.getFirstName());
        assertEquals("Doe", createdPerson.getLastName());
        assertEquals("1234 Main St", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
    }


    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {

        person.setFirstName("Marco");
        person.setLastName("Marchiori");
        person.setAddress("Los Angeles");
        person.setGender("Male");

        var createdPerson = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().
                                                encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                ).spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                    .body(person, objectMapper)
                .when()
                    .put()
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);
        assertEquals("Marco", createdPerson.getFirstName());
        assertEquals("Marchiori", createdPerson.getLastName());
        assertEquals("Los Angeles", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var createdPerson = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().
                                                encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                ).spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);
        assertEquals("Marco", createdPerson.getFirstName());
        assertEquals("Marchiori", createdPerson.getLastName());
        assertEquals("Los Angeles", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {

        var createdPerson = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().
                                                encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                ).spec(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);
        assertEquals("Marco", createdPerson.getFirstName());
        assertEquals("Marchiori", createdPerson.getLastName());
        assertEquals("Los Angeles", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertFalse(createdPerson.getEnabled());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {

        given(specification)
                .pathParam("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }


    @Test
    @Order(6)
    void findAllTest() throws JsonProcessingException {
        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .queryParams("page", 3, "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PagedModelPerson.class, objectMapper);

        List<PersonDTO> people = response.getContent();

        PersonDTO personOne = people.getFirst();
        person = personOne;

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Alyosha", personOne.getFirstName());
        assertEquals("Waterman", personOne.getLastName());
        assertEquals("Suite 35", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personTwo = people.get(1);
        person = personTwo;

        assertNotNull(personTwo.getId());
        assertTrue(personTwo.getId() > 0);

        assertEquals("Alyssa", personTwo.getFirstName());
        assertEquals("Notman", personTwo.getLastName());
        assertEquals("PO Box 93622", personTwo.getAddress());
        assertEquals("Female", personTwo.getGender());
        assertTrue(personTwo.getEnabled());
    }

    @Test
    @Order(7)
    void findByNameTest() throws JsonProcessingException {
        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("firstName", "tes")
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when()
                .get("findPeopleByName/{firstName}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PagedModelPerson.class, objectMapper);

        List<PersonDTO> people = response.getContent();

        PersonDTO personOne = people.getFirst();
        person = personOne;

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Gates", personOne.getFirstName());
        assertEquals("Pudsey", personOne.getLastName());
        assertEquals("Suite 84", personOne.getAddress());
        assertEquals("Female", personOne.getGender());
        assertFalse(personOne.getEnabled());
    }

    private void mockPerson() {
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("1234 Main St");
        person.setGender("Male");
        person.setEnabled(true);
    }

}
