package com.example.intelligentwebscrapping.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class QuestionAnswerPairs {
    @CollectionTable
    @ElementCollection
    private List<QuestionAnswerPair> questionAnswerPairs = new ArrayList<>();
    @Id
    @GeneratedValue
    private Long id;

    public Answer getAnswerForLastQuestion() {
        return questionAnswerPairs.get(questionAnswerPairs.size() - 1).getAnswer();
    }

    public void makeNewPair(Question question, Answer answer) {
        questionAnswerPairs.add(new QuestionAnswerPair(question, answer));

    }

    public List<QuestionAnswerPair> getQuestionAnswerPairs() {
        return questionAnswerPairs;
    }

    public void setQuestionAnswerPairs(List<QuestionAnswerPair> questionAnswerPairs) {
        this.questionAnswerPairs = questionAnswerPairs;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
