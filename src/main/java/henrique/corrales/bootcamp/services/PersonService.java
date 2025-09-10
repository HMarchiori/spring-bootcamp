package henrique.corrales.bootcamp.services;

import henrique.corrales.bootcamp.controllers.PersonController;
import henrique.corrales.bootcamp.data.PersonDTO;
import henrique.corrales.bootcamp.exceptions.RequiredObjectIsNullException;
import henrique.corrales.bootcamp.exceptions.ResourceNotFoundException;
import henrique.corrales.bootcamp.file.exporter.contract.PersonExporter;
import henrique.corrales.bootcamp.file.exporter.factory.FileExporterFactory;
import henrique.corrales.bootcamp.file.importer.contract.FileImporter;
import henrique.corrales.bootcamp.file.importer.factory.FileImporterFactory;
import henrique.corrales.bootcamp.models.Person;
import henrique.corrales.bootcamp.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    private final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository repository;
    private final PagedResourcesAssembler<PersonDTO> assembler;
    private final FileImporterFactory importer;
    private final FileExporterFactory exporter;

    public PersonService(PersonRepository repository,
                         PagedResourcesAssembler<PersonDTO> assembler,
                         FileExporterFactory exporter,
                         FileImporterFactory importer) {
        this.repository = repository;
        this.assembler = assembler;
        this.importer = importer;
        this.exporter = exporter;
    }

    // ========= Public API =========

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding all People!");
        Page<Person> people = repository.findAll(pageable);
        return getPagedModel(people, pageable);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting a People page!");
        var people = repository.findAll(pageable)
                .map(this::toDto)
                .getContent();

        try {
            PersonExporter exp = this.exporter.getExporter(acceptHeader);
            return exp.exportPeople(people);
        } catch (Exception e) {
            throw new RuntimeException("Export failure", e);
        }
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String name, Pageable pageable) {
        logger.info("Finding People by name!");
        Page<Person> people = repository.findPeopleByName(name, pageable);
        return getPagedModel(people, pageable);
    }

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("Importing people from file!");

        List<Person> entities;
        try (InputStream stream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(RequiredObjectIsNullException::new);
            FileImporter fileImporter = this.importer.getImporter(fileName);

            entities = fileImporter.importFile(stream).stream()
                    .map(this::toEntity)              // garante profileUrl/photoUrl/address/enabled
                    .map(repository::save)
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entities.stream().map(p -> {
            var dto = toDto(p);
            addHateoasLinks(dto);
            return dto;
        }).toList();
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return convertToDtoWithLinks(entity);
    }

    public PersonDTO create(PersonDTO person) {
        if (person == null) throw new RequiredObjectIsNullException("Required object is null.");
        logger.info("Creating one Person!");

        Person entity = toEntity(person);
        requireAddress(entity); // NOT NULL guard
        entity = repository.save(entity);

        return convertToDtoWithLinks(entity);
    }

    public PersonDTO update(PersonDTO person) {
        if (person == null) throw new RequiredObjectIsNullException("It is not allowed to persist a null object!");
        logger.info("Updating one Person!");

        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        // Atualiza todos os campos, inclusive os novos
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        if (person.getEnabled() != null) entity.setEnabled(person.getEnabled());
        entity.setProfileUrl(person.getProfileUrl());
        entity.setPhotoUrl(person.getPhotoUrl());

        requireAddress(entity); // NOT NULL guard
        entity = repository.save(entity);
        return convertToDtoWithLinks(entity);
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling one Person!");
        repository.disablePerson(id);
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return convertToDtoWithLinks(entity);
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

    // ========= Private: mapping, HATEOAS, guards =========

    private void requireAddress(Person p) {
        if (p.getAddress() == null || p.getAddress().isBlank()) {
            throw new RequiredObjectIsNullException("Address is required.");
        }
    }

    private Person toEntity(PersonDTO dto) {
        if (dto == null) return null;
        Person p = new Person();
        p.setId(dto.getId());
        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setAddress(dto.getAddress());
        p.setGender(dto.getGender());
        // default elegante: se vier nulo, mantenha true
        p.setEnabled(Objects.requireNonNullElse(dto.getEnabled(), Boolean.TRUE));
        p.setProfileUrl(dto.getProfileUrl());
        p.setPhotoUrl(dto.getPhotoUrl());
        return p;
    }

    private PersonDTO toDto(Person entity) {
        if (entity == null) return null;
        PersonDTO dto = new PersonDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setAddress(entity.getAddress());
        dto.setGender(entity.getGender());
        dto.setEnabled(entity.getEnabled());
        dto.setProfileUrl(entity.getProfileUrl());
        dto.setPhotoUrl(entity.getPhotoUrl());
        return dto;
    }

    private PagedModel<EntityModel<PersonDTO>> getPagedModel(Page<Person> people, Pageable pageable) {
        var peopleWithLinks = people.map(p -> {
            PersonDTO dto = toDto(p);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(PersonController.class)
                                .findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort())))
                .withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    private PersonDTO convertToDtoWithLinks(Person entity) {
        PersonDTO dto = toDto(entity);
        addHateoasLinks(dto);
        return dto;
    }

    private void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).findByName(dto.getFirstName(), 1, 12, "asc")).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(PersonController.class)
                .exportPage(1, 12, "asc", null))
                .withRel("exportPage").withType("GET").withTitle("Export People"));
    }
}