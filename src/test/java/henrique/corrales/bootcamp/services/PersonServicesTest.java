package henrique.corrales.bootcamp.services;

import henrique.corrales.bootcamp.data.PersonDTO;
import henrique.corrales.bootcamp.exceptions.RequiredObjectIsNullException;
import henrique.corrales.bootcamp.exceptions.ResourceNotFoundException;
import henrique.corrales.bootcamp.models.Person;
import henrique.corrales.bootcamp.repositories.PersonRepository;
import henrique.corrales.bootcamp.unittests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {

    private MockPerson input;

    @InjectMocks
    private PersonServices service;

    @Mock
    private PersonRepository repository;

    @Mock
    private PagedResourcesAssembler<PersonDTO> assembler;

    @Captor
    private ArgumentCaptor<Person> personCaptor;

    @BeforeEach
    void init() {
        input = new MockPerson();
        // NADA de MockitoAnnotations.openMocks(this); — já estamos com MockitoExtension
    }

    // ========= FIND BY ID =========
    @Test
    void findById_found_returnsDtoWithLinks() {
        Person entity = input.mockEntity(1);
        entity.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        PersonDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());

        assertHasLink(result, "self", "/api/person/v1/1", "GET");
        assertHasLink(result, "findAll", "/api/person/v1", "GET");
        assertHasLink(result, "create", "/api/person/v1", "POST");
        assertHasLink(result, "update", "/api/person/v1", "PUT");
        assertHasLink(result, "delete", "/api/person/v1/1", "DELETE");

        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findById_notFound_throwsResourceNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(repository).findById(99L);
        verifyNoMoreInteractions(repository);
    }

    // ========= CREATE =========
    @Test
    void create_success_mapsAndSaves_setsId_addsLinks() {
        PersonDTO dto = input.mockDTO(1);

        when(repository.save(any(Person.class))).thenAnswer(inv -> {
            Person p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PersonDTO result = service.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());

        assertHasLink(result, "self", "/api/person/v1/1", "GET");
        assertHasLink(result, "findAll", "/api/person/v1", "GET");
        assertHasLink(result, "create", "/api/person/v1", "POST");
        assertHasLink(result, "update", "/api/person/v1", "PUT");
        assertHasLink(result, "delete", "/api/person/v1/1", "DELETE");

        verify(repository).save(personCaptor.capture());
        Person saved = personCaptor.getValue();
        assertEquals("Address Test1", saved.getAddress());
        assertEquals("First Name Test1", saved.getFirstName());
        assertEquals("Last Name Test1", saved.getLastName());
        assertEquals("Female", saved.getGender());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void create_null_throwsRequiredObjectIsNullException() {
        RequiredObjectIsNullException ex =
                assertThrows(RequiredObjectIsNullException.class, () -> service.create(null));
        assertTrue(ex.getMessage().toLowerCase().contains("not allowed"));
        verifyNoInteractions(repository);
    }

    // ========= UPDATE =========
    @Test
    void update_success_findsEditsSaves_addsLinks() {
        Person existing = input.mockEntity(1);
        existing.setId(1L);

        PersonDTO dto = input.mockDTO(1);
        dto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

        PersonDTO result = service.update(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());

        assertHasLink(result, "self", "/api/person/v1/1", "GET");
        assertHasLink(result, "findAll", "/api/person/v1", "GET");
        assertHasLink(result, "create", "/api/person/v1", "POST");
        assertHasLink(result, "update", "/api/person/v1", "PUT");
        assertHasLink(result, "delete", "/api/person/v1/1", "DELETE");

        verify(repository).findById(1L);
        verify(repository).save(any(Person.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_null_throwsRequiredObjectIsNullException() {
        RequiredObjectIsNullException ex =
                assertThrows(RequiredObjectIsNullException.class, () -> service.update(null));
        assertTrue(ex.getMessage().toLowerCase().contains("not allowed"));
        verifyNoInteractions(repository);
    }

    @Test
    void update_notFound_throwsResourceNotFound() {
        PersonDTO dto = input.mockDTO(42);
        dto.setId(42L);
        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));
        verify(repository).findById(42L);
        verifyNoMoreInteractions(repository);
    }

    // ========= DELETE =========
    @Test
    void delete_success_findsAndDeletes() {
        Person entity = input.mockEntity(1);
        entity.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository).findById(1L);
        verify(repository).delete(personCaptor.capture());
        assertEquals(1L, personCaptor.getValue().getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete_notFound_throwsResourceNotFound() {
        when(repository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(7L));
        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    // ========= FIND ALL =========
    @Test
    void findAll_returnsPagedModelWithLinkedItems() {
        List<Person> entities = input.mockEntityList(); // 14 items
        Page<Person> page = new PageImpl<>(entities, PageRequest.of(0, 14), entities.size());
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        List<PersonDTO> dtoList = input.mockDTOList();
        for (int i = 0; i < dtoList.size(); i++) {
            int id = i;
            PersonDTO p = dtoList.get(i);
            p.setId((long) id);
            p.add(Link.of("/api/person/v1/" + id).withSelfRel().withType("GET"));
            p.add(Link.of("/api/person/v1").withRel("findAll").withType("GET"));
            p.add(Link.of("/api/person/v1").withRel("create").withType("POST"));
            p.add(Link.of("/api/person/v1").withRel("update").withType("PUT"));
            p.add(Link.of("/api/person/v1/" + id).withRel("delete").withType("DELETE"));
        }

        PagedModel<EntityModel<PersonDTO>> mockPaged =
                PagedModel.of(
                        dtoList.stream().map(EntityModel::of).collect(Collectors.toList()),
                        new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages())
                );

        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(mockPaged);

        PagedModel<EntityModel<PersonDTO>> result = service.findAll(PageRequest.of(0, 14));
        assertNotNull(result);
        assertEquals(14, result.getContent().size());

        List<PersonDTO> people = result.getContent().stream().map(EntityModel::getContent).collect(Collectors.toList());
        validateIndividualPerson(people.get(1), 1);
        validateIndividualPerson(people.get(4), 4);
        validateIndividualPerson(people.get(7), 7);

        verify(repository).findAll(any(Pageable.class));
        verify(assembler).toModel(any(Page.class), any(Link.class));
        verifyNoMoreInteractions(repository, assembler);
    }

    // ===== helpers =====

    private static void assertHasLink(RepresentationModel<?> model, String rel, String hrefSuffix, String type) {
        boolean found = model.getLinks().stream().anyMatch(link ->
                link.getRel().value().equals(rel)
                        && link.getHref().endsWith(hrefSuffix)
                        && type.equals(link.getType())
        );
        assertTrue(found, () -> "Expected link rel=" + rel + " type=" + type + " hrefEndsWith=" + hrefSuffix);
    }

    private static void validateIndividualPerson(PersonDTO person, int i) {
        assertNotNull(person);
        assertNotNull(person.getId());

        assertHasLink(person, "self",   "/api/person/v1/" + i, "GET");
        assertHasLink(person, "findAll","/api/person/v1",      "GET");
        assertHasLink(person, "create", "/api/person/v1",      "POST");
        assertHasLink(person, "update", "/api/person/v1",      "PUT");
        assertHasLink(person, "delete", "/api/person/v1/" + i, "DELETE");

        assertEquals("Address Test" + i, person.getAddress());
        assertEquals("First Name Test" + i, person.getFirstName());
        assertEquals("Last Name Test" + i, person.getLastName());
        assertEquals((i % 2 == 0) ? "Male" : "Female", person.getGender());
    }
}