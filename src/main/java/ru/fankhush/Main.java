package ru.fankhush;

import io.javalin.Javalin;
import ru.fankhush.entity.Gender;
import ru.fankhush.server.Server;
import ru.fankhush.service.PersonService;

public class Main {
    public static void main(String[] args) {
        PersonService service = new PersonService();
        Server.main(args);
    }
}