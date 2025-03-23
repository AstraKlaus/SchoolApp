package ak.spring.services;

import ak.spring.auth.RegisterRequest;
import ak.spring.dto.GroupProgressDTO;
import ak.spring.dto.HomeworkReportDTO;
import ak.spring.models.Role;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

import ak.spring.dto.PersonDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    @Value("${excel.file.path:/app/excel/users.xlsx}")
    private String FILE_PATH;

    public List<RegisterRequest> importUsersFromExcel(InputStream inputStream) throws IOException {
        List<RegisterRequest> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Начинаем с первой строки данных (пропускаем заголовок)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    // Получаем значения и очищаем их от невидимых символов и лишних пробелов
                    String lastName = cleanString(row.getCell(0).getStringCellValue(), false);
                    String firstName = cleanString(row.getCell(1).getStringCellValue(), false);
                    String patronymic = row.getCell(2) != null ? cleanString(row.getCell(2).getStringCellValue(), false) : "";
                    String roleString = cleanString(row.getCell(3).getStringCellValue(),true);

                    System.out.println(lastName + " " + firstName + " " + patronymic + " " + roleString);

                    // Преобразуем строку роли в enum
                    Role role = Role.valueOf(roleString.toUpperCase());
                    System.out.println(role.name());

                    // Создаем RegisterRequest
                    RegisterRequest user = RegisterRequest.builder()
                            .lastName(lastName)
                            .firstName(firstName)
                            .patronymic(patronymic)
                            .role(role)
                            .build();

                    System.out.println(user.toString());
                    users.add(user);
                }
            }
            return users;
        }
    }

    // Вспомогательный метод для очистки строки от невидимых символов и лишних пробелов
    private String cleanString(String input, Boolean isRole) {
        if (input == null) return "";
        if (!isRole){input = input.replaceAll("[^а-яА-ЯёЁ\\s]", "");}
        return input.trim()
                .replaceAll("[\\p{C}]", "") // Удаляем управляющие символы
                .replaceAll("\\s+", " ");    // Заменяем множественные пробелы одним

    }



    public ByteArrayInputStream exportUsersToExcel(List<PersonDTO> users) throws IOException {
        // Создаем новый Workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        createUserHeaders(sheet, false);

        // Заполняем данные
        int rowIndex = 1;
        for (PersonDTO user : users) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(user.getLastName() != null ? user.getLastName() : "");
            row.createCell(1).setCellValue(user.getFirstName() != null ? user.getFirstName() : "");
            row.createCell(2).setCellValue(user.getPatronymic() != null ? user.getPatronymic() : "");
            row.createCell(3).setCellValue(user.getUsername());
            row.createCell(4).setCellValue(user.getRole() != null ? user.getRole().name() : "");
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

        // Если файл существует, открываем его, иначе создаем новый
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis);
                 FileOutputStream fos = new FileOutputStream(FILE_PATH)) {

                Sheet sheet = workbook.getSheetAt(0); // Получаем первый лист

                // Находим последнюю строку и добавляем новую запись
                int rowNum = sheet.getLastRowNum() + 1;
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(lastName);
                row.createCell(1).setCellValue(firstName);
                row.createCell(2).setCellValue(patronymic);
                row.createCell(3).setCellValue(username);
                row.createCell(4).setCellValue(password);

                workbook.write(fos); // Записываем изменения в файл
            }
        } else {
            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(FILE_PATH)) {

                Sheet sheet = workbook.createSheet("Users"); // Создаем новый лист

                createUserHeaders(sheet, true);

                // Добавляем данные пользователя
                Row row = sheet.createRow(1);
                row.createCell(0).setCellValue(lastName);
                row.createCell(1).setCellValue(firstName);
                row.createCell(2).setCellValue(patronymic);
                row.createCell(3).setCellValue(username);
                row.createCell(4).setCellValue(password);

                workbook.write(fos); // Записываем в файл
            }
        }
    }


    public ByteArrayInputStream generateGroupProgressReportExcel(List<GroupProgressDTO> reportData) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Успеваемость группы");

            // Заголовки
            String[] headers = {"Группа", "Фамилия", "Имя", "Отчество", "Всего заданий", "Выполнено", "На проверке", "Не выполнено", "% выполнения"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Данные
            int rowIdx = 1;
            for (GroupProgressDTO data : reportData) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(data.getClassroomName());
                row.createCell(1).setCellValue(data.getLastName());
                row.createCell(2).setCellValue(data.getFirstName());
                row.createCell(3).setCellValue(data.getPatronymic());
                row.createCell(4).setCellValue(data.getTotalHomeworks());
                row.createCell(5).setCellValue(data.getCompleted());
                row.createCell(6).setCellValue(data.getInProgress());
                row.createCell(7).setCellValue(data.getNotCompleted());
                row.createCell(8).setCellValue(data.getCompletionRate());
            }

            // Авто-размер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


    public ByteArrayInputStream generateHomeworkReportExcel(List<HomeworkReportDTO> reportData) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Отчет по заданию");

            // Заголовки
            String[] headers = {"Задание", "Фамилия", "Имя", "Отчество", "Статус", "Комментарий", "Дата сдачи"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Данные
            int rowIdx = 1;
            for (HomeworkReportDTO data : reportData) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(data.getHomeworkName());
                row.createCell(1).setCellValue(data.getLastName());
                row.createCell(2).setCellValue(data.getFirstName());
                row.createCell(3).setCellValue(data.getPatronymic());
                row.createCell(4).setCellValue(data.getStatus());
                row.createCell(5).setCellValue(data.getComment());
                row.createCell(6).setCellValue(data.getSubmittedAt().toString());
            }

            // Авто-размер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public void removeUserFromExcel(String username) throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(FILE_PATH)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    String cellUsername = row.getCell(0).getStringCellValue();
                    if (username.equals(cellUsername)) {
                        if (i < lastRowNum) {
                            sheet.shiftRows(i + 1, lastRowNum, -1);
                        } else {
                            sheet.removeRow(row);
                        }
                        break;
                    }
                }
            }

            workbook.write(fos);
        }
    }



    // Приватный метод для создания заголовков пользователей
    private void createUserHeaders(Sheet sheet, boolean includePassword) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Фамилия");
        headerRow.createCell(1).setCellValue("Имя");
        headerRow.createCell(2).setCellValue("Отчество");
        headerRow.createCell(3).setCellValue("Имя пользователя");

        // Если нужен пароль, добавляем его, иначе добавляем роль
        if (includePassword) {
            headerRow.createCell(4).setCellValue("Пароль");
        } else {
            headerRow.createCell(4).setCellValue("Роль");
        }
    }

}

