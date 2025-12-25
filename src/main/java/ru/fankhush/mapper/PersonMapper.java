package ru.fankhush.mapper;

import ru.fankhush.dto.CreatePersonRequestDto;
import ru.fankhush.dto.FamilyTreeNodeDto;
import ru.fankhush.dto.PersonDto;
import ru.fankhush.entity.Gender;
import ru.fankhush.entity.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonMapper {
    public static PersonDto toPersonDto(Person person) {
        if (person == null) return null;

        return PersonDto.builder()
                .id(person.getId())
                .name(person.getName())
                .gender(person.getGender().name())
                .birthDate(person.getBirthDate())
                .photo(person.getPhoto())
                .fatherId(person.getFatherId())
                .motherId(person.getMotherId())
                .spouseId(person.getSpouseId())
                .build();
    }

    public static Person toEntity(PersonDto dto) {
        if (dto == null) return null;

        return Person.builder()
                .id(dto.getId())
                .name(dto.getName())
                .gender(Gender.valueOf(dto.getGender()))
                .birthDate(dto.getBirthDate())
                .fatherId(dto.getFatherId())
                .motherId(dto.getMotherId())
                .spouseId(dto.getSpouseId())
                .build();
    }

    public static Person toEntity(CreatePersonRequestDto dto) {
        if (dto == null) return null;

        Gender gender = null;
        if (dto.getGender() != null) {
            try {
                gender = Gender.valueOf(dto.getGender().toUpperCase());
            } catch (IllegalArgumentException e) {
                gender = Gender.MALE;
            }
        }

        return Person.builder()
                .name(dto.getName())
                .gender(gender != null ? gender : Gender.MALE)
                .birthDate(dto.getBirthDate())
                .photo(dto.getPhoto())
                .fatherId(dto.getFatherId())
                .motherId(dto.getMotherId())
                .spouseId(dto.getSpouseId())
                .build();
    }

    public static FamilyTreeNodeDto toFamilyTreeNodeDto(Person person) {
        if (person == null) return null;
        return FamilyTreeNodeDto.builder()
                .id(person.getId())
                .pid(person.getSpouseId())
                .mid(person.getMotherId())
                .fid(person.getFatherId())
                .name(person.getName())
                .born(formatDate(person.getBirthDate()))
                .gender(person.getGender() != null ? person.getGender().name().toLowerCase() : "")
                .image(person.getPhoto())
                .build();
    }

    public static FamilyTreeNodeDto familyTreeNodeDto(PersonDto dto){
        if (dto == null) return null;

        return FamilyTreeNodeDto.builder()
                .id(dto.getId())
                .pid(dto.getSpouseId())
                .mid(dto.getMotherId())
                .fid(dto.getFatherId())
                .name(dto.getName())
                .born(formatDate(dto.getBirthDate()))
                .gender(dto.getGender() != null ? dto.getGender().toLowerCase() : "")
                .image(dto.getPhoto())
                .build();
    }

    private static String formatDate(LocalDate birthDate) {
        if (birthDate == null) return null;
        return birthDate.format(DateTimeFormatter.ISO_DATE);
    }
}
