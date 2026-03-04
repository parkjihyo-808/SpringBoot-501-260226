package com.busanit501.springboot0226.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;

    // 시작 페이지 번호
    private int start;
    // 끝나는 페이지 번호
    private int end;

    // 이전 페이지의 존재여부
    private boolean prev;
    // 다음 페이지의 존재여부
    private boolean next;

    // 페이징 처리가 된 데이터 목록 10개
    private List<E> dtoList;

    // 해당 클래스 객체를 활성화 해줄 생성자를, 빌더 패턴으로 미리 등록,
    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {

        if(total <= 0) {
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        // start, end, total, last, prev, next 의 활성화 및 수식 조건
        this.end = (int)(Math.ceil(this.page/10.0)) * 10;
        this.start = this.end - 9;
        int last = (int)(Math.ceil(total/(double)size));
        this.end = end > last ? last : end;
        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }
}
