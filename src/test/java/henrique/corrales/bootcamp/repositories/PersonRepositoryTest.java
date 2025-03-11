package henrique.corrales.bootcamp.repositories;

import henrique.corrales.bootcamp.integrationtests.testcontainers.AbstractIntegrationTest;
import henrique.corrales.bootcamp.models.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest extends AbstractIntegrationTest {


    @Autowired
    private PersonRepository personRepository;
    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(1)
    void findPeopleByName() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC,
                        "firstName"));
        person = personRepository.findPeopleByName("iko", pageable)
                .getContent().get(0);

        assertNotNull(person);
        assertNotNull(person.getId());

        assertEquals("Nikola", person.getFirstName());
        assertEquals("Tesla", person.getLastName());
        assertEquals("Smiljan - Croatia", person.getAddress());
        assertEquals("Male", person.getGender());
        assertTrue(person.getEnabled());

    }

    @Test
    @Order(2)
    void disablePerson() {

        Long id = person.getId();

        personRepository.disablePerson(id);
        var result = personRepository.findById(id);

        person = result.get();

        assertNotNull(person);
        assertNotNull(person.getId());

        assertEquals("Nikola", person.getFirstName());
        assertEquals("Tesla", person.getLastName());
        assertEquals("Smiljan - Croatia", person.getAddress());
        assertEquals("Male", person.getGender());
        assertFalse(person.getEnabled());
    }

}