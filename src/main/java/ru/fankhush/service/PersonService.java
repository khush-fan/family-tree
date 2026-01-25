package ru.fankhush.service;

import ru.fankhush.dao.PersonDao;
import ru.fankhush.dto.CreatePersonRequestDto;
import ru.fankhush.dto.FamilyTreeNodeDto;
import ru.fankhush.dto.PersonDto;
import ru.fankhush.mapper.PersonMapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PersonService {
    private final static PersonDao personDao = PersonDao.getInstance();

    public FamilyTreeNodeDto createPerson(CreatePersonRequestDto requestDto) {
        var person = PersonMapper.toEntity(requestDto);
        var savedPerson = personDao.save(person);
        if(savedPerson.getSpouseId() != null){
            marry(savedPerson.getId(), savedPerson.getSpouseId());
        }
        return PersonMapper.toFamilyTreeNodeDto(savedPerson);
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
                .sorted(Comparator.comparing(FamilyTreeNodeDto::getId))
                .collect(Collectors.toList());
    }

    public PersonDto updatePerson(Integer id, PersonDto personDto) {
        if (!id.equals(personDto.getId())) {
            throw new RuntimeException("ID не совпадают!");
        }
        System.out.println("dto при обновлении: " + personDto);

        var person = PersonMapper.toEntity(personDto);
        System.out.println(person);

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
        var person2 = personDao.findById(personId2)
                .orElseThrow(() -> new RuntimeException("Человек с ID: " + personId2 + " не найден"));

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
