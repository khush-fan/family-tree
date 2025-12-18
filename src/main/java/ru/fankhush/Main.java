package ru.fankhush;

import ru.fankhush.entity.Gender;
import ru.fankhush.entity.Person;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        PersonDao personDao = PersonDao.getInstance();

//        Person person = Person.builder()
//                .name("Петров Алексей")
//                .gender(Gender.MALE)
//                .birthDate(LocalDate.of(2025,1,23))
//                .photo("photo1.png")
//                .fatherId(2)
//                .build();
//
//        personDao.save(person);
        personDao.delete(3);

    }
}