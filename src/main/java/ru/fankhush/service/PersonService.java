package ru.fankhush.service;

import ru.fankhush.dao.PersonDao;
import ru.fankhush.dto.CreatePersonRequestDto;
import ru.fankhush.dto.FamilyTreeNodeDto;
import ru.fankhush.dto.PersonDto;
import ru.fankhush.mapper.PersonMapper;

import java.util.List;
import java.util.stream.Collectors;

public class PersonService {
    private final static PersonDao personDao = PersonDao.getInstance();

    public PersonDto createPerson(CreatePersonRequestDto requestDto) {
        var person = PersonMapper.toEntity(requestDto);
        var savedPerson = personDao.save(person);
        return PersonMapper.toPersonDto(savedPerson);
    }

    public PersonDto getPersonById(Integer id) {
        return personDao.findById(id).map(PersonMapper::toPersonDto)
                .orElseThrow(() -> new RuntimeException("Человек с данным ID: " + id + " не найден"));
    }

    public List<PersonDto> getAllPersons() {
        return personDao.findAll().stream()
                .map(PersonMapper::toPersonDto)
                .collect(Collectors.toList());
    }

    public List<FamilyTreeNodeDto> getFamilyTree() {
        return personDao.findAll().stream()
                .map(PersonMapper::toFamilyTreeNodeDto)
                .collect(Collectors.toList());
    }

    public PersonDto updatePerson(Integer id, PersonDto personDto) {
        if (!id.equals(personDto.getId())) {
            throw new RuntimeException("ID не совпадают!");
        }
        var person = PersonMapper.toEntity(personDto);
        personDao.update(person);
        return PersonMapper.toPersonDto(person);
    }

    public boolean deletePerson(Integer id) {
        return personDao.delete(id);
    }

    public List<PersonDto> findChildren(Integer parentId) {
        return personDao.findChildren(parentId).stream()
                .map(PersonMapper::toPersonDto)
                .collect(Collectors.toList());
    }

    public void marry(Integer personId1, Integer personId2) {
        var person1 = personDao.findById(personId1)
                .orElseThrow(() -> new RuntimeException("Человек с ID: " + personId1 + " не найден"));
        var person2 = personDao.findById(personId2)
                .orElseThrow(() -> new RuntimeException("Человек с ID: " + personId2 + " не найден"));

        person1.setSpouseId(personId2);
        person2.setSpouseId(personId1);

        personDao.update(person1);
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
