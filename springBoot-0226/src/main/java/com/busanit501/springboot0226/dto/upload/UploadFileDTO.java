package com.busanit501.springboot0226.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDTO {
    // 자료구조, 데이터 + 파일을 첨부할 수 있는 구조
    private List<MultipartFile> files;
}
