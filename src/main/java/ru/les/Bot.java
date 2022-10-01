package ru.les;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.les.managers.PhotoManager;

public final class Bot extends TelegramLongPollingBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;
    public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        System.out.println(botName);
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String msgText = update.getMessage().getText();
                if (msgText.equals("/start")) {
                    SendMessage startMessage = new SendMessage();
                    startMessage.setText("Привет, отправь свою фотографию, чтобы сгенерировать эмоцию");
                    startMessage.setChatId(update.getMessage().getChatId());
                    //startMessage.setReplyMarkup(KeyboardManager.menuKeyboard());
                    execute(startMessage);
                } else {
                    execute (new SendMessage(
                            "" + update.getMessage().getChatId(),
                            "Не понимаю Вас"
                            ));
                }
            } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
                    PhotoManager.receivePhoto(this, update);
                    execute(PhotoManager.returnPhoto(update));
                    execute(PhotoManager.returnNewPhoto(update));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
