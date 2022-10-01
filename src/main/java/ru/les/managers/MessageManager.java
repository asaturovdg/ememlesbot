package ru.les.managers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class MessageManager {

    public static SendMessage startMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Привет, пришли мне фотографию своего лица (советую приблизиться к камере)");
        return sendMessage;
    }
}
