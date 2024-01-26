package com.os.process;

import com.os.file.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Cpu类
 * 21bigData lpy
 * @author lpy
 *
 */
public class Cpu {

    public static List<Pcb> rq = new ArrayList<>(); // 就绪队列，最多10个
    public static List<Pcb> bq = new ArrayList<>(); // 阻塞队列

    /**
     * 添加进程到就绪队列，
     */
    public static void addProcessToRq(String path){

        int idx = findEmptyRqIdx();
        if (idx != -1) {
            String s = FileUtil.cat(path);
            System.out.println("s = " + s); // start;x=1;x++;x--;end;
            String[] arr = s.split(";");
            // 创建新的pcb
            List<String> list = new ArrayList<>();
            for (int i = 0; i < arr.length; i++) {
                System.out.println(arr[i]);
                list.add(arr[i]);
            }
            rq.add(new Pcb(list, path)); // pcb加入就绪队列
        } else {
            System.out.println("就绪队列已满");
        }
    }

    /**
     * 寻找空白就绪队列位置，如果没有则返回-1,这里用的list，只判断是否超过10个即可
     * @return
     */
    public static int findEmptyRqIdx() {
        if (rq.size() > 10) return -1;
        return 1;
    }

    /**
     * 启动cpu，运行一次，为了显示过程，不然计算太快，多次调用利用前端time取执行即可
     * 返回【现在正在执行的进程，现在正在执行的操作，变量值】
     */
    public static String[] startOnce(){ // cpu运行一次
        Random rand = new Random();
        String name = "";
        String cil = "";
        int x = 0;
        // 一直循环，直到就绪队列为空
        if (!rq.isEmpty()){
            int rdnIdx = rand.nextInt(rq.size()); // 随机取就绪队列中一共进程执行一次，时间片为1
            Pcb cur = rq.get(rdnIdx);
            name = cur.name;
            cil = cur.runOne(); // 运行一次，并获取当前运行操作
            x = cur.getX(); // 获取变量值
            // System.out.println(name + "  现在在执行的操作：" + cil);
            // System.out.println(name + "  进程中变量的值：" + x);
            if (cil.equals("end")) { // 表示此进程运行完了
                rq.remove(rdnIdx); // 队列中去除
            } else if (cil.equals("进入阻塞队列")) {
                Pcb t = cur;
                rq.remove(cur);
                bq.add(t);
            }
        }
        // 每执行一次，就判断有无空闲设备唤醒
        for (int i = 0; i < 3; i++) {
            if (Pcb.abc[i] == 0) { // 设备空闲，唤醒等待线程的对应线程
                for (int j = 0; j < bq.size(); j++) {
                    Pcb t = bq.get(j);
                    if (t.needDevice == 'a' && i == 0 || t.needDevice == 'b' && i == 1 || t.needDevice == 'c' && i == 3) {
                        Pcb t2 = t;
                        bq.remove(t);
                        rq.add(t2);
                        j--; // 不要忘了指针回溯哦
                    }
                }
            }
        }
        return new String[]{name, cil, String.valueOf(x)};
    }
}
