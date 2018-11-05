package server.protocol;

public class WsProtocolResponse {
    private byte type;

    public WsProtocolResponse(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "WsProtocolResponse{" +
                "type=" + type +
                '}';
    }
}
