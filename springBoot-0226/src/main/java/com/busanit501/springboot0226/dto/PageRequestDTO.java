package com.busanit501.springboot0226.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Arrays;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default // 만약, page 를 지정하지 않으면, 기본값으로 1로 설정
    private int page = 1;

    @Builder.Default // 만약, size 를 지정하지 않으면, 기본값으로 10로 설정
    private int size = 10;

    // 검색어
    private String keyword;
    // 보고 있는 페이지의 정보를, URL 주소 뒤에 , ?page=3&size=10, 내용을 첨부하고 싶다.
    private String link;

    // 변경1,===============================================================
    // 검색 , 필터 이용시 필요한 준비물.
    private String type; // "t", "c" , "w", "tc","tw", "cw", "twc"

    // 검색시, 타입을 체크하는 기능 추가.
    // 목록 화면에서, 해당 타입을 검사하는 용도로 사용할 예정,
    public String[] getTypes() {
        if(type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }
    // 변경1,===============================================================

    // 변경2,===============================================================
    // ... : 가변 인자, 예시) "bno", "regDate"
    // String[] 배열로 취급 한다.
    // getPageable("bno", "regDate)
    // -> order by reg_date DESC, bno DESC, 호출 된다.
    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending() );
    }
    // 변경2,===============================================================

    public String getLink() {
        if(link == null || link.isEmpty()) {
            // 기본 : String, 불변, 객체를 새로 생성할 때마다, 새로운 메모리를 사용하고,
            // StringBuilder, 기존 메모리에(변경없이) 작업 및 사용함, 메모리 절약 효과. 그래서, 사용함.
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }

            if(keyword != null){
                // 한글로 넘어 오는 경우도 있어서, 인코딩을 UTF-8 로 변환.
                // 뭔가를 변환 작업을 한다면, 의무적으로 예외 처리를 해야함.
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            link = builder.toString(); // link = "page=3&size=10"
        }
        return link;
    }
}
