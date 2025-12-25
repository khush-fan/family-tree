package ru.fankhush.controller;

import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import ru.fankhush.service.PersonService;

import java.util.HashMap;
import java.util.Map;

public class TreeController {
    private static final PersonService service = new PersonService();

    public static void getFamilyTree(Context ctx) {
        try {
            var treeNodes = service.getFamilyTree();
            Map<String, Object> response = new HashMap<>();
            response.put("nodes", treeNodes);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .json(Map.of(
                            "error", "Ошибка при получении",
                            "details", e.getMessage()
                    ));
        }

    }


}
