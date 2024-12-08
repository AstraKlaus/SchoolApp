package ak.spring.services;

import ak.spring.auth.AuthenticationService;
import ak.spring.auth.RegisterRequest;
import ak.spring.models.Role;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

import ak.spring.dto.PersonDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private static final String FILE_PATH = "users_data.xlsx";
    private final AuthenticationService authenticationService;

    public ExcelService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    public List<RegisterRequest> importUsersFromExcel(InputStream inputStream) throws IOException {
        List<RegisterRequest> users = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // Начинаем с первой строки данных (пропускаем заголовок)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            if (row != null) {
                String lastName = row.getCell(0).getStringCellValue();
                String firstName = row.getCell(1).getStringCellValue();
                String patronymic = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
                String roleString = row.getCell(3).getStringCellValue();

                // Преобразуем строку роли в enum
                Role role = Role.valueOf(roleString.toUpperCase());

                // Создаем PersonDTO
                RegisterRequest user = RegisterRequest.builder()
                        .lastName(lastName)
                        .firstName(firstName)
                        .patronymic(patronymic)
                        .role(role)
                        .build();

                authenticationService.register(user);
                users.add(user);
            }
        }

        workbook.close();
        return users;
    }

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

