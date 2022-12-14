package com.atguigu.nio;

import org.junit.Test;

import javax.annotation.processing.FilerException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、使用NIO完成网络通信的三个核心：
 *  1，通道(Channel)：负责连接
 *
 *      java.nio.channel.Channel接口：
 *          |--SelectableChannel
 *              |--SocketChannel
 *              |--ServerSocketChannel
 *              |--DatagramChannel
 *
 *              |--Pipe.SinkChannel
 *              |--Pipe.SourceChannel
 *
 *
 *
 *  2，缓冲区(Buffer):负责数据的存取
 *
 *
 *  3，选择器(Selector):是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况
 *
 *
 */

public class TestBlockingNIO {

    @Test
    public void client(){
        SocketChannel sChannel = null;
        FileChannel inChannel = null;
        try{
            //1,获取通道
            sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
            inChannel = FileChannel.open(Paths.get("src/main/resources/1.jpg"), StandardOpenOption.READ);

            //2，分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            //3,读取本地文件，并发送到服务器
            while(inChannel.read(buf) != -1){
                buf.flip();
                sChannel.write(buf);
                buf.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //4.关闭通道
            try {
                inChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                sChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    //服务器端
    @Test
    public void server(){
        FileChannel outChannel = null;
        ServerSocketChannel ssChannel = null;
        SocketChannel sChannel = null;
        try{
            //1,获取通道
            ssChannel = ServerSocketChannel.open();
            outChannel = FileChannel.open(Paths.get("src/main/resources/3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            //2,绑定连接
            ssChannel.bind(new InetSocketAddress(9898));
            //3.获取客户端连接的通道
            sChannel = ssChannel.accept();
            //4，接收客户端的数据，并保存到本地
            ByteBuffer buf = ByteBuffer.allocate(1024);
            while(sChannel.read(buf) != -1){
                buf.flip();
                outChannel.write(buf);
                buf.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //5,关闭通道
            if(ssChannel != null){
                try{
                    ssChannel.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(sChannel != null) {
                try {
                    sChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
