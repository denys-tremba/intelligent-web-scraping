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

package com.example.intelligenttelegrambot.scrapping;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.ai.vectorstore.filter.antlr4.FiltersParser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

import static com.example.intelligenttelegrambot.scrapping.RagPipelineService.WEBSITE_CONTEXT_ROOT_KEY;
import static java.util.stream.Collectors.*;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;
import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.FILTER_EXPRESSION;
import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS;

/**
 * * @author Christian Tzolov
 */
@Service
public class ScrappingAssistant {
    private static final Logger logger = LoggerFactory.getLogger(ScrappingAssistant.class);
    private final ApplicationEventPublisher eventPublisher;

    private final ChatClient chatClient;
	private final VectorStore vectorStore;

	public ScrappingAssistant(ApplicationEventPublisher eventPublisher, ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory) {
        this.eventPublisher = eventPublisher;
		this.vectorStore = vectorStore;
		this.chatClient = modelBuilder
				.defaultSystem("""
						You are a chat support agent in a web site content understanding tasks.
						There is a context below. You MUST always refer to this context while providing
						strict and helpful answers. If you do not know the exact answer you MUST respond
						with phrase "I do not possess requested information".
						Today is {current_date}.
					""")
				.defaultAdvisors(
//						new PromptChatMemoryAdvisor(chatMemory),
//						new MessageChatMemoryAdvisor(chatMemory),
						new VectorStoreChatMemoryAdvisor(vectorStore),
					
						new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults().withTopK(2)), // RAG
						// new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()
						// 	.withFilterExpression("'documentType' == 'terms-of-service' && region in ['EU', 'US']")),
						
						new SimpleLoggerAdvisor()
				)
						
//				.defaultFunctions("getBookingDetails", "changeBooking", "cancelBooking") // FUNCTION CALLING

				.build();

    }

    public String chat(String chatId, String userMessageContent, String websiteHost) {
        eventPublisher.publishEvent(new PriorPromptProcessingEvent(this));

		ChatResponse chatResponse = this.chatClient.prompt()
				.system(s -> s.param("current_date", LocalDate.now().toString()))
				.user(userMessageContent)
				.advisors(a -> a
						.param(FILTER_EXPRESSION, WEBSITE_CONTEXT_ROOT_KEY + " == '" + websiteHost + "'")
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId + websiteHost)
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
				)
				.call()
				.chatResponse();

		String content = chatResponse.getResult().getOutput().getContent();
		return "%s Source of truth: %s".formatted(content, getSources(chatResponse));
    }

	@NotNull
	private String getSources(ChatResponse chatResponse) {
		List<Document> documents = chatResponse.getMetadata()
				.<List<Document>>get(RETRIEVED_DOCUMENTS);
		if (documents == null || documents.isEmpty()) {
			return "";
		}
		return documents.get(0).getMetadata().getOrDefault(RagPipelineService.SOURCE_OF_TRUTH_KEY,"").toString();
	}

}