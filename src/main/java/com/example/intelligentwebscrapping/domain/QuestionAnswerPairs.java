package com.example.intelligentwebscrapping.domain;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerPairs {
    private List<QuestionAnswerPair> questionAnswerPairs = new ArrayList<>();

    public Answer getAnswerForLastQuestion() {
        return questionAnswerPairs.get(questionAnswerPairs.size() - 1).getAnswer();
    }

    public void makeNewPair(Question question, Answer answer) {
        questionAnswerPairs.add(new QuestionAnswerPair(question, answer));

    }
}
