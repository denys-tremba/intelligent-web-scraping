package com.example.intelligentwebscrapping;

import io.micrometer.observation.ObservationRegistry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.FactCheckingEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FactCheckingTest {

    FactCheckingEvaluator factCheckingEvaluator;

    @BeforeAll
    void beforeAll() {
        OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");
        OllamaOptions options = OllamaOptions.builder()
                .withModel("bespoke-minicheck")
                .withNumPredict(2)
                .withTemperature(0.0d)
                .build();
        ChatModel chatModel = new OllamaChatModel(ollamaApi,
                options,
                null,
                null,
                ObservationRegistry.NOOP);
        factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(chatModel));
    }

    @Test
    void testFactChecking() {
        String context = "The father has three children: two sons and a daughter.";
        String claim = "The father has a daughter.";

        EvaluationRequest evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);

        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);
        assertTrue(evaluationResponse.isPass(), "The claim should be supported by the context");

        context = "The father has three children: two sons and a daughter.";
         claim = "The father has three sons.";

        // Create an EvaluationRequest
         evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);

        // Perform the evaluation
         evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);
        assertFalse(evaluationResponse.isPass(), "The claim should not be supported by the context");

    }

    @Test
    void test() {
        String context = "Cloze deletion is a sentence with its parts missing and replaced by three dots. Cloze deletion exercise is an exercise that uses cloze deletion to ask the student to fill in the gaps marked with the three dots. ";
        String claim = "The recall technique where a sentence with its parts missing and replaced by three dots is called \"Cloze deletion\"";
        Content content = createContent(context);
        EvaluationRequest evaluationRequest = new EvaluationRequest(Collections.singletonList(content), claim);
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);
        assertTrue(evaluationResponse.isPass(), "The claim should be supported by the context");
    }

    @NotNull
    private static Content createContent(String context) {
        return new Content() {

            @Override
            public String getContent() {
                return context;
            }

            @Override
            public Map<String, Object> getMetadata() {
                return Collections.emptyMap();
            }
        };
    }
}
