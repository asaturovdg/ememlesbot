package ru.les.managers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class PhotoManager {

    public static void receivePhoto(TelegramLongPollingBot bot, Update update) throws TelegramApiException {
        Document doc = update.getMessage().getDocument();
        GetFile getFile = new GetFile();
        getFile.setFileId(doc.getFileId());
        File file = bot.execute(getFile);
        try {
            InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
            DatabaseManager.photoToPy(update.getMessage().getChatId(), is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static SendPhoto returnPhoto(Update update) {

        long chat_id = update.getMessage().getChatId();

        // Array with photo objects with different sizes
        // We will get the biggest photo from that array
        List<PhotoSize> photos = update.getMessage().getPhoto();
        // Know file_id
        String f_id = photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getFileId();
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chat_id);
        sendPhoto.setPhoto(new InputFile().setMedia(f_id));
        sendPhoto.setCaption("Держи");
        return sendPhoto;
    }
}
