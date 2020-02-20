package cn.rokevin.app.upgrade;

import java.io.Serializable;

/**
 * 下载信息
 */
public class DownloadInfo implements Serializable {

    public static final int WAIT_DOWNLOAD = 0;
    public static final int DOWNLOADING = 1;
    public static final int FINISH = 2;
    public static final int ERORR = 3;

    /**
     * 存储位置
     */
    private String path;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型 jpg apk
     */
    private String fileType;

    /**
     * 下载该文件的url
     */
    private String url;

    /**
     * 下载时间戳
     */
    private int downloadTimestamp;

    /**
     * 文件总长度
     */
    private long contentLength;

    /**
     * 下载长度
     */
    private long readLength;

    /**
     * 下载百分比
     */
    private int percent;

    /**
     * 下载状态码
     */
    private int status;

    /**
     * 下载消息
     */
    private String message;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDownloadTimestamp() {
        return downloadTimestamp;
    }

    public void setDownloadTimestamp(int downloadTimestamp) {
        this.downloadTimestamp = downloadTimestamp;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", url='" + url + '\'' +
                ", downloadTimestamp=" + downloadTimestamp +
                ", contentLength=" + contentLength +
                ", readLength=" + readLength +
                ", percent=" + percent +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
