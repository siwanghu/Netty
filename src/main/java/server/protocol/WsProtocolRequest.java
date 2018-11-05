package server.protocol;

import java.io.Serializable;
import java.util.Arrays;

public class WsProtocolRequest implements Serializable {
    private int head_data = ConstantValue.HEAD_DATA;      //开始标志位
    private short typeId;                                  //类型
    private short versionId;                               //版本号
    private long deviceIdHigh;                            //设备id高64位
    private long deviceIdLow;                             //设备id低64位
    private long sessionIdHigh;                           //回话id高64位
    private long sessionIdLow;                            //回话id低64位
    private long sequenceId;                               //序号id
    private long reserve;                                  //预留8字节标志位
    private int check;                                      //校验码
    private int contentLength;                            //数据字段长度
    private byte[] content;                                //数据体

    public WsProtocolRequest(short typeId, short versionId, long deviceIdHigh, long deviceIdLow, long sessionIdHigh, long sessionIdLow, long sequenceId, long reserve, int check, int contentLength, byte[] content) {
        this.typeId=typeId;
        this.versionId=versionId;
        this.deviceIdHigh = deviceIdHigh;
        this.deviceIdLow = deviceIdLow;
        this.sessionIdHigh = sessionIdHigh;
        this.sessionIdLow = sessionIdLow;
        this.sequenceId = sequenceId;
        this.reserve = reserve;
        this.check=check;
        this.contentLength = contentLength;
        this.content = content;
    }

    public int getHead_data() {
        return head_data;
    }

    public short getTypeId() {
        return typeId;
    }

    public int getVersionId() {
        return versionId;
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

    public int getCheck() {
        return check;
    }

    public int getContentLength() {
        return contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setTypeId(short typeId) {
        this.typeId = typeId;
    }

    public void setVersionId(short versionId) {
        this.versionId = versionId;
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

    public void setCheck(int check) {
        this.check = check;
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
                ", typeId=" + typeId +
                ", versionId=" + versionId +
                ", deviceIdHigh=" + deviceIdHigh +
                ", deviceIdLow=" + deviceIdLow +
                ", sessionIdHigh=" + sessionIdHigh +
                ", sessionIdLow=" + sessionIdLow +
                ", sequenceId=" + sequenceId +
                ", reserve=" + reserve +
                ", check=" + check +
                ", contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
