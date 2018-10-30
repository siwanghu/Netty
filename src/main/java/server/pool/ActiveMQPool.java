package server.pool;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.protocol.ConstantValue;

import javax.jms.JMSException;

public class ActiveMQPool {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);
    private static PooledConnection connection;

    static{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setUserName("admin");
        activeMQConnectionFactory.setPassword("admin");
        activeMQConnectionFactory.setBrokerURL(ConstantValue.ACTIVEMQ_URL);
        try{
            PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
            pooledConnectionFactory.setMaximumActiveSessionPerConnection(10240);
            pooledConnectionFactory.setIdleTimeout(12000);
            pooledConnectionFactory.setMaxConnections(10240);
            pooledConnectionFactory.setBlockIfSessionPoolIsFull(true);
            connection = (PooledConnection) pooledConnectionFactory.createConnection();
            connection.start();
        }catch(Exception e) {
            LOGGER.debug("activeMQ连接池创建失败");
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try{
            if(connection != null) {
                connection.close();
            }
        }catch(JMSException e) {
            LOGGER.debug("activeMQ连接池关闭失败");
            throw new RuntimeException(e);
        }
    }

    public static PooledConnection getConnection() {
        return connection;
    }
}
