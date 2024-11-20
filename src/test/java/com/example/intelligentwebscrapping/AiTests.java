//package com.example.intelligenttelegrambot;
//
//import io.micrometer.observation.ObservationRegistry;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.evaluation.EvaluationRequest;
//import org.springframework.ai.evaluation.EvaluationResponse;
//import org.springframework.ai.evaluation.FactCheckingEvaluator;
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.ollama.api.OllamaOptions;
//import org.springframework.ai.ollama.management.ModelManagementOptions;
//
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Disabled
//public class AiTests {
//    @Test
//    void testFactChecking() {
//        // Set up the Ollama API
//        OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");
//
//        OllamaOptions options = OllamaOptions.builder()
//                .withModel("bespoke-minicheck")
//                .withNumPredict(2)
//                .withTemperature(0.0d)
//                .build();
//        ChatModel chatModel = new OllamaChatModel(ollamaApi,
//                options,
//                null,
//                null,
//                ObservationRegistry.NOOP,
//                ModelManagementOptions.defaults());
//
//
//        // Create the FactCheckingEvaluator
//        var factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(chatModel));
//
//        // Example context and claim
//        String context = "The father has three children: two sons and a daughter.";
//        String claim = "The father has a daughter.";
//
//        // Create an EvaluationRequest
//        EvaluationRequest evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);
//
//        // Perform the evaluation
//        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);
//
//        assertTrue(evaluationResponse.isPass(), "The claim should be supported by the context");
//
//        context = "The father has three children: two sons and a daughter.";
//         claim = "The father has three sons.";
//
//        // Create an EvaluationRequest
//         evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);
//
//        // Perform the evaluation
//         evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);
//        assertFalse(evaluationResponse.isPass(), "The claim should not be supported by the context");
//
//    }
//}
