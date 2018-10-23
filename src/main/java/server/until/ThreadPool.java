package server.until;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;
import server.protocol.ConstantValue;
import server.protocol.WsProtocol;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(50, 100, 10,
            TimeUnit.SECONDS, new LinkedBlockingQueue());

    public static void addTask(ChannelHandlerContext ctx, Object msg){
        WsProtocol wsProtocol=(WsProtocol)msg;
        threadPool.execute(new ThreadTask(wsProtocol));
    }
}
