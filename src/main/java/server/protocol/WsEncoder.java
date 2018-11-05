package server.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class WsEncoder extends MessageToByteEncoder<WsProtocolResponse> {

    @Override
    protected void encode(ChannelHandlerContext tcx, WsProtocolResponse msg,
                          ByteBuf out) throws Exception {
        out.writeByte(msg.getType());
    }
}