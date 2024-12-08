package ak.spring.services;

import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Answer;
import ak.spring.models.Homework;
import ak.spring.models.Lesson;
import ak.spring.repositories.AnswerRepository;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.LessonRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import io.minio.RemoveObjectArgs;

import java.util.List;
import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    // Репозитории конкретных сущностей, в зависимости от того, с чем вы работаете.
    private final AnswerRepository answerRepository;
    private final LessonRepository lessonRepository;
    private final HomeworkRepository homeworkRepository;

    public MinioService(MinioClient minioClient,
                        String bucketName,
                        AnswerRepository answerRepository,
                        LessonRepository lessonRepository,
                        HomeworkRepository homeworkRepository) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.answerRepository = answerRepository;
        this.lessonRepository = lessonRepository;
        this.homeworkRepository = homeworkRepository;
    }

    @NotNull
    public static ResponseEntity<Resource> getResourceResponseEntity(@PathVariable String fileName,
                                                                     List<String> attachments,
                                                                     MinioService minioService) {
        if (!attachments.contains(fileName)) {
            throw new ResourceNotFoundException("File", "name", fileName);
        }

        InputStream fileStream = minioService.getFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(fileStream));
    }

    /**
     * Пример загрузки файла для конкретного ответа (Answer).
     */
    public void uploadFileToAnswer(int answerId, MultipartFile file) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        String fileName = generateFileName(file.getOriginalFilename());
        uploadToMinIO(file, fileName);

        // Обновляем список attachments
        List<String> attachments = answer.getAttachments();
        attachments.add(fileName);
        answer.setAttachments(attachments);
        answerRepository.save(answer);
    }

    /**
     * Пример загрузки файла для урока (Lesson).
     */
    public void uploadFileToLesson(int lessonId, MultipartFile file) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        String fileName = generateFileName(file.getOriginalFilename());
        uploadToMinIO(file, fileName);

        List<String> attachments = lesson.getAttachments();
        attachments.add(fileName);
        lesson.setAttachments(attachments);
        lessonRepository.save(lesson);
    }

    /**
     * Пример загрузки файла для домашнего задания (Homework).
     */
    public void uploadFileToHomework(int homeworkId, MultipartFile file) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Homework not found"));

        String fileName = generateFileName(file.getOriginalFilename());
        uploadToMinIO(file, fileName);

        List<String> attachments = homework.getAttachments();
        attachments.add(fileName);
        homework.setAttachments(attachments);
        homeworkRepository.save(homework);
    }

    /**
     * Получение файла из MinIO по имени.
     * Можно возвращать InputStream, чтобы далее переслать пользователю.
     */
    public InputStream getFile(String fileName) {

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить файл из MinIO", e);
        }
    }

    /**
     * Удаление файла из MinIO и из списка вложений.
     */
    public void removeFileFromAnswer(int answerId, String fileName) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        removeFromMinIO(fileName);

        List<String> attachments = answer.getAttachments();
        attachments.remove(fileName);
        answer.setAttachments(attachments);
        answerRepository.save(answer);
    }

    public void removeFileFromHomework(int homeworkId, String fileName) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        removeFromMinIO(fileName);

        List<String> attachments = homework.getAttachments();
        attachments.remove(fileName);
        homework.setAttachments(attachments);
        homeworkRepository.save(homework);
    }

    public void removeFileFromLesson(int lessonId, String fileName) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        removeFromMinIO(fileName);

        List<String> attachments = lesson.getAttachments();
        attachments.remove(fileName);
        lesson.setAttachments(attachments);
        lessonRepository.save(lesson);
    }

    // Генерация уникального имени файла
    private String generateFileName(String originalName) {
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    // Метод, непосредственно загружающий файл в MinIO
    private void uploadToMinIO(MultipartFile file, String fileName) {
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла в MinIO", e);
        }
    }

    // Метод удаления из MinIO
    private void removeFromMinIO(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла из MinIO", e);
        }
    }
}


