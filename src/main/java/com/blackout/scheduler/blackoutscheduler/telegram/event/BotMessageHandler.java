package com.blackout.scheduler.blackoutscheduler.telegram.event;

import com.blackout.scheduler.blackoutscheduler.entity.User;
import com.blackout.scheduler.blackoutscheduler.repo.UserRepo;
import com.blackout.scheduler.blackoutscheduler.telegram.Bot;
import com.blackout.scheduler.blackoutscheduler.telegram.constants.StatusConstants;
import com.blackout.scheduler.blackoutscheduler.telegram.service.UserActionService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static java.util.Objects.nonNull;

public class BotMessageHandler {

    private UserRepo userRepo;
    private UserActionService userActionService;

    public BotMessageHandler(UserRepo userRepo, UserActionService userActionService) {
        this.userRepo = userRepo;
        this.userActionService = userActionService;
    }


    public void handle(Bot bot, Update event) throws TelegramApiException {
        Message message = event.getMessage();
        System.out.println(message.getChatId());
        Optional<User> user = userRepo.findById(message.getChatId());
        String status = user.get().getStatus();
        if (nonNull(status)) {
            switch (status) {
                case StatusConstants.STREET_REQUEST -> userActionService.saveStreet(bot, event);
                case StatusConstants.HOUSE_REQUEST -> userActionService.saveHouse(bot, event);
                default -> isNotValid(bot, event);

            }
        } else {
            isNotValid(bot, event);
        }
    }

    private void isNotValid(Bot bot, Update event) throws TelegramApiException {
        SendMessage response = SendMessage.builder()
                .chatId(event.getMessage().getChatId().toString())
                .text("Эта команда не поддерживается")
                .build();
        bot.execute(response);
    }
}
