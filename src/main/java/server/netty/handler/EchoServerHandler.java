package server.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import server.protocol.ConstantValue;
import server.protocol.WsProtocolRequest;
import server.protocol.WsProtocolResponse;

import javax.jms.*;

public class EchoServerHandler extends ChannelHandlerAdapter {
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private MessageProducer producer;
	private Connection connection;
	private Session session;

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, ConstantValue.ACTIVEMQ_URL);
		connection= connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(ConstantValue.QUEUE_NAME);
		producer = session.createProducer(destination);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		WsProtocolRequest wsProtocol=(WsProtocolRequest)msg;
		ReferenceCountUtil.release(msg);
		if(wsProtocol.getTypeId()==0) {
			ObjectMessage objectMessage = session.createObjectMessage(wsProtocol);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			producer.send(destination, objectMessage);
		}else if(wsProtocol.getTypeId()==1){
			ctx.channel().writeAndFlush(new WsProtocolResponse(ConstantValue.PONG));
		}
	}

	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		super.close(ctx, promise);
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
		connection.close();
		System.gc();
	}
}