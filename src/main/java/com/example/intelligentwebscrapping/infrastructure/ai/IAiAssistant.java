package com.example.intelligentwebscrapping.infrastructure.ai;

import com.example.intelligentwebscrapping.domain.Answer;
import com.example.intelligentwebscrapping.domain.Conversation;
import com.example.intelligentwebscrapping.domain.Question;

public interface IAiAssistant {
    Answer chat(Conversation conversation, Question question);
}
