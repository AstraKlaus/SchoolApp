package ak.spring.controllers;

import ak.spring.dto.GroupProgressDTO;
import ak.spring.dto.HomeworkReportDTO;
import ak.spring.services.ExcelService;
import ak.spring.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("v1/api/reports")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Tag(name = "Report Management", description = "Управление отчётами и формированием отчётных файлов")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ExcelService excelService;

    @GetMapping("/group/{classroomId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Сформировать отчёт по прогрессу группы")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отчёт успешно сформирован"),
            @ApiResponse(responseCode = "404", description = "Группа не найдена"),
            @ApiResponse(responseCode = "400", description = "Ошибка при формировании отчёта")
    })
    public ResponseEntity<Resource> getGroupReport(
            @Parameter(description = "Идентификатор группы") @PathVariable int classroomId
    ) throws IOException {
        List<GroupProgressDTO> report = reportService.generateGroupProgressReport(classroomId);
        ByteArrayInputStream excelStream = excelService.generateGroupProgressReportExcel(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=group_report.xlsx")
                .body(new InputStreamResource(excelStream));
    }

    @GetMapping("/homework/{homeworkId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Сформировать отчёт по заданию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отчёт успешно сформирован"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "400", description = "Ошибка при формировании отчёта")
    })
    public ResponseEntity<Resource> getHomeworkReport(
            @Parameter(description = "Идентификатор задания") @PathVariable int homeworkId
    ) throws IOException {
        List<HomeworkReportDTO> report = reportService.generateHomeworkReport(homeworkId);
        ByteArrayInputStream excelStream = excelService.generateHomeworkReportExcel(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=homework_report.xlsx")
                .body(new InputStreamResource(excelStream));
    }
}

