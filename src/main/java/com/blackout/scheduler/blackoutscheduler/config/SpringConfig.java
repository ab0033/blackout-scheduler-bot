package com.blackout.scheduler.blackoutscheduler.config;


import com.blackout.scheduler.blackoutscheduler.factory.Driver;
import com.blackout.scheduler.blackoutscheduler.parser.WebPageParser;
import com.blackout.scheduler.blackoutscheduler.repo.UserRepo;
import com.blackout.scheduler.blackoutscheduler.telegram.Bot;
import com.blackout.scheduler.blackoutscheduler.telegram.constants.CallbackTextConstants;
import com.blackout.scheduler.blackoutscheduler.telegram.event.BotCallbackHandler;
import com.blackout.scheduler.blackoutscheduler.telegram.event.BotCommandHandler;
import com.blackout.scheduler.blackoutscheduler.telegram.event.BotEventDispatcher;
import com.blackout.scheduler.blackoutscheduler.telegram.event.BotMessageHandler;
import com.blackout.scheduler.blackoutscheduler.telegram.service.UserActionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class SpringConfig {

    @Bean
    public Driver driver() {
        return new Driver();
    }

    @Bean
    public WebPageParser webPageParser(Driver driver) {
        return new WebPageParser(driver);
    }

    @Bean
    public Bot bot(DefaultBotOptions defaultBotOptions,
                   BotEventDispatcher eventDispatcher,
                   @Value("${telegram.bot.token}") String botToken,
                   @Value("${telegram.bot.name}") String botUserName
    ) {
        return new Bot(defaultBotOptions, botUserName, botToken, eventDispatcher);
    }

    @Bean
    public BotSession botSession(Bot bot) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            return telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        return new DefaultBotOptions();
    }

    @Bean
    public BotEventDispatcher botEventDispatcher(BotCallbackHandler callbackHandler,
                                                 BotCommandHandler commandHandler,
                                                 BotMessageHandler messageHandler) {
        return new BotEventDispatcher(callbackHandler, commandHandler, messageHandler);
    }

    @Bean
    public UserActionService userRegistrationService(UserRepo userRepo, WebPageParser webPageParser, @Value("${file.path}") String path) {
        return new UserActionService(userRepo, webPageParser, path);
    }

    @Bean
    public BotCallbackHandler botCallbackHandler(UserActionService userActionService) {
        return new BotCallbackHandler(userActionService);
    }

    @Bean
    public BotCommandHandler botCommandHandler(InlineKeyboardMarkup keyboardMarkup) {
        return new BotCommandHandler(keyboardMarkup);
    }

    @Bean
    public BotMessageHandler botMessageHandler(UserRepo userRepo, UserActionService userActionService) {
        return new BotMessageHandler(userRepo, userActionService);
    }


    @Bean
    public InlineKeyboardMarkup keyboardMarkup() {
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        inlineKeyboardButtons.add(Collections.singletonList(
                new InlineKeyboardButton("Регистрация", null, CallbackTextConstants.REGISTRATION, null, null, null, null, null, null)
        ));
        inlineKeyboardButtons.add(Collections.singletonList(
                new InlineKeyboardButton("Отправить График", null, CallbackTextConstants.SEND_SCREEN, null, null, null, null, null, null)
        ));
        inlineKeyboardButtons.add(Collections.singletonList(
                new InlineKeyboardButton("Удалить данные", null, CallbackTextConstants.CLEAR_DATA, null, null, null, null, null, null)
        ));
        return new InlineKeyboardMarkup(inlineKeyboardButtons);
    }
}
