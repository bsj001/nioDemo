package com.atguigu.channel;
import java.lang.String;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * 一、通道(Channel)：用于源节点与目标节点的连接，在Java NIO中负责缓冲区中数据的传输，Channel本身不存储数据，因此需要配合缓冲区进行传输。
 * 二、通道的主要实现类
 *  java.nio.channels.Channel接口：
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 *
 * 三、获取通道
 * 1，Java针对支持通道的类提供了getChannel()方法
 *      本地IO:
 *          FileInputStream/FileOutputStream
 *          RandomAccessFile
 *
 *          网络IO：
 *          Socket
 *          ServerSocket
 *          DatagramSocket
 *
 * 2,在JDK 1.7中的NIO2针对各个通道提供了静态方法open()
 * 3,在JDK 1.7中的NIO2的Files 工具类的newByteChannel()
 *
 * 四、通道之间的数据传输
 * transferFrom()
 * transferTo()
 *
 * 五、分散(Scatter)与聚集(Gather)
 *  分散读取(Scattering Reads):将通道中的数据分散到多个缓冲区中 注意：按照缓冲中的顺序，从Channel中读取的数据依次将Buffer填满。
 *  聚集写入(Gathering writes):将多个缓冲区中的数据聚集到通道中 注意：按照缓冲中的顺序，写入position和limit之间的数据到Channel。
 *
 * 六、字符集：Charset
 *    编码：字符串->字符数组
 *    解码：字符数组->字符串
 */
public class ChannelTest {

    @Test
    public void test6(){
        Charset cs1 = Charset.forName("GBK");

        //获取编码器
        CharsetEncoder ce = cs1.newEncoder();

        //获取解码器
        CharsetDecoder cd = cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("尚硅谷NB");
        cBuf.flip();

        //编码
        ByteBuffer bBuf = null;
        try {
            bBuf = ce.encode(cBuf);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < bBuf.limit(); i++) {
            System.out.println(bBuf.get());
        }

//        解码
        bBuf.flip();
        CharBuffer cBuf2 = null;
        try {
            cBuf2 = cd.decode(bBuf);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
        System.out.println(cBuf2);
    }

    @Test
    public void test5(){
        SortedMap<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> entries = map.entrySet();
        for(Map.Entry<String,Charset> entry:entries){
            System.out.println(entry.getKey()+"="+entry.getValue());
        }

        //
    }

    private FileChannel channel1;
    private RandomAccessFile raf2;
    private FileChannel channel2;

    //分散与聚集
    @Test
    public void test4(){
        RandomAccessFile raf1 = null;
        try {
            //1,获取通道
            raf1 = new RandomAccessFile("src/main/resources/1.txt","rw");
            channel1 = raf1.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer buf1 = ByteBuffer.allocate(100);
            ByteBuffer buf2 = ByteBuffer.allocate(1024);

            //3,分散读取
            ByteBuffer[] bufs = {buf1,buf2};
            channel1.read(bufs);

            for(ByteBuffer byteBuffer:bufs){
                byteBuffer.flip();
            }

            //打印
            System.out.println(new String(bufs[0].array(),0,bufs[0].limit()));
            System.out.println("===========================");
            System.out.println(new String(bufs[1].array(),0,bufs[1].limit()));

            //4，聚集写入
            raf2 = new RandomAccessFile("src/main/resources/2.txt","rw");
            channel2 = raf2.getChannel();
            channel2.write(bufs);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(channel1 != null){
                try {
                    channel1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(raf1 != null){
                try {
                    raf1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(channel2 != null){
                try {
                    channel2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(raf2 != null){
                try {
                    raf2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    //通道之间的数据传输（直接缓冲区）
    @Test
    public void test3() throws IOException {
        inChannel = FileChannel.open(Paths.get("src/main/resources/1.jpg"), StandardOpenOption.READ);
        outChannel = FileChannel.open(Paths.get("src/main/resources/2.jpg"),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);

//        inChannel.transferTo(0,inChannel.size(),outChannel);
        outChannel.transferFrom(inChannel,0,inChannel.size());

        inChannel.close();
        outChannel.close();
    }

    private MappedByteBuffer inMappedBuf;
    private MappedByteBuffer outMappedBuf;

    //使用直接缓冲区完成文件的复制（内存映射文件）
    @Test
    public void test2(){
        try{
            inChannel = FileChannel.open(Paths.get("src/main/resources/1.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("src/main/resources/3.jpg"),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);

            inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

            //直接对缓冲区进行数据的读写操作
            byte[] dst = new byte[inMappedBuf.limit()];
            inMappedBuf.get(dst);
            outMappedBuf.put(dst);
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if (outChannel != null){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private FileInputStream fis;
    private FileOutputStream fos;
    private FileChannel inChannel;
    private FileChannel outChannel;

    //利用通道完成文件的复制(非直接缓冲区)
    @Test
    public void test1(){
        try{
            fis = new FileInputStream("src/main/resources/1.jpg");
            fos = new FileOutputStream("src/main/resources/3.jpg");

            //1，获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //2,分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            //3，将通道中的数据存入缓冲区
            while(inChannel.read(buf) != -1){
                buf.flip();//切换读取数据的模式
                outChannel.write(buf);
                buf.clear();//清空缓冲区
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (outChannel != null){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }
}
