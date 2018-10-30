package server.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.pool.ActiveMQPool;
import server.protocol.ConstantValue;
import server.protocol.WsProtocol;
import server.until.ServerUntil;

import javax.jms.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class EchoServerHandler extends ChannelHandlerAdapter {
	private ServerUntil serverUntil;
	private Destination destination;
	private MessageProducer producer;
	private Connection connection;
	private Session session;

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		serverUntil=new ServerUntil();
		connection= ActiveMQPool.getConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createTopic(ConstantValue.TOPIC_NAME);
		producer = session.createProducer(destination);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		WsProtocol wsProtocol=(WsProtocol)msg;
		ReferenceCountUtil.release(msg);
		ObjectMessage objectMessage=session.createObjectMessage(wsProtocol);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		producer.send(destination,objectMessage);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		closeResourse();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private void closeResourse() throws Exception{
		session.close();
		producer.close();
		serverUntil=null;
		destination=null;
		producer=null;
		connection=null;
		session=null;
		System.gc();
	}
}
