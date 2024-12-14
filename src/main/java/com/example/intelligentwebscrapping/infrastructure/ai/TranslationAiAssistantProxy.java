package com.example.intelligentwebscrapping.infrastructure.ai;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import com.example.intelligentwebscrapping.domain.Answer;
import com.example.intelligentwebscrapping.domain.Conversation;
import com.example.intelligentwebscrapping.domain.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Lazy
public class TranslationAiAssistantProxy implements IAiAssistant{
    private static final Logger logger = LoggerFactory.getLogger( TranslationAiAssistantProxy.class );

    private final Translator translator;
    private final AiAssistant aiAssistant;
    private final String MODEL_LANGUAGE = "en-GB";

    public TranslationAiAssistantProxy(@Value("${deepl.api.key}") String apiKey, AiAssistant aiAssistant) {
        translator = new Translator(apiKey);
        this.aiAssistant = aiAssistant;
    }

    @Override
    public Answer chat(Conversation conversation, Question question) {
        try {
            return doChat(conversation, question);
        } catch (DeepLException | InterruptedException e) {
            throw new TranslationAiAssistantProxyException(e);
        }
    }

    private Answer doChat(Conversation conversation, Question question) throws DeepLException, InterruptedException {
        String localeLanguage = conversation.getLocaleLanguage();
        if (conversation.getLocale().getCountry().equals(Locale.forLanguageTag(MODEL_LANGUAGE).getCountry())) {
            logger.info("Translation skipper: {} == {}", localeLanguage, MODEL_LANGUAGE);
            return aiAssistant.chat(conversation, question);
        }
        TextResult result = translator.translateText(question.getQuestionValue(), null, MODEL_LANGUAGE);
        logger.info("Translated question from [{}]{} to [{}]{}", localeLanguage, question.getQuestionValue(), MODEL_LANGUAGE, result.getText());
        Answer answer = aiAssistant.chat(conversation, new Question(result.getText()));
        result = translator.translateText(answer.getAnswerValue(), null, result.getDetectedSourceLanguage());
        logger.info("Translated answer from [{}]{} to [{}]{}", MODEL_LANGUAGE, answer.getAnswerValue(), localeLanguage, result.getText());
        return new Answer(result.getText());
    }

    public static class TranslationAiAssistantProxyException extends RuntimeException {
        public TranslationAiAssistantProxyException(Throwable e) {
            super(e);
        }
    }
}
