package com.blackout.scheduler.blackoutscheduler.telegram.service;

import com.blackout.scheduler.blackoutscheduler.entity.User;
import com.blackout.scheduler.blackoutscheduler.parser.WebPageParser;
import com.blackout.scheduler.blackoutscheduler.repo.UserRepo;
import com.blackout.scheduler.blackoutscheduler.telegram.Bot;
import com.blackout.scheduler.blackoutscheduler.telegram.constants.StatusConstants;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public class UserActionService {

    private UserRepo userRepo;
    private WebPageParser webPageParser;
    private String path;

    public UserActionService(UserRepo userRepo, WebPageParser webPageParser, String path) {
        this.userRepo = userRepo;
        this.webPageParser = webPageParser;
        this.path = path;
    }

    public void register(Bot bot, Update event) throws TelegramApiException {
        Long chatId = getChatId(event);
        Optional<User> user = userRepo.findById(chatId);
        User usr = new User();
        usr.setId(chatId);
        if (user.isEmpty()) {
            userRepo.save(usr);
        }
        if (user.get().getStreetName() == null) {
            user.get().setStatus(StatusConstants.STREET_REQUEST);
            userRepo.save(user.get());
            requestStreet(bot, chatId.toString());
        } else if (user.get().getHouseNumber() == null && user.get().getStreetName() != null) {
            user.get().setStatus(StatusConstants.HOUSE_REQUEST);
            userRepo.save(user.get());
            requestHouse(bot, chatId.toString());
        } else {
            user.get().setStatus(StatusConstants.COMPLETE);
            userRepo.save(user.get());
            getUserData(bot, chatId.toString(), user.get());
        }
    }

    public void sendScreen(Bot bot, Update event) throws TelegramApiException {
        Message message = event.getCallbackQuery().getMessage();
        makeScreenShot(event);
        bot.execute(SendPhoto.builder()
                .chatId(message.getChatId().toString())
                .photo(new InputFile(getFile(getFilePath(message.getChatId())), message.getChatId().toString()))
                .build());
    }

    public void unregister(Bot bot, Update event) throws TelegramApiException {
        Long chatId = getChatId(event);
        Optional<User> user = userRepo.findById(chatId);
        user.get().setStreetName(null);
        user.get().setHouseNumber(null);
        user.get().setStatus(null);
        userRepo.save(user.get());
        bot.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Ваши данные очищены")
                .build());
    }

    public void saveStreet(Bot bot, Update event) throws TelegramApiException {
        Optional<User> user = userRepo.findById(event.getMessage().getChatId());
        if (Objects.equals(user.get().getStatus(), StatusConstants.STREET_REQUEST)) {
            user.get().setStreetName(event.getMessage().getText());
            user.get().setStatus(StatusConstants.HOUSE_REQUEST);
            userRepo.save(user.get());
            register(bot, event);
        }
    }

    public void saveHouse(Bot bot, Update event) throws TelegramApiException {
        Optional<User> user = userRepo.findById(event.getMessage().getChatId());
        if (Objects.equals(user.get().getStatus(), StatusConstants.HOUSE_REQUEST)) {
            user.get().setHouseNumber(event.getMessage().getText());
            user.get().setStatus(StatusConstants.COMPLETE);
            userRepo.save(user.get());
            register(bot, event);
        }
    }

    private void requestStreet(Bot bot, String chatId) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Введите название улицы")
                .build());
    }

    private void requestHouse(Bot bot, String chatId) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Введите номер дома\nДома с буквой вводятся через /")
                .build());
    }

    private void getUserData(Bot bot, String chatId, User user) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Ваш адресс : " + user.getStreetName() + " " + user.getHouseNumber())
                .build());
    }

    private Long getChatId(Update event) {
        Long chatId;
        if (event.getCallbackQuery() != null) {
            chatId = event.getCallbackQuery().getMessage().getChatId();
        } else {
            chatId = event.getMessage().getChatId();
        }
        return chatId;
    }

    private InputStream getFile(String filePath) {
        File file = new File(filePath);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilePath(Long fileNameId) {
        StringBuilder sb = new StringBuilder(path);
        sb.append(fileNameId);
        sb.append(".jpeg");
        return sb.toString();
    }

    private String getFileName(Update event) {
        StringBuilder sb = new StringBuilder(event.getCallbackQuery().getMessage().getChatId().toString());
        sb.append(".jpeg");
        return sb.toString();
    }

    private void makeScreenShot(Update event) {
        webPageParser.parseWebPage(getStreetName(event), getHouseNumber(event), getFileName(event));
    }

    private String getStreetName(Update event) {
        Optional<User> user = userRepo.findById(event.getCallbackQuery().getMessage().getChatId());
        return user.get().getStreetName();
    }

    private String getHouseNumber(Update event) {
        Optional<User> user = userRepo.findById(event.getCallbackQuery().getMessage().getChatId());
        return user.get().getHouseNumber();
    }
}
