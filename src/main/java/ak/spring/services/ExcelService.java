package ak.spring.services;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import ak.spring.dto.PersonDTO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    private static final String FILE_PATH = "users_data.xlsx";


    public ByteArrayInputStream exportUsersToExcel(List<PersonDTO> users) throws IOException {
        // Создаем новый Workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Заголовки столбцов
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Фамилия");
        headerRow.createCell(1).setCellValue("Имя");
        headerRow.createCell(2).setCellValue("Отчество");
        headerRow.createCell(3).setCellValue("Имя пользователя");
        headerRow.createCell(4).setCellValue("Роль");

        // Заполняем данные
        int rowIndex = 1;
        for (PersonDTO user : users) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(user.getLastName() != null ? user.getLastName() : ""); // Фамилия
            row.createCell(1).setCellValue(user.getFirstName() != null ? user.getFirstName() : ""); // Имя
            row.createCell(2).setCellValue(user.getPatronymic() != null ? user.getPatronymic() : ""); // Отчество
            row.createCell(3).setCellValue(user.getUsername()); // Имя пользователя
            row.createCell(4).setCellValue(user.getRole() != null ? user.getRole().name() : ""); // Роль
        }

        // Автоматически подгоняем ширину столбцов
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        // Сохраняем Workbook в поток
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } finally {
            workbook.close();
        }
    }


    // Метод для добавления пользователя в файл Excel
    public void addUserToExcel(String username, String firstName, String patronymic, String lastName, String password) throws IOException {
        File file = new File(FILE_PATH);
        Workbook workbook;
        Sheet sheet;

        // Если файл существует, открываем его, иначе создаем новый
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis); // Открываем существующий .xlsx файл
                sheet = workbook.getSheetAt(0); // Получаем первый лист
            }
        } else {
            workbook = new XSSFWorkbook(); // Создаем новый .xlsx файл
            sheet = workbook.createSheet("Users"); // Создаем новый лист
            // Добавляем заголовки для столбцов
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Фамилия");
            headerRow.createCell(1).setCellValue("Имя");
            headerRow.createCell(2).setCellValue("Отчество");
            headerRow.createCell(3).setCellValue("Имя пользователя");
            headerRow.createCell(4).setCellValue("Пароль");
        }

        // Находим последнюю строку и добавляем новую запись
        int rowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(username);
        row.createCell(1).setCellValue(firstName);
        row.createCell(2).setCellValue(patronymic);
        row.createCell(3).setCellValue(lastName);
        row.createCell(4).setCellValue(password);

        // Записываем обновленный Excel файл обратно на диск
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos); // Записываем изменения в файл
        }
        workbook.close(); // Закрываем workbook для освобождения ресурсов
    }
}

