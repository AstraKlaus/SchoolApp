package ak.spring.services;

import ak.spring.models.Accord;
import ak.spring.models.Author;
import ak.spring.models.Song;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ak.spring.configs.BotConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
@Transactional
public class TelegramBot extends TelegramLongPollingBot {

    private final AccordService accordService;
    private final SongService songService;
    private final AuthorService authorService;
    private final BotConfig botConfig;

    @Autowired
    public TelegramBot(AccordService accordService,
                       SongService songService,
                       AuthorService authorService,
                       BotConfig botConfig) {
        this.accordService = accordService;
        this.songService = songService;
        this.authorService = authorService;
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.getMessage().hasDocument() && !update.getMessage().getCaption().isEmpty()) {
            uploadImage(update);
        }

        if(update.hasMessage() && update.getMessage().hasText()){
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (message){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    startButtons(chatId);
                    break;
                case "Аккорды":
                    taskDone(update.getMessage());
                    break;
                default:
                    findByText(chatId, message);
                    break;
            }
        }
    }

    private void findByText(long chatId, String message) {
        List<Song> song = songService.findByName(message);
        List<Author> author = authorService.findByName(message);

        if (song != null){
            sendSong(chatId, song.get(0));
        }else if (author != null) {
            authorButtons(chatId, author.get(0));
        }else {
            sendMessage(chatId,"Группы или песни с таким название не найдено.");
        }
    }

    private void uploadImage(Update update) {
        Message message = update.getMessage();
        PhotoSize photo = message.getDocument().getThumb();

        GetFile getFile = new GetFile(photo.getFileId());

        try {
            File file = execute(getFile);
            var fileToSave = downloadFile(file);
            byte[] fileContent = Files.readAllBytes(fileToSave.toPath());

            Accord accord = new Accord();
            accord.setName(message.getCaption());
            accord.setImage(fileContent);

            accordService.save(accord);

            sendMessage(message.getChatId(), "Картинка успешно сохранена");
        } catch (TelegramApiException | IOException e ) {
            e.printStackTrace();
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Добро пожаловать в амогус, " + name;
        sendMessage(chatId, answer);
    }

    private void taskDone(Message message) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton createRoom = new InlineKeyboardButton();

        createRoom.setText("Выполнил задание");
        createRoom.setCallbackData("TASK_DONE");

        rowInLine.add(createRoom);

        rowsInLine.add(rowInLine);

        markup.setKeyboard(rowsInLine);
        message.setReplyMarkup(markup);
    }

    private void startButtons(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add(new KeyboardButton("Аккорды"));
        row.add(new KeyboardButton("..."));

        rows.add(row);

        markup.setKeyboard(rows);

        sendMessage.setReplyMarkup(markup);
        sendMessage.setText("Что хочешь сделать?");
        try {
            execute(sendMessage);
        }catch (TelegramApiException e) {
            log.error("Кнопки комнаты не отправилось " + e.getMessage());
        }
    }

    private void authorButtons(long chatId, Author author) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        for (Song song:author.getSongs()){
            rows.add(new KeyboardRow(List.of(new KeyboardButton(song.getName()))));
        }

        markup.setKeyboard(rows);

        sendMessage.setReplyMarkup(markup);
        sendMessage.setText("Какую песню?");
        try {
            execute(sendMessage);
        }catch (TelegramApiException e) {
            log.error("Кнопки комнаты не отправилось " + e.getMessage());
        }
    }

    private void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        }catch (TelegramApiException e) {
            log.error("Сообщение не отправилось " + e.getMessage());
        }
    }

    private void sendSong(long chatId, Song song){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("<b>"+song.getAuthor().getName() + "\n" + song.getName() + "</b>\n" + song.getText());
        message.enableHtml(true);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));

        try {
            execute(message);
        }catch (TelegramApiException e) {
            log.error("Сообщение не отправилось " + e.getMessage());
        }

        for(Accord accord : song.getAccords()) {
            InputStream photoStream = new ByteArrayInputStream(accord.getImage());
            sendPhoto.setPhoto(new InputFile(photoStream, accord.getName()));
            sendPhoto.setCaption(accord.getName());
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                log.error("Аккорды не отправились " + e.getMessage());
            }
        }

    }
}
