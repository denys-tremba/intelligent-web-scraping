package com.example.intelligentwebscrapping.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {
}
