package ng.upperlink.nibss.cmms.dto.search;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageSearch extends Search implements Serializable {

    private final static int MIN_DATA_SIZE = 10;

    private final static int MAX_DATA_SIZE = 100;

    protected int pageNumber;

    protected int pageSize;

    public int getPageSize() {
        if (pageSize <= 0)
            return MIN_DATA_SIZE;
        if( pageSize > MAX_DATA_SIZE)
            return MAX_DATA_SIZE;

        return pageSize;
    }

    public int getPageNumber() {
        if (pageNumber <= 0)
            return 0;

        int page = pageNumber; //- 1;

        return page < 0 ? 0 : page;
    }

    public boolean isPageable(){
        return !(this.pageSize == 0 && this.pageNumber == 0);
    }

}
