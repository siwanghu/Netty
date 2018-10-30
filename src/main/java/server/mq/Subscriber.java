package server.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.pool.ThreadPool;
import server.protocol.ConstantValue;
import server.protocol.WsProtocol;
import server.until.ServerUntil;

import javax.jms.*;

public class Subscriber {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);

    public static void main(String[] args) throws JMSException {
        ThreadPool threadPool=new ThreadPool();
        ServerUntil serverUntil=new ServerUntil();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConstantValue.ACTIVEMQ_URL);
        connectionFactory.setTrustAllPackages(true);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(ConstantValue.TOPIC_NAME);
        MessageConsumer messageConsumer = session.createConsumer(destination);
        messageConsumer.setMessageListener((message) -> {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                WsProtocol wsProtocol=(WsProtocol) objectMessage.getObject();
                threadPool.addTaskWirteFile(wsProtocol);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}

