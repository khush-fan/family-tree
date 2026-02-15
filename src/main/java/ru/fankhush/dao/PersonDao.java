package ru.fankhush.dao;

import lombok.extern.slf4j.Slf4j;
import ru.fankhush.entity.Gender;
import ru.fankhush.entity.Person;
import ru.fankhush.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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
                WHERE id = ?
            """;
    private static final String CHILD_CHILDREN_SQL = """
            SELECT * FROM persons
            WHERE father_id = ? OR mother_id = ?;
                        """;

    private static final String CLEAR_REFERENCES_SQL = """
                UPDATE persons
                    SET father_id = CASE WHEN father_id = ? THEN NULL ELSE father_id END,
                        mother_id = CASE WHEN mother_id = ? THEN NULL ELSE mother_id END,
                        spouse_id = CASE WHEN spouse_id = ? THEN NULL ELSE spouse_id END
                    WHERE father_id = ? OR mother_id = ? OR spouse_id = ?
            """;

    private PersonDao() {
    }

    public static PersonDao getInstance() {
        return INSTANCE;
    }

    public Person save(Person person) {
        log.debug("Сохранение узла: name={}, gender={}", person.getName(), person.getGender());
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
            log.error("Ошибка SQL при сохранении {}: {}", person.getName(), e.getMessage(), e);
            throw new RuntimeException("Ошибка при сохранении: " + e.getMessage(), e);
        }
    }

    public List<Person> findAll() {
        log.debug("Поиск всех узлов");
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
        ) {
            List<Person> persons = new ArrayList<>();
            var result = statement.executeQuery();

            while (result.next()) {
                persons.add(buildPerson(result));
            }

            return persons;

        } catch (SQLException e) {
            log.error("Ошибка при выводе: {}", e.getMessage());
            throw new RuntimeException("Ошибка при выводе: " + e.getMessage(), e);
        }
    }

    public Optional<Person> findById(Integer id) {
        log.debug("Поиск по id: {}", id);
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL);
        ) {
            statement.setInt(1, id);
            var result = statement.executeQuery();
            Person person = null;

            if (result.next()) {
                person = buildPerson(result);
            }
            log.debug("Результат поиска по id {}: {}", id, Optional.ofNullable(person));
            return Optional.ofNullable(person);

        } catch (SQLException e) {
            log.warn("Ошибка {} при поиске по id: {}", e.getMessage(), id);
            throw new RuntimeException("Ошибка при поиске по id: " + e.getMessage(), e);
        }
    }

    public boolean delete(Integer id) {
        log.debug("Удаление узла по id: {}", id);
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL);
        ) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.warn("Ошибка {} при удалении node:{}", e.getMessage(), id);
            throw new RuntimeException("Ошибка при удалении: " + e.getMessage(), e);
        }
    }

    public boolean update(Person person) {
        log.debug("Обновление персоны: id={}, name={}", person.getId(), person.getName());
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL);
        ) {
            statement.setString(1, person.getName());
            statement.setString(2, person.getGender().name());
            statement.setDate(3, Date.valueOf(person.getBirthDate()));
            statement.setString(4, person.getPhoto());
            setOptionalInt(statement, 5, Optional.ofNullable(person.getFatherId()));
            setOptionalInt(statement, 6, Optional.ofNullable(person.getMotherId()));
            setOptionalInt(statement, 7, Optional.ofNullable(person.getSpouseId()));
            statement.setInt(8, person.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Ошибка SQL при обновлении персоны id={}: {}", person.getId(), e.getMessage(), e);
            throw new RuntimeException("Ошибка при обновлении: " + e.getMessage(), e);
        }
    }


    public void clearReferences(Integer id) {
        log.debug("Удаление связей для id: {} перед удалением", id);
        try (
                var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(CLEAR_REFERENCES_SQL);
        ) {
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.setInt(3, id);
            statement.setInt(4, id);
            statement.setInt(5, id);
            statement.setInt(6, id);
            var result = statement.executeUpdate();
            log.info("Очищено ссылок: {}", result);
        } catch (SQLException e) {
            log.error("Ошибка удаления связей узла", e);
            throw new RuntimeException("Ошибка очистки: " + e.getMessage(), e);
        }
    }

    public List<Person> findChildren(Integer parentId) {
        List<Person> children = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(CHILD_CHILDREN_SQL);
        ) {
            statement.setInt(1, parentId);
            statement.setInt(2, parentId);
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                children.add(buildPerson(resultSet));
            }
            return children;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске детей: " + e.getMessage(), e);
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
        if (value != null && value.isPresent() && value.get() > 0) {
            statement.setInt(index, value.get());
        } else {
            statement.setNull(index, Types.INTEGER);
        }
    }

}
