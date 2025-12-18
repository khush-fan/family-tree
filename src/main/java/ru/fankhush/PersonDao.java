package ru.fankhush;

import ru.fankhush.entity.Gender;
import ru.fankhush.entity.Person;
import ru.fankhush.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonDao {
    private final static PersonDao INSTANCE = new PersonDao();
    private static final String SAVE_SQL = """
            INSERT INTO persons(name,gender, birth_date, photo,father_id, mother_id, spouse_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, name, gender, birth_date, photo, father_id, mother_id, spouse_id FROM persons
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM persons
            WHERE id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE persons
            SET name = ?,
                gender = ?,
                birth_date = ?,
                photo = ?,
                father_id = ?,
                mother_id = ?,
                spouse_id = ?
            """;

    private PersonDao() {
    }

    public static PersonDao getInstance() {
        return INSTANCE;
    }

    public Person save(Person person) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, person.getName());
            statement.setString(2, person.getGender().name());
            statement.setDate(3, Date.valueOf(person.getBirthDate()));
            statement.setString(4, person.getPhoto());
            setOptionalInt(statement, 5, Optional.ofNullable(person.getFatherId()));
            setOptionalInt(statement, 6, Optional.ofNullable(person.getMotherId()));
            setOptionalInt(statement, 7, Optional.ofNullable(person.getSpouseId()));
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if (keys.next())
                person.setId(keys.getInt("id"));

            return person;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении: " + e.getMessage(), e);
        }
    }

    public List<Person> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
        ) {
            List<Person> persons = new ArrayList<>();
            var result = statement.executeQuery();

            while (result.next()) {
                System.out.println(result);
                persons.add(buildPerson(result));
            }
            return persons;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выводе: " + e.getMessage(), e);
        }
    }

    public Optional<Person> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL);
        ) {
            statement.setInt(1, id);
            var result = statement.executeQuery();
            Person person = null;

            if (result.next())
                person = buildPerson(result);

            return Optional.ofNullable(person);

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске по id: " + e.getMessage(), e);
        }
    }

    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL);
        ) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении: " + e.getMessage(), e);
        }
    }

    public boolean update(Person person) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL);
        ) {
            statement.setString(1, person.getName());
            statement.setString(2, person.getGender().name());
            statement.setDate(3, Date.valueOf(person.getBirthDate()));

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении: " + e.getMessage(), e);
        }
    }

    private Person buildPerson(ResultSet result) throws SQLException {
        return new Person(
                result.getInt("id"),
                result.getString("name"),
                Gender.valueOf(result.getString("gender").trim()),
                result.getTimestamp("birth_date").toLocalDateTime().toLocalDate(),
                result.getString("photo"),
                result.getInt("father_id"),
                result.getInt("mother_id"),
                result.getInt("spouse_id")
        );
    }

    private void setOptionalInt(PreparedStatement statement, int index, Optional<Integer> value) throws SQLException {
        if (value != null && value.isPresent()) {
            statement.setInt(index, value.get());
        } else {
            statement.setNull(index, Types.INTEGER);
        }
    }

}
