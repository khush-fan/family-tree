package ru.fankhush.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fankhush.dao.PersonDao;
import ru.fankhush.dto.CreatePersonRequestDto;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    private CreatePersonRequestDto requestDto;

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonDao personDaoMock;

    @Test
    void createPerson() {
    }

    @Test
    void getPersonById_ShouldReturnPersonDto_() {
    }

    @Test
    void getAllPersons() {
    }

    @Test
    void getFamilyTree() {
    }

    @Test
    void updatePerson() {
    }

    @Test
    void deletePerson() {
    }

    @Test
    void findChildren() {
    }

    @Test
    void marry() {
    }
}