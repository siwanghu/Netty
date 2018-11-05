package server.until;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.protocol.ConstantValue;
import server.protocol.WsProtocolRequest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class ServerUntil {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);

    public byte[] merge(byte a[],byte b[]) {
        byte c[]= Arrays.copyOf(a, a.length+b.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }

    private UUID fromByte(byte[] data) {
        if (data.length != 16) {
            throw new IllegalArgumentException("Invalid UUID byte[]");
        }
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);

        return new UUID(msb, lsb);
    }

    public String bytesToStringUUID(byte[] high,byte[] low){
        UUID UUID=fromByte(merge(high,low));
        return UUID.toString();
    }

    public void hasDirectoryWithCreate(String filename){
        File file=new File(filename);
        if(!file.exists()&&!file.isDirectory())
            file .mkdir();
    }

    public void hasFileWithCreate(String filename){
        File file=new File(filename);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void recordPackage(WsProtocolRequest wsProtocol){
        String deviceID=this.bytesToStringUUID(this.longToBytes(wsProtocol.getDeviceIdHigh()),this.longToBytes(wsProtocol.getDeviceIdLow()));
        String sessionID=this.bytesToStringUUID(this.longToBytes(wsProtocol.getSessionIdHigh()),this.longToBytes(wsProtocol.getSessionIdLow()));
        String sequenceID=String.valueOf(wsProtocol.getSequenceId());
        LOGGER.debug("收到包:"+deviceID+":"+sessionID+":"+sequenceID);
        String deviceDir= ConstantValue.FILE_DIR+ File.separatorChar+deviceID;
        String sessionDir=deviceDir+File.separatorChar+sessionID;
        String file=sessionDir+File.separatorChar+sequenceID;
        this.hasDirectoryWithCreate(deviceDir);
        this.hasDirectoryWithCreate(sessionDir);
        try {
            RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write(wsProtocol.getContent());
            randomFile.close();
        }catch(Exception e) {
            LOGGER.error("包："+deviceID+":"+sessionID+":"+sequenceID+"记录出错!");
            throw new RuntimeException(e);
        }
    }

    public void echoPackage(WsProtocolRequest wsProtocol){
        LOGGER.debug("--------------收到包------------------");
        LOGGER.debug("头部："+Arrays.toString(this.intToByteArray(wsProtocol.getHead_data())));
        LOGGER.debug("版本号："+Arrays.toString(this.intToByteArray(wsProtocol.getVersionId())));
        LOGGER.debug("设备id高8位："+Arrays.toString(this.longToBytes(wsProtocol.getDeviceIdHigh())));
        LOGGER.debug("设备id低8位："+Arrays.toString(this.longToBytes(wsProtocol.getDeviceIdLow())));
        LOGGER.debug("对应设备UUID:"+this.bytesToStringUUID(this.longToBytes(wsProtocol.getDeviceIdHigh()),this.longToBytes(wsProtocol.getDeviceIdLow())));
        LOGGER.debug("回话id高8位："+Arrays.toString(this.longToBytes(wsProtocol.getSessionIdHigh())));
        LOGGER.debug("回话id低8位："+Arrays.toString(this.longToBytes(wsProtocol.getSessionIdLow())));
        LOGGER.debug("对应回话UUID:"+this.bytesToStringUUID(this.longToBytes(wsProtocol.getSessionIdHigh()),this.longToBytes(wsProtocol.getSessionIdLow())));
        LOGGER.debug("序号Id："+Arrays.toString(this.longToBytes(wsProtocol.getSequenceId())));
        LOGGER.debug("保留位："+Arrays.toString(this.longToBytes(wsProtocol.getReserve())));
        LOGGER.debug("校验码："+Arrays.toString(this.intToByteArray(wsProtocol.getCheck())));
        LOGGER.debug("数据长度:"+Arrays.toString(this.intToByteArray(wsProtocol.getContentLength())));
        LOGGER.debug("数据：");
        for(int i=0;i<wsProtocol.getContent().length;i++){
            LOGGER.debug(" "+wsProtocol.getContent()[i]);
        }
        LOGGER.debug("\n");
    }
}
