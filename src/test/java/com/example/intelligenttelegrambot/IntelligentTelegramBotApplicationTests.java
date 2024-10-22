package com.example.intelligenttelegrambot;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled
class IntelligentTelegramBotApplicationTests {
    @Autowired
    ChatModel chatModel;
    @Test
    void testEvaluation() {


        String userText = "What is the purpose of Carina?";
        EmbeddingModel embeddingModel = new TransformersEmbeddingModel();
        VectorStore vectorStore = new SimpleVectorStore(embeddingModel);
        ChatResponse response = ChatClient.builder(chatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .user(userText)
                .call()
                .chatResponse();

        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
                response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS), response.getResult().getOutput().getContent());

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

    }

}
