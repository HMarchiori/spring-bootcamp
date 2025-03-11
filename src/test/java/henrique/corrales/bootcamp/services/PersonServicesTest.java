package henrique.corrales.bootcamp.services;

import henrique.corrales.bootcamp.models.Person;
import henrique.corrales.bootcamp.repositories.PersonRepository;
import henrique.corrales.bootcamp.unittests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {

    private MockPerson input;

    @InjectMocks
    private PersonServices service;

    @Mock
    private PersonRepository repository;

    @BeforeEach
    void setUp() {
        input = new MockPerson();
    }

    @Test
    void findById_ShouldReturnPerson_WhenPersonExists() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        var result = service.findById(1L);

        assertNotNull(result);
        assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals("First Name Test1", result.getFirstName()),
                () -> assertEquals("Last Name Test1", result.getLastName()),
                () -> assertEquals("Female", result.getGender()),
                () -> assertTrue(result.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals("self")
                                && link.getHref().endsWith("/api/person/v1/1")
                                && link.getType().equals("GET"))
                )
        );
    }
}
