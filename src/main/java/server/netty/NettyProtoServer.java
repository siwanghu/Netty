package server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyProtoServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyProtoServer.class);
	public static final int PORT = 7766;

	public NettyProtoServer() {
	}

	public void initialize() {
		ServerBootstrap server = new ServerBootstrap();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new InitializerPipeline()).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true);
			ChannelFuture f = server.bind(PORT).sync();
			LOGGER.debug("服务端口为:" + PORT);
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			LOGGER.error("Netty启动异常：", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		new NettyProtoServer().initialize();
	}
}
