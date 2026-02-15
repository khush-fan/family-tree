package ru.fankhush.service;

import lombok.extern.slf4j.Slf4j;
import ru.fankhush.dao.PersonDao;
import ru.fankhush.dto.CreatePersonRequestDto;
import ru.fankhush.dto.FamilyTreeNodeDto;
import ru.fankhush.dto.PersonDto;
import ru.fankhush.mapper.PersonMapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PersonService {
    private final static PersonDao personDao = PersonDao.getInstance();

    public FamilyTreeNodeDto createPerson(CreatePersonRequestDto requestDto) {
        var person = PersonMapper.toEntity(requestDto);
        var savedPerson = personDao.save(person);
        log.info("Saved person: {}", savedPerson);
        if (savedPerson.getSpouseId() != null) {
            log.info("The person with id: {} have spouse", savedPerson.getId());
            marry(savedPerson.getId(), savedPerson.getSpouseId());
        }
        return PersonMapper.toFamilyTreeNodeDto(savedPerson);
    }

    public PersonDto getPersonById(Integer id) {
        return personDao.findById(id).map(PersonMapper::toPersonDto)
                .orElseThrow(() -> {
                            log.warn("Человек с ID {} не найден", id);
                            return new RuntimeException("Человек с данным ID: " + id + " не найден");
                        }
                );
    }

    public List<PersonDto> getAllPersons() {
        return personDao.findAll().stream()
                .map(PersonMapper::toPersonDto)
                .collect(Collectors.toList());
    }

    public List<FamilyTreeNodeDto> getFamilyTree() {
        return personDao.findAll().stream()
                .map(PersonMapper::toFamilyTreeNodeDto)
                .sorted(Comparator.comparing(FamilyTreeNodeDto::getId))
                .collect(Collectors.toList());
    }

    public PersonDto updatePerson(Integer id, PersonDto personDto) {
        if (!id.equals(personDto.getId())) {
            log.error("ID {} и {} не совпадают!", id, personDto.getId());
            throw new RuntimeException("ID не совпадают!");
        }
        System.out.println("dto при обновлении: " + personDto);
        log.info("update dto: {}", personDto);
        var person = PersonMapper.toEntity(personDto);
        personDao.update(person);
        return PersonMapper.toPersonDto(person);
    }

    public boolean deletePerson(Integer id) {
        personDao.clearReferences(id);
        return personDao.delete(id);
    }

    public List<PersonDto> findChildren(Integer parentId) {
        return personDao.findChildren(parentId).stream()
                .map(PersonMapper::toPersonDto)
                .collect(Collectors.toList());
    }

    public void marry(Integer personId1, Integer personId2) {
        var person2 = personDao.findById(personId2)
                .orElseThrow(() -> {
                    log.error("Человек с ID: {} не найден", personId2);
                    return new RuntimeException("Человек с ID: " + personId2 + " не найден");
                });

        person2.setSpouseId(personId1);
        personDao.update(person2);
    }

    public PersonDto addChild(Integer fatherId, Integer motherId, CreatePersonRequestDto requestDto) {
        var child = PersonMapper.toEntity(requestDto);
        child.setFatherId(fatherId);
        child.setMotherId(motherId);

        var savedPerson = personDao.save(child);

        return PersonMapper.toPersonDto(savedPerson);
    }
}
