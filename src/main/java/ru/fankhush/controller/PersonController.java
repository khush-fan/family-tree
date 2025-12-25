package ru.fankhush.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import ru.fankhush.config.JacksonConfig;
import ru.fankhush.service.PersonService;

import java.util.HashMap;
import java.util.Map;

public class PersonController {
    private static final PersonService service = new PersonService();
    private static final ObjectMapper mapper = JacksonConfig.getMapper();

    public static void getAll(Context ctx){
        try{
            var persons = service.getAllPersons();
            ctx.json(persons);
        } catch (Exception e){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(errorResponse("Ошибка при получении списка", e));
        }
    }



    private static Map<String, String> errorResponse(String message, Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        response.put("details", e.getMessage());
        return response;
    }


}
