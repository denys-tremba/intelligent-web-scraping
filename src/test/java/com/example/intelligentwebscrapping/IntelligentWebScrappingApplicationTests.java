package com.example.intelligentwebscrapping;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Profile("test")
class IntelligentWebScrappingApplicationTests {
    @Qualifier("openAiChatModel")
    @Autowired
    ChatModel chatModel;
    @Autowired
    VectorStore vectorStore;
    @CsvFileSource(files = {"src/main/resources/qa.csv"},delimiter = ';', numLinesToSkip = 1)
    @ParameterizedTest
    void testEvaluation(String question, String answer) {
        String userText = question;
        ChatResponse response = ChatClient.builder(chatModel).build().prompt().advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults())).user(userText).call().chatResponse();
        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        List<Content> retrievedDocuments = response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        String responseContent = response.getResult().getOutput().getContent();
        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, retrievedDocuments, responseContent);

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response %s is not relevant to the query %s with context".formatted(responseContent, userText));

    }

}
