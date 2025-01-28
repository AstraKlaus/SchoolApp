package ak.spring.services;

import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.AttachableEntity;
import ak.spring.repositories.AnswerRepository;
import ak.spring.repositories.AttachableRepository;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.LessonRepository;
import io.minio.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucketName;
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

    public <T extends AttachableEntity> String uploadFile(
            AttachableRepository<T, Integer> repository,
            int entityId,
            MultipartFile file,
            String notFoundMessage
    ) {
        T entity = repository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException(notFoundMessage));

        String fileName = generateFileName(file.getOriginalFilename());
        uploadToMinIO(file, fileName);

        entity.getAttachments().add(fileName);
        repository.save(entity);

        return fileName;
    }

    public <T extends AttachableEntity> void removeFile(
            AttachableRepository<T, Integer> repository,
            int entityId,
            String fileName,
            String notFoundMessage
    ) {
        T entity = repository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException(notFoundMessage));

        if (!entity.getAttachments().contains(fileName)) {
            throw new ResourceNotFoundException("File", "name", fileName);
        }

        removeFromMinIO(fileName);
        entity.getAttachments().remove(fileName);
        repository.save(entity);
    }

    public String uploadFileToAnswer(int answerId, MultipartFile file) {
        return uploadFile(answerRepository, answerId, file, "Answer not found");
    }

    public String uploadFileToLesson(int lessonId, MultipartFile file) {
        return uploadFile(lessonRepository, lessonId, file, "Lesson not found");
    }

    public String uploadFileToHomework(int homeworkId, MultipartFile file) {
        return uploadFile(homeworkRepository, homeworkId, file, "Homework not found");
    }

    public void removeFileFromAnswer(int answerId, String fileName) {
        removeFile(answerRepository, answerId, fileName, "Answer not found");
    }

    public void removeFileFromLesson(int lessonId, String fileName) {
        removeFile(lessonRepository, lessonId, fileName, "Lesson not found");
    }

    public void removeFileFromHomework(int homeworkId, String fileName) {
        removeFile(homeworkRepository, homeworkId, fileName, "Homework not found");
    }

    public ResponseEntity<Resource> getResourceResponseEntity(String storedFileName, List<String> attachments) {
        if (!attachments.contains(storedFileName)) {
            throw new ResourceNotFoundException("Файл не найден");
        }

        String originalFileName = getOriginalFileName(storedFileName);
        InputStream fileStream = getFile(storedFileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(getContentType(storedFileName)))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + originalFileName + "\"") // Фикс здесь
                .body(new InputStreamResource(fileStream));
    }


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

    public String getContentType(String fileName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return stat.contentType();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить метаданные из MinIO", e);
        }
    }


    // Генерация уникального имени файла
    private String generateFileName(String originalName) {
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private void uploadToMinIO(MultipartFile file, String fileName) {
        try (InputStream is = file.getInputStream()) {
            String originalName = file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .userMetadata(Map.of("original-name", originalName))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки в MinIO", e);
        }
    }

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

    public String getOriginalFileName(String storedName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storedName)
                            .build()
            );
            return stat.userMetadata().get("original-name");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения метаданных", e);
        }
    }
}


