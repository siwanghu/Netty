package server.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class WsDecoder extends ByteToMessageDecoder {
    public final int BASE_LENGTH = ConstantValue.HEAD_LENGTH;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
                          List<Object> out) throws Exception {
        if (buffer.readableBytes() >= BASE_LENGTH) {
            if (buffer.readableBytes() > 2048) {
                buffer.skipBytes(buffer.readableBytes());
            }
            int beginReader;
            while (true) {
                beginReader = buffer.readerIndex();
                buffer.markReaderIndex();
                if (buffer.readInt() == ConstantValue.HEAD_DATA) {
                    break;
                }
                buffer.resetReaderIndex();
                buffer.readByte();
                if (buffer.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }
            int versionId=buffer.readInt();
            long deviceIdHigh=buffer.readLong();
            long deviceIdLow=buffer.readLong();
            long sessionIdHigh=buffer.readLong();
            long sessionIdLow=buffer.readLong();
            long sequenceId=buffer.readLong();
            long reserve=buffer.readLong();
            int check=buffer.readInt();
            int length = buffer.readInt();
            if (buffer.readableBytes() < length) {
                buffer.readerIndex(beginReader);
                return;
            }
            byte[] data = new byte[length];
            buffer.readBytes(data);
            WsProtocol protocol = new WsProtocol(versionId,deviceIdHigh,deviceIdLow,sessionIdHigh,sessionIdLow,sequenceId,reserve,check,length,data);
            out.add(protocol);
        }
    }
}
