package com.example.intelligentwebscrapping.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class QuestionAnswerPair {
    private Question question;
    private Answer answer;

    public QuestionAnswerPair(Question question, Answer answer) {
        this.question = question;
        this.answer = answer;
    }

    public QuestionAnswerPair() {

    }

    public Answer getAnswer() {
        return answer;
    }

    public Question getQuestion() {
        return question;
    }
}
