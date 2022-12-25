package com.blackout.scheduler.blackoutscheduler.telegram;

import com.blackout.scheduler.blackoutscheduler.telegram.event.BotEventDispatcher;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {

    private String botUserName;

    private String botToken;

    private final BotEventDispatcher eventDispatcher;

    public Bot(DefaultBotOptions options, String botUserName, String botToken, BotEventDispatcher eventDispatcher) {
        super(options);
        this.botUserName = botUserName;
        this.botToken = botToken;
        this.eventDispatcher = eventDispatcher;
    }


    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update event) {
        try {
            eventDispatcher.handle(this, event);
        } catch (TelegramApiException e) {
            System.err.println("Failed to process event: " + e.getMessage());
        }

    }
}
