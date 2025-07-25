package com.example.intelligentwebscrapping;


import org.junit.jupiter.api.Disabled;
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
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled
class SelfAssessmentTest {
    @Autowired
    ChatModel chatModel;
    @Autowired
    VectorStore vectorStore;
    @ParameterizedTest
    @CsvFileSource(resources = "/qa.csv")
    void testEvaluation(String question, String answer) {


        String userText = question;
//        String userText = "Does average human need talent to learn new languages?";
        ChatResponse response = ChatClient.builder(chatModel).build().prompt().advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults())).user(userText).call().chatResponse();
        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        List<Content> retrievedDocuments = response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, retrievedDocuments, response.getResult().getOutput().getContent());

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the value");

    }

}
