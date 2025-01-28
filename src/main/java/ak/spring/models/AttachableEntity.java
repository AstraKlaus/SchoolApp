package ak.spring.models;


import java.util.List;

public interface AttachableEntity {
    List<String> getAttachments();
    void setAttachments(List<String> attachments);
}