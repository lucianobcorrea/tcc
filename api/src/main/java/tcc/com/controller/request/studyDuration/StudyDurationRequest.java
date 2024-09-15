package tcc.com.controller.request.studyDuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StudyDurationRequest {
    private String duration;
    private MultipartFile image;
}
