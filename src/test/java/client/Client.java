package client;

import redis.clients.jedis.Jedis;
import server.pool.RedisPool;
import server.until.ServerUntil;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {
    private static  byte[] head=new byte[]{
            0x57,0x53,0x4B,0x4A,                      //头部
            0x00,0x00,0x00,0x01,                      //版本号
            0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x09, //设备id高位
            0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x08, //设备id低位
            0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x06, //回话id高位
            0x00,0x00,0x00,0x00,0x00,0x00,0x09,0x0F, //回话id低位
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01, //序号id
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, //保留字段
            0x00,0x00,0x00,0x00,                     //校验码
            0x00,0x00,0x04,0x00,                     //长度
    };
    private static byte[] data=new byte[1024];
    private static byte[] tail="auditoryworks".getBytes();
    private static Random random=new Random();
    private static int[] device={0,1,2,3,4,5,6,7,8,9,10,
                                   11,12,13,14,15,16,17,18,19,20,
                                   21,22,23,24,25,26,27,28,29,30,
                                   31,32,33,34,35,36,37,38,39,40,
                                   41,42,43,44,45,46,47,48,49,50};
    private static ServerUntil serverUntil=new ServerUntil();

    public static void main(String[] args){
        //test1();
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

    public static void test1() {
        try {
            long id= Calendar.getInstance().getTimeInMillis()+new Random().nextInt(100000);
            ThreadClient.currentThread().setName("线程"+id);
            byte[] deviceID = serverUntil.longToBytes(id);
            System.out.println(Arrays.toString(deviceID));
            for (int x = 0, y = 8; x < deviceID.length; x++, y++) {
                head[y] = deviceID[x];
            }
            Socket client = new Socket("127.0.0.1", 7766);
            OutputStream out = client.getOutputStream();
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) 0x00;
            }
            long sequence = 0;
            FileInputStream in = new FileInputStream(new File("D:\\music.mp3"));
            while (in.read(data) > -1) {
                byte[] bytes = method(method(head, data), tail);
                byte[] sequenceID = serverUntil.longToBytes(sequence);
                byte[] length = serverUntil.longToBytes(data.length);
                for (int i = 0, j = 40; i < sequenceID.length; i++, j++) {
                    bytes[j] = sequenceID[i];
                }
                out.write(bytes);
                sequence++;
                System.out.println(ThreadClient.currentThread().getName()+": 发送"+sequence+"   成功");
            }
            out.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public  static void test2(){
        ArrayList<ThreadClient> threads=new ArrayList<ThreadClient>();
        for(int i=0;i<1;i++) {
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
            File filename=new File("D:\\test.zip");
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