package com.example.intelligentwebscrapping.domain;

import com.example.intelligentwebscrapping.infrastructure.ApplicationContextProvider;
import com.example.intelligentwebscrapping.infrastructure.ai.ArtificialIntelligenceFacade;
import com.example.intelligentwebscrapping.infrastructure.scrapping.UriScrappingFacade;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;

import java.net.URI;
import java.util.Locale;
@Entity
public class Conversation {
    @Id
    @GeneratedValue
    private Long conversationId;

    @Transient
    private Locale locale;
    @Convert(converter = UriConverter.class)
    private URI uri;
    private boolean completed;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private QuestionAnswerPairs questionAnswerPairs = new QuestionAnswerPairs();

    public Conversation() {
        this.locale = Locale.forLanguageTag("Uk-ua");
    }


    public void enterUri(URI uri) {
        this.uri = uri;
        ApplicationContextProvider.instance().applicationContext().getBean(UriScrappingFacade.class).scrape(uri);
    }

    public void askQuestion(String value) {
        Question question = new Question(value);
        Answer answer = ApplicationContextProvider.instance().applicationContext().getBean(ArtificialIntelligenceFacade.class).prompt(this, question);
        questionAnswerPairs.makeNewPair(question, answer);
    }

    public Answer getLastAnswer() {
        return questionAnswerPairs.getAnswerForLastQuestion();
    }

    public String getUriHost() {
        return uri.getHost();
    }

    public Locale getLocale() {
        return locale;
    }

    public void becomeComplete() {
        this.completed = true;
    }


    public String getLocaleLanguage() {
        return locale.getLanguage();
    }


    public QuestionAnswerPairs getQuestionAnswerPairs() {
        return questionAnswerPairs;
    }

    public void setQuestionAnswerPairs(QuestionAnswerPairs questionAnswerPairs) {
        this.questionAnswerPairs = questionAnswerPairs;
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}
