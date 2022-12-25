package com.blackout.scheduler.blackoutscheduler.telegram.event;

import com.blackout.scheduler.blackoutscheduler.telegram.Bot;
import com.blackout.scheduler.blackoutscheduler.telegram.constants.CallbackTextConstants;
import com.blackout.scheduler.blackoutscheduler.telegram.service.UserActionService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotCallbackHandler {

    private final UserActionService userActionService;


    public BotCallbackHandler(UserActionService userActionService) {
        this.userActionService = userActionService;
    }

    public void handle(Bot bot, Update event) throws TelegramApiException {
        CallbackQuery query = event.getCallbackQuery();
        String callbackData = query.getData();
        System.out.println(callbackData);
        switch (callbackData) {
            case CallbackTextConstants.REGISTRATION -> userActionService.register(bot, event);
            case CallbackTextConstants.CLEAR_DATA -> userActionService.unregister(bot, event);
            case CallbackTextConstants.SEND_SCREEN -> userActionService.sendScreen(bot, event);
            default -> {
            }
        }
    }
}
