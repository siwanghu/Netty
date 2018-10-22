package server.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class WsEncoder  extends MessageToByteEncoder<WsProtocol> {
    @Override
    protected void encode(ChannelHandlerContext tcx, WsProtocol msg,
                          ByteBuf out) throws Exception {
        out.writeInt(msg.getHead_data());
        out.writeInt(msg.getVersionId());
        out.writeLong(msg.getDeviceIdHigh());
        out.writeLong(msg.getDeviceIdLow());
        out.writeLong(msg.getSessionIdHigh());
        out.writeLong(msg.getSessionIdLow());
        out.writeLong(msg.getSequenceId());
        out.writeLong(msg.getReserve());
        out.writeInt(msg.getCheck());
        out.writeInt(msg.getContentLength());
        out.writeBytes(msg.getContent());
        out.writeBytes("auditoryworks".getBytes());
    }
}