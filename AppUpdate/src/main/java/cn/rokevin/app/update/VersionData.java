package cn.rokevin.app.update;

import java.io.Serializable;

/**
 * Created by luokaiwen on 15/5/29.
 * <p/>
 * 版本升级信息
 */
public class VersionData implements Serializable {

    /**
     * version : 1.2.6
     * content :
     * url :
     * force : false
     */

    private String version;
    private String content;
    private String url;
    private boolean force;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    public String toString() {
        return "VersionData{" +
                "version='" + version + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", force=" + force +
                '}';
    }
}
