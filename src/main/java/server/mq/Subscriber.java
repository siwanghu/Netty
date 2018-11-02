package server.mq;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.pool.ThreadPool;
import server.protocol.ConstantValue;
import server.protocol.WsProtocol;
import server.until.ServerUntil;
import org.bson.Document;

import javax.jms.*;
import java.util.Base64;

public class Subscriber {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);

    public static void main(String[] args) throws JMSException {
        MongoClient mongoClient = new MongoClient( ConstantValue.MONGODB_URL, 27017 );
        ServerUntil serverUntil=new ServerUntil();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConstantValue.ACTIVEMQ_URL);
        connectionFactory.setTrustAllPackages(true);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(ConstantValue.QUEUE_NAME);
        MessageConsumer messageConsumer = session.createConsumer(destination);
        messageConsumer.setMessageListener((message) -> {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                WsProtocol wsProtocol=(WsProtocol) objectMessage.getObject();
                String deviceID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(wsProtocol.getDeviceIdHigh()), serverUntil.longToBytes(wsProtocol.getDeviceIdLow()));
                String sessionID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(wsProtocol.getSessionIdHigh()), serverUntil.longToBytes(wsProtocol.getSessionIdLow()));
                String sequenceID = String.valueOf(wsProtocol.getSequenceId());
                MongoDatabase mongoDatabase = mongoClient.getDatabase(deviceID);
                MongoCollection<Document> collection = mongoDatabase.getCollection(sessionID);
                Document document=new Document();
                document.append("_id",sequenceID);
                document.append("device",deviceID);
                document.append("session",sessionID);
                document.append("content", Base64.getEncoder().encodeToString(wsProtocol.getContent()));
                collection.insertOne(document);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}