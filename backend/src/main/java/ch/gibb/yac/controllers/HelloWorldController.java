package ch.gibb.yac.controllers;

import ch.gibb.yac.handlers.HelloWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @Autowired
    private HelloWebSocketHandler handler;


    @GetMapping("/")
    public String index() {
        this.handler.sendToAll("Hellllllllooooo!");
        return "Haloooo";
    }

    @GetMapping("/notify/{user}")
    public void notifyUser(@AuthenticationPrincipal(expression = "username") String thisUser, @PathVariable("user") String user) {
        handler.sendToUser(user, "Hello " + user + ", " + thisUser + " said hi!");
    }
}
