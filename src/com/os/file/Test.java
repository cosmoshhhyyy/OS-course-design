package com.os.file;

import java.io.File;
import java.io.IOException;
/**
 * 用于测试
 * 21bigData lpy
 * @author lpy
 *
 */
public class Test {
    //    0，1，2文件名字
//    3，4拓展名
//    5 目录还是文件
//    6 起始块号
//    7 文件长度
    public static void main(String[] args) throws IOException {
        System.out.println("新建目录开始=========");
        // test
        FileUtil.mkdir("rot");
        FileUtil.mkdir("rot/aaa");
        FileUtil.mkdir("rot/aaa/bbb");
        FileUtil.mkdir("rot/ccc");
         FileUtil.mkdir("rot/ccc/ddd");
        System.out.println("新建目录结束=========");
        FileUtil.show();
        String str = "";
        for (int i = 0; i < 65; i++) {
            str += "a";
        }
        str+="bcd";
        System.out.println("新建文件开始=========");
        FileUtil.touch("rot/aaa/cc.tt", str);
        //FileUtil.touch("rot/ccc/ee.tt", str);
        System.out.println("新建文件结束=========，测试成功");
        FileUtil.show();
        System.out.println("查看测试开始=========");
        String cat = FileUtil.cat("rot/aaa/cc.tt");
        System.out.println(cat);
        System.out.println("查看测试结束=========， 测试成功");

        FileUtil.show();

        System.out.println("删除文件测试开始=========");
        FileUtil.rm("rot/aaa/cc.tt");
        FileUtil.show();
        System.out.println(cat);
        System.out.println("删除文件测试结束=========，测试成功");

        System.out.println("删除目录测试开始=========");

        // FileUtil.rm("rot");
        System.out.println("删除目录测试结束=========,测试成功");
        FileUtil.show();

        System.out.println("移动测试开始=========");
        FileUtil.touch("rot/ccc/ee.xx", "123");
        FileUtil.mv("rot/ccc/ee.xx", "rot/aaa/ff.xx");
        System.out.println("移动测试结束=========, 测试成功");

        System.out.println("拷贝测试开始=========");
        FileUtil.touch("rot/ccc/yy.xx", "123");
        FileUtil.cp("rot/ccc/yy.xx", "rot/aaa/kk.xx");
        System.out.println("移拷贝测试结束=========, 测试成功");
//        for (int i = 64 * 3; i < 64 * 128; i++) {
//            System.out.println(FileUtil.disk[i]);
//        }
    }
}
