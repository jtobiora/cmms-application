package ng.upperlink.nibss.cmms.api;

import lombok.Data;

@Data
public class ReportSearch extends BaseSearch {

    private String downloadType;

    public DownloadType getDownloadType() {
        return downloadType == null ? DownloadType.CSV : DownloadType.find(downloadType);
    }

}
