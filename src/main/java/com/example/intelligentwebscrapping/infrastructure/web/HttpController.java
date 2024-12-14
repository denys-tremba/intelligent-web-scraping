package com.example.intelligentwebscrapping.infrastructure.web;

import com.example.intelligentwebscrapping.domain.Conversation;
import com.example.intelligentwebscrapping.domain.ConversationRepository;
import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@Controller
@RequestMapping("/conversation")
public class HttpController {
    private final ConversationRepository conversationRepository;
    IntelligentWebScrappingSystem intelligentWebScrappingSystem = new IntelligentWebScrappingSystem();
    private static final Logger logger = LoggerFactory.getLogger( HttpController.class );


    public HttpController(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @GetMapping("/all")
    public String listAllConversations(Model model) {
        Iterable<Conversation> conversations = conversationRepository.findAll();
        model.addAttribute("conversations", conversations);
        return "conversations";
    }



    @GetMapping
    public String getConversationCreationForm(Principal principal) {
        return "form";
    }

    @GetMapping("/{id}")
    public String getConversationChatForm(Principal principal, @PathVariable String id, Model model) {
        Conversation conversation = conversationRepository.findById(Long.parseLong(id)).orElseThrow();
        model.addAttribute("conversation", conversation);
        intelligentWebScrappingSystem.setConversation(conversation);
        return "conversation";
    }

    @PostMapping("/{id}/delete")
    public String deleteConversation(Principal principal, @PathVariable String id, Model model) {
        Conversation conversation = conversationRepository.findById(Long.parseLong(id)).orElseThrow();
        model.addAttribute("conversation", conversation);
        conversationRepository.delete(conversation);
        return "redirect:/conversation/all";
    }

    @PostMapping("/create")
    public String startConversation(Principal principal, @RequestParam URI uri) {

        intelligentWebScrappingSystem.startConversation();
        intelligentWebScrappingSystem.enterUri(uri);
        Conversation conversation = intelligentWebScrappingSystem.getConversation();
        conversationRepository.save(conversation);
        logger.info("Conversation {} saved", conversation);
        return "redirect:/conversation/" + conversation.getConversationId();
    }

    @ResponseBody
    @GetMapping("/ask")
    public String chat(@RequestParam String message) {
        intelligentWebScrappingSystem.askQuestion(message);
        conversationRepository.save(intelligentWebScrappingSystem.getConversation());
        return intelligentWebScrappingSystem.getLastAnswer().getAnswerValue();
    }
}
