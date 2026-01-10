package ru.fankhush.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import ru.fankhush.config.JacksonConfig;
import ru.fankhush.dto.CreatePersonRequestDto;
import ru.fankhush.dto.PersonDto;
import ru.fankhush.service.PersonService;

import java.util.HashMap;
import java.util.Map;

public class PersonController {
    private static final PersonService service = new PersonService();
    private static final ObjectMapper mapper = JacksonConfig.getMapper();

    public static void getAll(Context ctx) {
        try {
            var persons = service.getAllPersons();
            ctx.json(persons);
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Ошибка при получении списка", e));
        }
    }

    public static void getById(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            var person = service.getPersonById(id);
            ctx.json(person);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный формат ID", e));
        } catch (RuntimeException e) {
            ctx.status(HttpStatus.NOT_FOUND_404)
                    .json(errorResponse(e.getMessage(), e));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Ошибка при получении человека", e));
        }
    }

    public static void create(Context ctx) {
        try {
            var requestDto = mapper.readValue(ctx.body(), CreatePersonRequestDto.class);
            var createdPerson = service.createPerson(requestDto);
            ctx.status(HttpStatus.CREATED_201).json(createdPerson);
        } catch (JsonProcessingException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный формат json", e));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Ошибка при создании человека", e));
        }
    }

    public static void update(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            var personDto = mapper.readValue(ctx.body(), PersonDto.class);
            personDto.setId(id);
            var updatedPerson = service.updatePerson(id, personDto);
            ctx.json(updatedPerson);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный id", e));
        } catch (JsonProcessingException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный формат json", e));
        } catch (RuntimeException e) {
            ctx.status(HttpStatus.NOT_FOUND_404)
                    .json(errorResponse(e.getMessage(), e));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Ошибка при обновлении человека", e));
        }
    }

    public static void delete(Context ctx) {
        try {
            var id = Integer.parseInt(ctx.pathParam("id"));
            var deleted = service.deletePerson(id);
            if (deleted) {
                ctx.status(HttpStatus.NO_CONTENT_204);
            } else {
                ctx.status(HttpStatus.NOT_FOUND_404)
                        .json(Map.of("message", "Человек не найден"));
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный формат ID", e));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Ошибка при удалении", e));
        }
    }

    public static void getChildren(Context ctx) {
        try {
            var parentId = Integer.parseInt(ctx.pathParam("id"));
            var childs = service.findChildren(parentId);
            ctx.json(childs);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный формат ID", e));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Произошла ошибка при получении детей по ID родителя", e));
        }
    }

    public static void marry(Context ctx) {
        try {
            var person1Id = Integer.parseInt(ctx.pathParam("id"));

            var requestBody = mapper.readTree(ctx.body());

            if (!requestBody.has("spouseId")) {
                ctx.status(HttpStatus.BAD_REQUEST_400)
                        .json(Map.of("error", "Отсутствует spouseId в теле запроса"));
                return;
            }

            Integer person2Id = requestBody.get("spouseId").asInt();
            service.marry(person1Id, person2Id);

            ctx.json(
                    Map.of(
                            "message", "Супруги успешно созданы",
                            "person1Id", person1Id,
                            "person2Id", person2Id
                    )
            );
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный формат ID", e));
        } catch (JsonProcessingException e){
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .json(errorResponse("Неверный формат JSON", e));
        } catch (RuntimeException e){
            ctx.status(HttpStatus.NOT_FOUND_404)
                    .json(errorResponse(e.getMessage(), e));
        } catch (Exception e){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Произошла ошибка при установки замужества", e));
        }



    }

    private static Map<String, String> errorResponse(String message, Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        response.put("details", e.getMessage());
        return response;
    }
}
