package study.datajpa.paging;

import lombok.Getter;
import org.springframework.data.domain.*;

import java.io.Serializable;
import java.util.List;
@Getter
public class PageCustom<T> implements Serializable {
    private List<T> content;
    private PageableCustom pageable;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
    private boolean first;
    private Sort sort;
    private int numberOfElements;
    private boolean empty;

    /*
    커스텀하지 않은 기본 Pageable과 page번호 제외하고 동일하게 구성
     */
    public PageCustom(Page<T> page) {
        this.content = page.getContent();
        this.pageable = new PageableCustom(page);
        this.last = page.isLast();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.first = page.isFirst();
        this.sort = page.getSort();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();
    }

    public PageCustom(Slice<T> slice) {
        this.content = slice.getContent();
        this.pageable = new PageableCustom(slice);
        this.last = slice.isLast();
        this.size = slice.getSize();
        this.number = slice.getNumber();
        this.first = slice.isFirst();
        this.sort = slice.getSort();
        this.numberOfElements = slice.getNumberOfElements();
        this.empty = slice.isEmpty();
    }

}
