package client;

import redis.clients.jedis.Jedis;
import server.pool.RedisPool;
import server.until.ServerUntil;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {
    private static byte[] data=new byte[1024*1024*2];

    public static void main(String[] args){
        //test3("00000166-a9ea-af40-0000-000000000108","00000000-0000-0206-0000-00000000090f");
        test2();

//        String tablename="`2018-10-29/09-48-51`";
//        String sql = "CREATE TABLE "+tablename + " (id VARCHAR(512) not NULL, content VARCHAR(2048) not NULL, device VARCHAR(512) not NULL, session VARCHAR(512) not NULL, PRIMARY KEY (id));";
//        System.out.println(sql);

//        Properties p=new Properties();
//        try {
//            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("dbcpconfig.properties"));
//            System.out.println(p.getProperty("username"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public  static void test2(){
        ArrayList<ThreadClient> threads=new ArrayList<ThreadClient>();
        for(int i=0;i<100;i++) {
            ThreadClient thread =new ThreadClient(i);
            threads.add(thread);
        }
        for(int i=0;i<threads.size();i++){
            System.out.println(threads.get(i).getName());
            threads.get(i).start();
        }
        for(int i=0;i<threads.size();i++){
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void test3(String deviceID,String sessionID){
        Jedis jedis= RedisPool.getJedis();
        try {
            File filename=new File("D:\\a.zip");
            if(!filename.exists())
                filename.createNewFile();
            OutputStream file = new FileOutputStream(filename);
            for (int i = 0; i < 4125; i++) {
                System.out.println(deviceID + ":" + sessionID + ":" + i);
                //byte[] data = Base64.getDecoder().decode(jedis.get(deviceID + ":" + sessionID + ":" + i));
                file.write(data);
            }
            file.close();
            RedisPool.returnResource(jedis);
        }catch (Exception e){}
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    private static byte[] method(byte a[],byte b[]) {
        byte c[]= Arrays.copyOf(a, a.length+b.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}