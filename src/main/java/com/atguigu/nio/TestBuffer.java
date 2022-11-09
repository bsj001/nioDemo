package com.atguigu.nio;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 一、缓冲区（Buffer)：在java NIO 中负责数据的存取。缓冲区就是数组，用于存储不同数据类型的数据。
 *
 * 根据数据类型不同（boolean除外），提供了相应类型的缓冲区。
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 *
 * 上述缓冲区的管理方式几乎一致，都是用allocate()获取缓冲区
 *
 * 二、缓冲区存取数据的两个核心方法
 * put()：存入数据到缓冲区中
 * get():获取缓冲区中的数据
 *
 *
 * 四、缓冲区中的四个核心属性：
 * capacity:容量，表示缓冲区中最大存储数据的容量，一旦声明不能改变。
 * limit：界限，表示缓冲区中可以操作数据的大小。(limit后数据不能进行读写）
 * position:位置，表示缓冲区中正在操作数据的位置。
 *
 * 0<=mark<=position<=limit<capacity
 *
 * mark:标记，表示记录当前position位置，可以通过reset()恢复到mark的位置
 *
 * hasRemaining缓冲区是否还有数据
 * remaining缓冲区中还剩下几个数据未读取
 *
 *
 * 五、直接缓冲区与非直接缓冲区
 * 非直接缓冲区：通过 allocation()方法分配的缓冲区、将缓冲区建立在JVM的内存中。
 * 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中。
 */


public class TestBuffer {

    @Test
    public void test3(){
        //分配直接缓冲区
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        //判断是否是直接缓冲区
        System.out.println(buf.isDirect());
    }
    @Test
    public void test2(){
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);

        buf.put(str.getBytes());
        buf.flip();

        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,2);
        System.out.println(new String(dst,0,2));

        System.out.println(buf.position());

        buf.mark();//标记位置

        buf.get(dst,2,2);//断续读取

        System.out.println(buf.position());

        buf.reset();//重置，恢复到mark时的位置
        System.out.println(buf.position());

        //判断缓冲区是否还有数据
        if(buf.hasRemaining()){
            //如果有，还剩下几个
            System.out.println(buf.remaining());
        }


    }

    @Test
    public void test1(){
        //1,分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println("===================allocate================");
        System.out.println(buf.position());
        System.out.println(buf.capacity());
        System.out.println(buf.limit());

        //2,利用Put()存入数据到缓冲区
        buf.put("abc".getBytes());


        System.out.println("===================put()================");
        System.out.println(buf.position());
        System.out.println(buf.capacity());
        System.out.println(buf.limit());

        //3,切换读取数据模式
        buf.flip();

        System.out.println("===================flip()================");
        System.out.println(buf.position());
        System.out.println(buf.capacity());
        System.out.println(buf.limit());

        //4,读取数据get()
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        System.out.println(Arrays.toString(bytes));

        System.out.println("===================get()================");
        System.out.println(buf.position());
        System.out.println(buf.capacity());
        System.out.println(buf.limit());

        //5,rewind()可重复读数据
        buf.rewind();

        System.out.println("===================rewind()================");
        System.out.println(buf.position());
        System.out.println(buf.capacity());
        System.out.println(buf.limit());


        //5,clear()清空缓冲区,但是缓冲区中的数据依然存在，但是处于”被遗忘“状态
        buf.clear();

        System.out.println("===================clear()================");
        System.out.println(buf.position());
        System.out.println(buf.capacity());
        System.out.println(buf.limit());

        System.out.println((char)buf.get());
    }

}
