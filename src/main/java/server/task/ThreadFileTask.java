package server.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.protocol.ConstantValue;
import server.protocol.WsProtocolRequest;
import server.until.ServerUntil;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;

public class ThreadFileTask implements Runnable, Serializable {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);
    private WsProtocolRequest wsProtocol;
    private List<WsProtocolRequest> buffer;
    private ServerUntil serverUntil;

    public ThreadFileTask(WsProtocolRequest wsProtocol){
        this.wsProtocol=wsProtocol;
        this.buffer=null;
        serverUntil=new ServerUntil();
    }

    public ThreadFileTask(List<WsProtocolRequest> buffer){
        this.buffer=buffer;
        this.wsProtocol=null;
        serverUntil=new ServerUntil();
    }

    @Override
    public void run() {
        if(wsProtocol!=null) {
            String deviceID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(wsProtocol.getDeviceIdHigh()), serverUntil.longToBytes(wsProtocol.getDeviceIdLow()));
            String sessionID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(wsProtocol.getSessionIdHigh()), serverUntil.longToBytes(wsProtocol.getSessionIdLow()));
            String sequenceID = String.valueOf(wsProtocol.getSequenceId());
            Thread.currentThread().setName(deviceID + ":" + sessionID + ":" + sequenceID);
            String deviceDir = ConstantValue.FILE_DIR + File.separatorChar + deviceID;
            String sessionDir = deviceDir + File.separatorChar + sessionID;
            String file = sessionDir + File.separatorChar + sequenceID;
            serverUntil.hasDirectoryWithCreate(deviceDir);
            serverUntil.hasDirectoryWithCreate(sessionDir);
            try {
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
                long fileLength = randomFile.length();
                randomFile.seek(fileLength);
                randomFile.write(wsProtocol.getContent());
                wsProtocol.setContent(null);
                randomFile.close();
                LOGGER.debug("线程：" + Thread.currentThread().getName() + "执行成功!");
            } catch (Exception e) {
                LOGGER.error("线程：" + Thread.currentThread().getName() + "文件写入出错!");
                throw new RuntimeException(e);
            }
        }else {
            try {
                for(int i=0;i<buffer.size();i++){
                    String deviceID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(buffer.get(i).getDeviceIdHigh()), serverUntil.longToBytes(buffer.get(i).getDeviceIdLow()));
                    String sessionID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(buffer.get(i).getSessionIdHigh()), serverUntil.longToBytes(buffer.get(i).getSessionIdLow()));
                    String sequenceID = String.valueOf(buffer.get(i).getSequenceId());
                    String deviceDir = ConstantValue.FILE_DIR + File.separatorChar + deviceID;
                    String sessionDir = deviceDir + File.separatorChar + sessionID;
                    String file = sessionDir + File.separatorChar + sequenceID;
                    serverUntil.hasDirectoryWithCreate(deviceDir);
                    serverUntil.hasDirectoryWithCreate(sessionDir);
                    RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
                    long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(buffer.get(i).getContent());
                    buffer.get(i).setContent(null);
                    randomFile.close();
                    LOGGER.debug("后台线程：" + Thread.currentThread().getName() + "执行成功!");
                }
                LOGGER.debug("后台线程：" + Thread.currentThread().getName() + "执行结束!");
                this.buffer.clear();
                this.buffer=null;
            } catch (Exception e) {
                LOGGER.error("后台线程：" + Thread.currentThread().getName() + "文件写入出错!");
                throw new RuntimeException(e);
            }
        }
    }
}
