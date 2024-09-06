package com.example.intelligenttelegrambot.telegram;

import com.example.intelligenttelegrambot.customer.CustomerSupportAssistant;
import com.example.intelligenttelegrambot.customer.EtlPipelineService;
import com.example.intelligenttelegrambot.customer.Scraper;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

@Component
public class TelegramBotApiClient implements SpringLongPollingBot {
    public static final String SCRAPE = "scrape";
    private final TelegramClient telegramClient;

    private final String token;
    private final CustomerSupportAssistant supportAssistant;
    private final List<BotCommand> commands;
    private final EtlPipelineService etlPipelineService;
    Scraper scraper;

    public TelegramBotApiClient(@Value("${bot.token}") String token, CustomerSupportAssistant supportAssistant, EtlPipelineService etlPipelineService) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
        this.supportAssistant = supportAssistant;
        this.etlPipelineService = etlPipelineService;
        this.commands = List.of(new BotCommand(SCRAPE, SCRAPE));
        this.init();
        scraper = new Scraper();
    }

    private void init() {
        try {
            telegramClient.execute(new SetMyCommands(
                    commands
            ));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }


    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return new LongPollingUpdateConsumer() {
            @Override
            public void consume(List<Update> list) {
                list = List.of(list.get(list.size() - 1));

                for (Update update : list) {
//                    boolean command = isCommand(update);

                    if (update.getMessage().getText().startsWith("/scrape")) {
                        consumeCommand(SCRAPE, update.getMessage().getChatId(), update.getMessage().getText());
                    } else {
                        SendMessage map = map(update);
                        consume(map);
                    }
                }
            }

            private void handleCommand(Update update) {
                String text = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                commands.stream()
                        .map(BotCommand::getCommand)
                        .filter(command -> command.equals(text.substring(1)))
                        .findAny()
                        .ifPresentOrElse(c -> consumeCommand(c, chatId, text), () -> handleUnknownCommand(chatId));
            }

            private void handleUnknownCommand(Long chatId) {
                sendMessage(chatId, "Unknown command");
            }

            private void sendMessage(Long chatId, String text) {
                try {
                    telegramClient.execute(SendMessage.builder().chatId(chatId).text(text).build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            private void consumeCommand(String command, Long chatId, String text) {
                if (command.equals(SCRAPE)) {
                    Path path = scraper.scrape(URI.create(text));
                    etlPipelineService.performPipeline(path.toUri().toString());
                    sendMessage(chatId, text + " is successfully scraped into " + path.toString());
                } else {
                    handleUnknownCommand(chatId);
                }
            }

            private boolean isCommand(Update update) {
                return update.getMessage().isCommand();
            }

            private int sort(Update left, Update right) {
                return left.getUpdateId() - right.getUpdateId();
            }

            private void consume(SendMessage sendMessage) {
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            private SendMessage map(Update update) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                String reply = null;
                try {
                    reply = supportAssistant.chat(chatId.toString(), message.getText());
                } catch (NonTransientAiException e) {
                    return SendMessage.builder().chatId(chatId).text("Please try to send message again...").build();
                }
                return SendMessage.builder().chatId(chatId).text(reply).build();
            }
        };
    }
}
