package study.datajpa.paging;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Getter
public class PageableCustom {
    private Sort sort;
    private Long offset;
    private int pageNumber;
    private int pageSize;
    private boolean paged;
    private boolean unpaged;

    /*
    커스텀하지 않은 기본 Pageable과 page번호 제외하고 동일하게 구성
     */
    public PageableCustom(Page page) {
        this.sort = page.getSort();
        this.offset = page.getPageable().getOffset();
        this.pageNumber = page.getPageable().getPageNumber() + 1;
        this.pageSize = page.getPageable().getPageSize();
        this.paged = page.getPageable().isPaged();
        this.unpaged = page.getPageable().isUnpaged();

    }

    public PageableCustom(Slice slice) {
        this.sort = slice.getSort();
        this.offset = slice.getPageable().getOffset();
        this.pageNumber = slice.getPageable().getPageNumber() + 1;
        this.pageSize = slice.getPageable().getPageSize();
        this.paged = slice.getPageable().isPaged();
        this.unpaged = slice.getPageable().isUnpaged();
    }
}
