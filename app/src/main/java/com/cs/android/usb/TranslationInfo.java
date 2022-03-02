package com.cs.android.usb;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/22 10:27
 **/
public class TranslationInfo {

    private String uri;
    private STATE state;
    private String receiver;
    private String name;
    private String type;
    private String size;
    private int progress;

    public TranslationInfo(String uri, STATE state, String receiver, String name, String type, String size, int progress) {
        this.uri = uri;
        this.state = state;
        this.receiver = receiver;
        this.name = name;
        this.type = type;
        this.size = size;
        this.progress = progress;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "uri='" + uri + '\'' +
                ", state='" + state + '\'' +
                ", receiver='" + receiver + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", progress='" + progress + '\'' +
                '}';
    }

    public enum STATE {
        IDLE, WAIT_CONFIRM, TRANSLATING
    }
}
