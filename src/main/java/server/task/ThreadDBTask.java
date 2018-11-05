package server.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.pool.RDBPool;
import server.protocol.WsProtocolRequest;
import server.until.ServerUntil;

import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;

public class ThreadDBTask implements Runnable, Serializable {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);
    private WsProtocolRequest wsProtocol;
    private List<WsProtocolRequest> buffer;
    private ServerUntil serverUntil;

    public ThreadDBTask(WsProtocolRequest wsProtocol){
        this.wsProtocol=wsProtocol;
        this.buffer=null;
        serverUntil=new ServerUntil();
    }

    public ThreadDBTask(List<WsProtocolRequest> buffer){
        this.buffer=buffer;
        this.wsProtocol=null;
        serverUntil=new ServerUntil();
    }

    @Override
    public void run() {
        Connection connection= RDBPool.getConnection();
        try {
            Statement statement=connection.createStatement();
            if (wsProtocol != null) {
                String deviceID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(wsProtocol.getDeviceIdHigh()), serverUntil.longToBytes(wsProtocol.getDeviceIdLow()));
                String sessionID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(wsProtocol.getSessionIdHigh()), serverUntil.longToBytes(wsProtocol.getSessionIdLow()));
                String sequenceID = String.valueOf(wsProtocol.getSequenceId());
                String stream = deviceID + ":" + sessionID;
                String id = deviceID + ":" + sessionID + ":" + sequenceID;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
                String time=df.format(new java.util.Date());
                String tablename=sessionID+":"+time;
                String sql = "CREATE TABLE IF NOT EXISTS"+"`"+tablename +"`"+ "(id VARCHAR(512) not NULL, content VARCHAR(5120) not NULL, device VARCHAR(512) not NULL, session VARCHAR(512) not NULL, PRIMARY KEY (id));";
                statement.executeUpdate(sql);
                String insert="INSERT INTO "+"`"+tablename+"`"+" VALUES("+"'"+id+"'"+","+"'"+Base64.getEncoder().encodeToString(wsProtocol.getContent())+"'"+","+"'"+deviceID+"'"+","+"'"+sessionID+"'"+");";
                statement.executeUpdate(insert);
            }else{
                for(int i=0;i<buffer.size();i++){
                    String deviceID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(buffer.get(i).getDeviceIdHigh()), serverUntil.longToBytes(buffer.get(i).getDeviceIdLow()));
                    String sessionID = serverUntil.bytesToStringUUID(serverUntil.longToBytes(buffer.get(i).getSessionIdHigh()), serverUntil.longToBytes(buffer.get(i).getSessionIdLow()));
                    String sequenceID = String.valueOf(buffer.get(i).getSequenceId());
                    String stream = deviceID + ":" + sessionID;
                    String id = deviceID + ":" + sessionID + ":" + sequenceID;
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
                    String time=df.format(new java.util.Date());
                    String tablename=sessionID+":"+time;
                    String sql = "CREATE TABLE IF NOT EXISTS"+"`"+tablename +"`"+ "(id VARCHAR(512) not NULL, content VARCHAR(5120) not NULL, device VARCHAR(512) not NULL, session VARCHAR(512) not NULL, PRIMARY KEY (id));";
                    statement.executeUpdate(sql);
                    String insert="INSERT INTO "+"`"+tablename+"`"+" VALUES("+"'"+id+"'"+","+"'"+Base64.getEncoder().encodeToString(wsProtocol.getContent())+"'"+","+"'"+deviceID+"'"+","+"'"+sessionID+"'"+");";
                    statement.executeUpdate(insert);
                }
            }
        }catch (Exception e){
            LOGGER.error("sql语句执行失败!");
            throw new RuntimeException(e);
        }finally {
            RDBPool.close(null,null,connection);
        }
    }
}
