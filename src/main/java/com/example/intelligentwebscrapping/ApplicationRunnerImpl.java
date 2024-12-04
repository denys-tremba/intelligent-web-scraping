package com.example.intelligentwebscrapping;

import com.example.intelligentwebscrapping.domain.*;
import com.example.intelligentwebscrapping.infrastructure.ai.EvaluatorImpl;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.*;
import org.springframework.ai.model.Content;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

//@Component
@Profile("!test")
public class ApplicationRunnerImpl implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger( ApplicationRunnerImpl.class );

    public static final String QUERY_SYSTEM = """
            	You are a chat support agent. You are an expert in understanding markdown files (especially CommonMark specification).
            	Your main task is to retrieve meaning from markdown and answer questions by referring to the context.
            	You should always refer to the context while providing strict and helpful answers.
            	If you do not know the exact answer you should respond with phrase "I do not possess requested information".
            """;
    private final ChatClient chatClient;
    private final Evaluator factCheckingEvaluator;
    private final EvaluatorImpl relevancyEvaluator;

    public ApplicationRunnerImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore)).defaultSystem(QUERY_SYSTEM).build();
        factCheckingEvaluator = new RelevancyEvaluator(chatClientBuilder);
        relevancyEvaluator = new EvaluatorImpl(chatClientBuilder);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Stats stats = new Stats();
        QuestionAnswerPairs pairs = loadQuestionAnswerPairs();
        for (QuestionAnswerPair pair : pairs.getQuestionAnswerPairs()) {
            logger.info("Before processing {}", pair.getQuestion());
            Question question = pair.getQuestion();
            ChatResponse chatResponse = chatClient.prompt().system(QUERY_SYSTEM).user(question.value()).call().chatResponse();
            String claim = chatResponse.getResult().getOutput().getContent();
            if (claim.contains("I do not possess")) {
                stats.incrementMissing();
                continue;
            }
            List<Content> context = chatResponse.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
            EvaluationRequest evaluationRequest = new EvaluationRequest(question.value(), context, claim);
            EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);
            if (!evaluationResponse.isPass()) {
                doWhenGroundedFactualityFailed(stats, claim, question.value(), pair.getAnswer().value());
            } else {
                stats.incrementAccuracy();
            }
            logger.info("Stats after processing {} : {}", pair.getQuestion(), stats.toString());
        }
        persistStatsToFileSystem(stats);
    }

    private void persistStatsToFileSystem(Stats stats) {
        try {
            Files.write(Path.of("stats.txt"), stats.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doWhenGroundedFactualityFailed(Stats stats, String actual, String userText, String expectedAnswer) {
        boolean response = relevancyEvaluator.evaluate(userText, actual, expectedAnswer);
        if (!response) {
            stats.incrementHallucination();
        }
    }

    private QuestionAnswerPairs loadQuestionAnswerPairs() throws URISyntaxException {
        URI uri = ApplicationRunnerImpl.class.getClassLoader().getResource("qa.csv").toURI();
        Path path = Path.of(uri);
        try (Stream<String> stream = Files.lines(path)) {
            List<QuestionAnswerPair> pairs = stream.skip(1).map(line -> {
                String[] split = line.split(";");
                Question question = new Question(split[0]);
                Answer answer = new Answer(split[1]);
                return new QuestionAnswerPair(question, answer);
            }).toList();
            QuestionAnswerPairs result = new QuestionAnswerPairs();
            result.setQuestionAnswerPairs(pairs);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
