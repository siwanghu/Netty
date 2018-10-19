package server.protocol;

import java.util.Arrays;

public class WsProtocol {
    private int head_data = ConstantValue.HEAD_DATA;     //开始标志位
    private long deviceIdHigh;                            //设备id高8位
    private long deviceIdLow;                             //设备id低8位
    private long sessionIdHigh;                           //回话id高8位
    private long sessionIdLow;                            //回话id低8位
    private long sequenceId;                               //序号id
    private long reserve;                                  //预留8字节标志位
    private int contentLength;                            //数据字段长度
    private byte[] content;                                //长度必须小于1024字节

    public WsProtocol(long deviceIdHigh, long deviceIdLow, long sessionIdHigh, long sessionIdLow, long sequenceId, long reserve, int contentLength, byte[] content) {
        this.deviceIdHigh = deviceIdHigh;
        this.deviceIdLow = deviceIdLow;
        this.sessionIdHigh = sessionIdHigh;
        this.sessionIdLow = sessionIdLow;
        this.sequenceId = sequenceId;
        this.reserve = reserve;
        this.contentLength = contentLength;
        this.content = content;
    }

    public int getHead_data() {
        return head_data;
    }

    public long getDeviceIdHigh() {
        return deviceIdHigh;
    }

    public long getDeviceIdLow() {
        return deviceIdLow;
    }

    public long getSessionIdHigh() {
        return sessionIdHigh;
    }

    public long getSessionIdLow() {
        return sessionIdLow;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public long getReserve() {
        return reserve;
    }

    public int getContentLength() {
        return contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setDeviceIdHigh(long deviceIdHigh) {
        this.deviceIdHigh = deviceIdHigh;
    }

    public void setDeviceIdLow(long deviceIdLow) {
        this.deviceIdLow = deviceIdLow;
    }

    public void setSessionIdHigh(long sessionIdHigh) {
        this.sessionIdHigh = sessionIdHigh;
    }

    public void setSessionIdLow(long sessionIdLow) {
        this.sessionIdLow = sessionIdLow;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setReserve(long reserve) {
        this.reserve = reserve;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "WsProtocol{" +
                "head_data=" + head_data +
                ", deviceIdHigh=" + deviceIdHigh +
                ", deviceIdLow=" + deviceIdLow +
                ", sessionIdHigh=" + sessionIdHigh +
                ", sessionIdLow=" + sessionIdLow +
                ", sequenceId=" + sequenceId +
                ", reserve=" + reserve +
                ", contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
