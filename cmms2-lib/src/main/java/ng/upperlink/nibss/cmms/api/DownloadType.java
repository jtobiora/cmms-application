package ng.upperlink.nibss.cmms.api;

public enum DownloadType {

    CSV("csv", "text/csv"),
    EXCEL("xlsx", "application/octet-stream"),
    PDF("pdf", "application/pdf"),
    UNKNOWN("csv", "text/csv");

    private final String extension;
    private final String mimeType;

    DownloadType(String type, String mimeType) {

        this.extension = type;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static synchronized DownloadType find(String extension) {
        try {
            return DownloadType.valueOf(extension.toUpperCase());
        } catch (RuntimeException e) {
            return findByExtension(extension);
        }
    }

    private static DownloadType findByExtension(String extension) {
        DownloadType type = null;
        for (DownloadType downloadType : DownloadType.values()) {
            if( downloadType.extension.equalsIgnoreCase(extension)) {
                type = downloadType;
                break;
            }
        }
        return  type == null? UNKNOWN : type;
    }


}
