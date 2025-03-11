package henrique.corrales.bootcamp.services;

import henrique.corrales.bootcamp.controllers.PersonController;
import henrique.corrales.bootcamp.data.PersonDTO;
import henrique.corrales.bootcamp.exceptions.RequiredObjectIsNullException;
import henrique.corrales.bootcamp.exceptions.ResourceNotFoundException;
import henrique.corrales.bootcamp.mapper.custom.ObjectMapper;
import henrique.corrales.bootcamp.models.Person;
import henrique.corrales.bootcamp.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {

    private final Logger logger = LoggerFactory.getLogger(PersonServices.class);
    private final PersonRepository repository;
    private final PagedResourcesAssembler<PersonDTO> assembler;

    @Autowired
    public PersonServices(PersonRepository repository, PagedResourcesAssembler<PersonDTO> assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding all People!");

        Page<Person> people = repository.findAll(pageable);
        return getPagedModel(people);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String name, Pageable pageable) {
        logger.info("Finding People by name!");

        Page<Person> people = repository.findPeopleByName(name, pageable);
        return getPagedModel(people);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return convertToDtoWithLinks(entity);
    }

    public PersonDTO create(PersonDTO person) {
        if (person == null) {
            throw new RequiredObjectIsNullException("Required object is null.");
        }

        logger.info("Creating one Person!");
        Person entity = ObjectMapper.parseObject(person, Person.class);
        entity = repository.save(entity);
        return convertToDtoWithLinks(entity);
    }

    public PersonDTO update(PersonDTO person) {
        if (person == null) {
            throw new RequiredObjectIsNullException("It is not allowed to persist a null object!");
        }

        logger.info("Updating one Person!");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        entity = repository.save(entity);
        return convertToDtoWithLinks(entity);
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling one Person!");

        repository.disablePerson(id); // Atualiza no banco

        Person entity = repository.findById(id) // Busca a entidade atualizada
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        return convertToDtoWithLinks(entity);
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

    private PagedModel<EntityModel<PersonDTO>> getPagedModel(Page<Person> people) {
        var peopleWithLinks = people.map(person -> {
            PersonDTO dto = ObjectMapper.parseObject(person, PersonDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        return assembler.toModel(peopleWithLinks);
    }

    private PersonDTO convertToDtoWithLinks(Person entity) {
        PersonDTO dto = ObjectMapper.parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    private void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findall").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}