package server.until;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.protocol.ConstantValue;
import server.protocol.WsProtocol;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class ThreadTask implements Runnable, Serializable {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);
    private WsProtocol wsProtocol;

    public ThreadTask(WsProtocol wsProtocol){
        this.wsProtocol=wsProtocol;
    }

    @Override
    public void run() {
        String deviceID=ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getDeviceIdHigh()),ServerUntil.longToBytes(wsProtocol.getDeviceIdLow()));
        String sessionID=ServerUntil.bytesToStringUUID(ServerUntil.longToBytes(wsProtocol.getSessionIdHigh()),ServerUntil.longToBytes(wsProtocol.getSessionIdLow()));
        String sequenceID=String.valueOf(wsProtocol.getSequenceId());
        Thread.currentThread().setName(deviceID+":"+sessionID+":"+sequenceID);
        String deviceDir= ConstantValue.fileDir+ File.separatorChar+deviceID;
        String sessionDir=deviceDir+File.separatorChar+sessionID;
        String file=sessionDir+File.separatorChar+sequenceID;
        ServerUntil.hasDirectoryWithCreate(deviceDir);
        ServerUntil.hasDirectoryWithCreate(sessionDir);
        try {
            RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write(wsProtocol.getContent());
            randomFile.close();
            LOGGER.debug("线程："+Thread.currentThread().getName()+"执行成功!");
        }catch(Exception e) {
            LOGGER.error("线程："+Thread.currentThread().getName()+"文件写入出错!");
            throw new RuntimeException(e);
        }
    }
}
