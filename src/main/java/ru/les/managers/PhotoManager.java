package ru.les.managers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PhotoManager {
    private static HashMap<String, String> engToRus = new HashMap<>() {{
        put("Anger", "Злость\uD83D\uDE21");
        put("Disgust", "Отвращение\uD83E\uDD22");
        put("Fear", "Страх\uD83D\uDE27");
        put("Happy", "Радость\uD83D\uDE04");
        put("Neutral", "Безразличие\uD83D\uDE10");
        put("Sadness", "Грусть\uD83D\uDE22");
    }};

    public static void switchPhoto(TelegramLongPollingBot bot, Update update) throws TelegramApiException{
        receivePhoto(bot, update);
        bot.execute(returnNewPhoto(update));
    }
    public static void receivePhoto(TelegramLongPollingBot bot, Update update) throws TelegramApiException {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        String photoId = photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getFileId();

        GetFile getFile = new GetFile();
        getFile.setFileId(photoId);
        File file = bot.execute(getFile);
        try {
            InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
            DatabaseManager.photoToPy(update.getMessage().getChatId(), is);
            System.out.println("OK!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        bot.execute(new SendChatAction(
                "" + update.getMessage().getChatId(),
                "upload_photo"
        ));
    }
    public static SendPhoto returnPhoto(Update update) {

        long chat_id = update.getMessage().getChatId();
        List<PhotoSize> photos = update.getMessage().getPhoto();
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

    public static SendPhoto returnNewPhoto(Update update) {
        long chatId = update.getMessage().getChatId();
        String emotion = "none";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "/root/getimage.py", "" + chatId);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            System.out.println("Ok!");
            bufferedReader.close();

            emotion = DatabaseManager.emotionFromPy(chatId);

            processBuilder = new ProcessBuilder("python3", "/root/insert.py", "" + chatId, emotion);
            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            System.out.println("Ok2!");
            bufferedReader.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseManager.updateUserEmotion(chatId, emotion);

        InputStream inputStream = DatabaseManager.photoFromPy(chatId);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(inputStream, "ememlesbot"));
        sendPhoto.setCaption("Ваша эмоция: " + engToRus.get(emotion));
        return sendPhoto;
    }
}
