package com.example.intelligentwebscrapping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public final class Answer {
    @Column(columnDefinition = "TEXT")
    private String answerValue;

    public Answer(String answerValue) {
        this.answerValue = answerValue;
    }

    public Answer() {

    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String value) {
        this.answerValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Answer) obj;
        return Objects.equals(this.answerValue, that.answerValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerValue);
    }

    @Override
    public String toString() {
        return "Answer[" +
                "value=" + answerValue + ']';
    }

}
