package com.busanit501.springboot0226.controller;

import com.busanit501.springboot0226.dto.ReplyDTO;
import com.busanit501.springboot0226.dto.upload.UploadFileDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@Log4j2
public class UpDownController {

    // 이미지 파일이 저장되는 위치를 , application.properties 에 등록 했음.
    //
    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    @Tag(name = "이미지 파일 업로드 테스트",
            description = "post 방식으로 멀티파트 폼에 이미지를 첨부해서 서버에 전달하기. ")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(
            // 화면에서 전달된 이미지 파일들을 받기. files
            UploadFileDTO uploadFileDTO
    )  {
        log.info(" UpDownController 이미지 첨부 테스트 확인 : "  );

        // 첨부된 이미지들의 파일명 확인 해보기.
        if(uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach(file -> {
                // 원본 이미지 파일명을 , 서버의 로그로 확인.
                log.info(file.getOriginalFilename());

                // 원본 이미지 파일명
                String originName = file.getOriginalFilename();
                // uuid 를 이용해서, 원본 파일명을, 중복 안되게, 랜덤한 문자열의 길이로 수정.
                String uuid = UUID.randomUUID().toString();
                // 업로드 경로 : c:\\upload\\springTest
                // 랜덤하게 생성된 uuid 를 덧붙여서, 원본 파일명과 같이 사용함.
                Path savePath = Paths.get(uploadPath, uuid+"_"+originName);
                log.info(" UpDownController 이미지 첨부 경로 확인(uuid이용) savePath : " + savePath.toString()  );

                try {
                    // 실제 경로에, 이미지 파일 저장.
                    file.transferTo(savePath);

                    // 첨부된 파일이 , 이미지이면, 썸네일 도구 이용해서, 이미지 화질 축소 작업.
                    // savePath :
                    // c:\\upload\\springTest\\5b418a60-407e-406e-991e-db88d35ea426_크롬기준-로컬스토리지 저장소 확인 방법.PNG
                    // image/png, image/jpeg, image/jpg , 이런 형식으로 서버에 전달이 됩니다.
                    if(Files.probeContentType(savePath).startsWith("image")) {
                        // 자바에서, 새로운 파일을 생성하는 도구,
                        // 예시 : s_5b418a60-407e-406e-991e-db88d35ea426_크롬기준-로컬스토리지 저장소 확인 방법.PNG
                        // 주의사항은 파일명에 앞에 s_ 있는지를 확인 잘해야함. 우리는 썸네일로 약속.
                        // 빈 파일 생성,
                        File thumbFile = new File(uploadPath, "s_" + uuid+"_"+originName);
                        // 썸네일 도구 이용해서, 축소된 이미지 내용을,  thumbFile 쓰기 작업, (바이트 작업),
                        // 도구 이용.
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200);

                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        return null;
    }
}
