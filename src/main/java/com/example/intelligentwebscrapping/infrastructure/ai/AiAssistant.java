/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.intelligentwebscrapping.infrastructure.ai;

import com.example.intelligentwebscrapping.domain.Answer;
import com.example.intelligentwebscrapping.domain.Conversation;
import com.example.intelligentwebscrapping.domain.Question;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.example.intelligentwebscrapping.infrastructure.ai.EtlPipeline.WEBSITE_CONTEXT_ROOT_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;
import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.FILTER_EXPRESSION;
import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS;

@Service
public class AiAssistant implements IAiAssistant {
    private static final Logger logger = LoggerFactory.getLogger(AiAssistant.class);

	private final ChatClient chatClient;
	private final VectorStore vectorStore;
	private final ChatMemory chatMemory;

	public AiAssistant(ChatClient.Builder modelBuilder,
					   VectorStore vectorStore,
					   ChatMemory chatMemory) {
		this.chatMemory = chatMemory;
		this.vectorStore = vectorStore;
		this.chatClient = modelBuilder
				.defaultSystem("""
				Answer the question based on the context below.
            	Context has format of markdown file snippets (especially CommonMark specification).
            	Keep the answer short and concise.
            	Respond "I do not possess requested information" if not sure about the answer.
					""")
				.defaultAdvisors(
//						new SimpleLoggerAdvisor(),
//						new PromptChatMemoryAdvisor(chatMemory)
						new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults().withTopK(3))
				)
				.build();

    }

	public void eraseConversation(String chatId) {
		chatMemory.clear(chatId);
	}

    @Override
	public Answer chat(Conversation conversation, Question question) {
		ChatResponse chatResponse = this.chatClient.prompt()
				.system(s -> s.param("current_date", LocalDate.now().toString()))
				.user(question.getQuestionValue())
				.advisors(a -> a
						.param(FILTER_EXPRESSION, WEBSITE_CONTEXT_ROOT_KEY + " == '" + conversation.getUriHost() + "'")
//						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversation.getConversationId().value())
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
				)
				.call()
				.chatResponse();

		String content = chatResponse.getResult().getOutput().getContent();
		return new Answer("%s Source of truth: %s".formatted(content, getSources(chatResponse)));
    }

	@NotNull
	private String getSources(ChatResponse chatResponse) {
		List<Document> documents = chatResponse.getMetadata()
				.get(RETRIEVED_DOCUMENTS);
		if (documents == null || documents.isEmpty()) {
			return "";
		}
		return documents.get(0).getMetadata().getOrDefault(EtlPipeline.SOURCE_OF_TRUTH_KEY,"").toString();
	}

}