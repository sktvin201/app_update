package cn.rokevin.app.upgrade;

/**
 * 下载进度监听
 */
public interface DownloadProgressListener {

    /**
     * @param info 下载信息
     */
    void progress(DownloadInfo info);

}
