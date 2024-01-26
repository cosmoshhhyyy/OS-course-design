package com.os.process;

import java.util.ArrayList;
import java.util.List;
/**
 * Pcb类，也包含了设备（静态）
 * 21bigData lpy
 * @author lpy
 *
 */
public class Pcb {

    public static int[] abc = {0, 0, 0}; // 三个个设备 abc，0为未被占用，>0表示剩余被占用的时间

    public List<String> list; // 进程信息，需要执行的任务

    public char useDevice = ' '; // 此进程占用的设备，空说明不占用
    private int x = 0; // 存放的数，若未赋值，默认为0

    public int idx = 0; // 现在要执行操作的位置

    public String name = ""; // PCb名字, a,b,c

    public char needDevice = ' '; // 如果在阻塞队列中，这表示需求等待的设备，设备被释放责被唤醒，abc
    /**
     * 构造，传入操作序列，进程名字
     * @param list
     * @param name
     */
    public Pcb(List<String> list, String name) {
        this.list = list;
        this.name = name;
    }

    // 设置x
    public void setX(int x) {
        this.x = x;
    }

    /**
     * x--
     */
    private void add() {
        this.x++;
    }

    /**
     * x++
     */
    private void std() {
        this.x--;
    }

    /**
     * 运行一次次进程，若运行完毕，返回正在执行的指令
     * @return
     */
    public String runOne() {
        String str = list.get(idx);
        char opt = str.charAt(1); // 判断要执行什么

        switch (opt) {
            case '=': // 赋值
                int timeOrval = Integer.parseInt(String.valueOf(str.charAt(2)));
                setX(timeOrval);
                break;
            case '+': // ++
                add();
                break;
            case '-': // --
                std();
                break;
            case 'a': // 需要占用一个设备
                if (abc[0] > 0) { // 已经被占用
                    if (this.useDevice == 'a') { // 说明是被当前进程占用
                        abc[0]--; // 剩余占用时间减一秒
                        if (abc[0] == 0) {
                            this.useDevice = ' '; // 如果运行完，释放a
                            idx++; // 下次执行下一条语句
                        }
                        return "a设备运行一秒";
                    } else { // 进入阻塞队列
                        this.needDevice = 'a'; // 需要等待的设备
                        return "进入阻塞队列";
                    }
                } else { // 未被占用
                    this.useDevice = 'a'; // 状态，1为正在使用设备且在就绪态
                    abc[0] = Integer.parseInt(String.valueOf(str.charAt(2))); // 运行时间
                    return "开始占用设备a";
                }
            case 'b':
                if (abc[1] > 0) { // 已经被占用
                    if (this.useDevice == 'b') { // 说明是被当前进程占用
                        abc[1]--; // 剩余占用时间减一秒
                        if (abc[1] == 0) {
                            this.useDevice = ' '; // 如果运行完，释放b
                            idx++; // 下次执行下一条语句
                        }
                        return "b设备运行一秒";
                    } else { // 进入阻塞队列
                        this.needDevice = 'b';
                        return "进入阻塞队列";
                    }
                } else { // 未被占用
                    this.useDevice = 'b'; // 状态，1为正在使用设备且在就绪态
                    abc[1] = Integer.parseInt(String.valueOf(str.charAt(2))); // 运行时间
                    return "开始占用设备b";
                }
            case 'c':
                if (abc[2] > 0) { // 已经被占用
                    if (this.useDevice == 'c') { // 说明是被当前进程占用
                        abc[2]--; // 剩余占用时间减一秒
                        if (abc[2] == 0) {
                            this.useDevice = ' '; // 如果运行完，释放c
                            idx++; // 下次执行下一条语句
                        }
                        return "b设备运行一秒";
                    } else { // 进入阻塞队列
                        this.needDevice = 'c';
                        return "进入阻塞队列";
                    }
                } else { // 未被占用
                    this.useDevice = 'c'; // 状态，设置该进程使用的的设备
                    abc[2] = Integer.parseInt(String.valueOf(str.charAt(2))); // 运行时间
                    return "开始占用设备c";
                }
        }
        idx++;

        return list.get(idx - 1);
    }

    /**
     * 获取当前x值
     * @return
     */
    public int getX() {
        return x;
    }
    /**
     * toString
     */
    public String toString() {
        return "{" +
                "" + name +
                '}';
    }
}
