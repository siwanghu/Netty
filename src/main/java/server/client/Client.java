package server.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws IOException {
        byte[] head=new byte[]{
                0x57,0x53,0x4B,0x4A,                      //头部
                0x00,0x00,0x00,0x01,                      //版本号
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x09, //设备id高位
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x08, //设备id低位
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x06, //回话id高位
                0x00,0x00,0x00,0x00,0x00,0x00,0x09,0x0F, //回话id低位
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01, //序号id
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, //保留字段
                0x00,0x00,0x00,0x00,                     //校验码
                0x00,0x00,0x04,0x00,                     //长度
        };

        byte[] data=new byte[1024];
        for(int i=0;i<data.length;i++){
            data[i]=(byte) 0xFF;
        }

        byte[] tail="auditoryworks".getBytes();

        Socket client = new Socket("127.0.0.1", 7766);
        OutputStream out =client.getOutputStream();
        byte[] bytes=method(method(head,data),tail);
        while(true) {
            System.out.println("发送数据包成功！");
            out.write(bytes);
        }
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