package ru.les.managers;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardManager {
    public static ReplyKeyboardMarkup menuKeyboard() {
        KeyboardRow menuRow1 = new KeyboardRow();
        List<KeyboardRow> menuKeyboard = new ArrayList<>();

        menuRow1.add("Сгенерировать мем");
        menuKeyboard.add(menuRow1);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(menuKeyboard);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup cancelKeyboard() {
        KeyboardRow menuRow1 = new KeyboardRow();
        List<KeyboardRow> menuKeyboard = new ArrayList<>();

        menuRow1.add("Отмена");
        menuKeyboard.add(menuRow1);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(menuKeyboard);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
