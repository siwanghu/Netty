package client;

import server.until.ServerUntil;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class ThreadClient extends Thread implements Runnable {
    private byte[] head=new byte[]{
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
    private byte[] data=new byte[1024];
    private byte[] tail="auditoryworks".getBytes();
    private Random random=new Random();
    private int[] device=new int[1000];
    private ServerUntil serverUntil=new ServerUntil();
    private int i;
    public ThreadClient(int i){
        this.i=i;
        for(int id=0;id<1000;id++){
            device[id]=id;
        }
    }

    @Override
    public void run() {
        try {
//            Socket client = new Socket("127.0.0.1", 7766);
            Socket client = new Socket("139.196.127.240", 7766);
            OutputStream out = client.getOutputStream();
            byte[] deviceIDHigh = serverUntil.longToBytes(device[i]);
            System.out.println(Arrays.toString(deviceIDHigh));
            for (int x = 0, y = 24; x < deviceIDHigh.length; x++, y++) {
                head[y] = deviceIDHigh[x];
            }
            System.out.println(Arrays.toString(deviceIDHigh));
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
                System.out.println(Thread.currentThread().getName()+": 发送"+sequence+"   成功");
            }
//            while(true){
//                byte[] bytes = method(method(head, data), tail);
//                byte[] sequenceID = ServerUntil.longToBytes(sequence);
//                byte[] length = ServerUntil.longToBytes(data.length);
//                for (int i = 0, j = 40; i < sequenceID.length; i++, j++) {
//                    bytes[j] = sequenceID[i];
//                }
//                out.write(bytes);
//                sequence++;
//                System.out.println(Thread.currentThread().getName()+": 发送"+sequence+"   成功");
//            }
            out.close();
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + "崩溃");
        }
    }

    private  byte[] method(byte a[],byte b[]) {
        byte c[]= Arrays.copyOf(a, a.length+b.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
