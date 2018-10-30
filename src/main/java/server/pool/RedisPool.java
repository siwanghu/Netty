package server.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import server.netty.handler.EchoServerHandler;

public final class RedisPool {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);
    private static String ADDR = "127.0.0.1";
    private static Integer PORT = 6379;
    private static String PASS="root";

    private static Integer MAX_TOTAL = 1024;
    private static Integer MAX_IDLE = 200;
    private static Integer MAX_WAIT_MILLIS = 100000;
    private static Integer TIMEOUT = 100000;
    private static Boolean TEST_ON_BORROW = true;
    private static JedisPool jedisPool = null;

    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config,ADDR,PORT,TIMEOUT,PASS);
            LOGGER.debug("Redis连接池初始化");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public synchronized static Jedis getJedis(){
        try {
            if(jedisPool != null){
                Jedis jedis = jedisPool.getResource();
                return jedis;
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void returnResource(final Jedis jedis){
        if(jedis!=null){
            jedisPool.returnResource(jedis);
        }
    }
}
