package com.example.intelligentwebscrapping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
@Embeddable
public final class Question {
    @Column(columnDefinition = "TEXT")
    private String QuestionValue;

    public Question(String QuestionValue) {
        this.QuestionValue = QuestionValue;
    }

    public Question() {

    }

    public String getQuestionValue() {
        return QuestionValue;
    }

    public void setQuestionValue(String value) {
        this.QuestionValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Question) obj;
        return Objects.equals(this.QuestionValue, that.QuestionValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(QuestionValue);
    }

    @Override
    public String toString() {
        return "Question[" +
                "value=" + QuestionValue + ']';
    }

}
