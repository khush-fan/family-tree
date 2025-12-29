package ru.fankhush.server;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;
import ru.fankhush.controller.PersonController;
import ru.fankhush.controller.TreeController;

import java.util.Map;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        Javalin app = createApp();
        setupRoutes(app);
        app.start(PORT);
    }

    private static Javalin createApp() {
        return Javalin.create(config -> {
//            config.plugins.enableCors( cors -> {
//                cors.add(CorsPluginConfig::anyHost);
//            });


            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });

            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
                staticFiles.precompress = false;
            });

            config.bundledPlugins.enableDevLogging();
        }).exception(Exception.class, ((exception, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of(
                    "error", "Внутрення ошибка сервера",
                    "message", exception.getMessage() != null ? exception.getMessage() : "Неизвестная ошибка"
            ));
        })).error(404, ctx -> {
            ctx.json(Map.of(
                    "error", "Ресурс не найден",
                    "path", ctx.path()
            ));
        });
    }


    public static Javalin setupRoutes(Javalin app) {
        app.get("/persons", PersonController::getAll);
        app.get("/persons/{id}", PersonController::getById);
        app.post("/persons", PersonController::create);
        app.put("/persons/{id}", PersonController::update);
        app.delete("/persons/{id}", PersonController::delete);
        app.get("/persons/{id}/children", PersonController::getChildren);
        app.post("/persons/{id}/marry", PersonController::marry);
        app.get("/tree", TreeController::getFamilyTree);
        return app;
    }
}
