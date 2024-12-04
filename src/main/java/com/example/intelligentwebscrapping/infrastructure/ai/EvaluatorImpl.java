package com.example.intelligentwebscrapping.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.Evaluator;

public class EvaluatorImpl {
    private static final String DEFAULT_EVALUATION_PROMPT_TEXT = """
			    Your task is to evaluate if the actual response under [actual] for the query under [query]
			    is in line with the expected response under [expected].\\n
			    You have two options to answer. Either YES/ NO.\\n
			    Answer - YES, if the response for the query
			    is in line with the expected response otherwise NO.\\n
			    [query]: \\n {query}\\n
			    [actual]: \\n {actual}\\n
			    [expected]: \\n {expected}\\n
			    Answer: "
			""";

    private final ChatClient.Builder chatClientBuilder;

    public EvaluatorImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }
    public boolean evaluate(String userText, String responseContent, String expected) {
        String evaluationResponse = this.chatClientBuilder.build()
                .prompt()
                .user(userSpec -> userSpec.text(DEFAULT_EVALUATION_PROMPT_TEXT)
                        .param("query", userText)
                        .param("actual", responseContent)
                        .param("expected", expected))
                .call()
                .content();
        return evaluationResponse.toLowerCase().contains("yes");
    }
}
