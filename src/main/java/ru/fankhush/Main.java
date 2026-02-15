package ru.fankhush;

import ru.fankhush.server.Server;
import ru.fankhush.service.PersonService;

public class Main {
    public static void main(String[] args) {
        PersonService service = new PersonService();
        Server.main(args);
    }


}