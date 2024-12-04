package com.example.intelligentwebscrapping.infrastructure.web;

import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.domain.UserId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.security.Principal;

@Controller
@RequestMapping("/conversation")
public class HttpController {
    IntelligentWebScrappingSystem intelligentWebScrappingSystem = new IntelligentWebScrappingSystem();

    @GetMapping
    public String getConversationCreationForm(Principal principal) {
        return "form";
    }

    @GetMapping("/chat")
    public String getConversationChatForm(Principal principal) {
        return "conversation";
    }

    @PostMapping("/create")
    public String startConversation(Principal principal, @RequestParam URI uri) {

        intelligentWebScrappingSystem.startConversation(new UserId(principal.getName()));
        intelligentWebScrappingSystem.enterUri(uri);
        return "redirect:/conversation/chat";
    }

    @ResponseBody
    @GetMapping("/ask")
    public String chat(@RequestParam String message) {
        intelligentWebScrappingSystem.askQuestion(message);
        return intelligentWebScrappingSystem.getLastAnswer().value();
    }
}
