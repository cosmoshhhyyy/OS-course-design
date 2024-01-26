package com.os.process;

import com.os.file.FileUtil;

import java.io.IOException;
/**
 * 用于测试
 * 21bigData lpy
 * @author lpy
 *
 */
public class Test3 {
    public static void main(String[] args) throws IOException {

        FileUtil.mkdir("rot");
        String s = "start;x=1;x++;x--;?a9;end;";  // 传入时，去除空格，用js去操作
        String s2 = "start;x=3;x++;x--;?a5;end;";
        FileUtil.touch("rot/aa.ex", s);
        FileUtil.touch("rot/bb.ex", s2);
        Cpu.addProcessToRq("rot/aa.ex");
        Cpu.addProcessToRq("rot/bb.ex");
        for (int i = 0; i < 30; i++) {
            Cpu.startOnce();
        }
    }
}
