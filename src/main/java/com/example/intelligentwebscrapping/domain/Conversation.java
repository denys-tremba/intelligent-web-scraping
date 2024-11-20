package com.example.intelligentwebscrapping.domain;

import com.example.intelligentwebscrapping.infrastructure.ApplicationContextProvider;
import com.example.intelligentwebscrapping.infrastructure.ai.ArtificialIntelligenceFacade;
import com.example.intelligentwebscrapping.infrastructure.scrapping.UriScrappingFacade;

import java.net.URI;
import java.util.Locale;

public class Conversation {

    private Locale locale;
    private UserId userId;
    private URI uri;
    private boolean completed;
    private QuestionAnswerPairs questionAnswerPairs = new QuestionAnswerPairs();

    public Conversation(UserId userId) {
        this.userId = userId;
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

    public UserId getConversationId() {
        return userId;
    }

    public String getLocaleLanguage() {
        return locale.getLanguage();
    }
}
