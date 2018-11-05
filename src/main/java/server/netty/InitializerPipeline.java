package server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import server.netty.handler.EchoServerHandler;
import server.protocol.ConstantValue;
import server.protocol.WsDecoder;
import server.protocol.WsEncoder;

public class InitializerPipeline extends ChannelInitializer<SocketChannel> {
	public InitializerPipeline() {
   
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		ByteBuf delimiter = Unpooled.copiedBuffer("auditoryworks".getBytes());
		pipeline.addFirst(new DelimiterBasedFrameDecoder(ConstantValue.MAX_PACKAGE_SIZE, delimiter));
		pipeline.addLast(new WsEncoder());
		pipeline.addLast(new WsDecoder());
		pipeline.addLast("handler", new EchoServerHandler());
	}
}
