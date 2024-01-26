package com.os.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
/**
 * 文件类
 * 21bigData lpy
 * @author lpy
 *
 */
public class FileUtil {

	/*
    顺序遍历
   0，1，2文件名字
   3，4拓展名
   5 目录还是文件， 1为目录，0为文件
   6 起始块号
   7 文件长度
   -1 为文件结束链接标志
  文件名，最长3个字母，拓展名2字母

  说明：
   目录和文件要一层一层件，不能跳越，方法也不一样touch和mkdir
   会根据要求，创建真实的文件夹在项目中，但是不包含内容，只作为文件结构展示，当然模拟的disk中也会创建。
   名字最长3字母 拓展名最长为2字母
   写文件名字的时候要加上拓展名
   mkdir 新建目录 1
   touch 新建文件 1
   cat查看文件 
   rm可以删除文件和目录 1
   cp 拷贝文件，要拷贝有效文件 1
   mv 移动文件 要移动有效文件 1

有些地方完全可以避免重复代码，但是我懒得写了，能实现效果就行*/
    public final static int LEN = 128 * 64; // 长度

    public static byte[] disk = new byte[LEN]; // 磁盘

    // 初始化，前三个块已经被占用了
    static {
        disk[0] = 1;
        disk[1] = 1;
        disk[2] = 1;
    }

    /**
     * 寻找空白块的编号，找不到返回-1
     * @return
     */
    public static int findEmptyBlock(){
        for (int i = 3; i < 64 * 2; i++) {
            if (disk[i] == 0) {
                // System.out.println("找到的空块为：  " + i);
                return i;
            }
        }

        return -1;
    }

    /**
     * 创建目录
     * @param path 创建路径（包含目录名）
     */
    public static void mkdir(String path){ //
        String[] arr = path.split("/");
        String name = arr[arr.length - 1]; // 获取目录名

        int idx = findEmptyBlock(); // 第一个空白位置
        int startidx = 2 * 64; // 从root根目录寻找
        if (idx != -1) { // 找到了空白块
            // 创建逻辑文件夹
            for (int i = 0; i < arr.length - 1; i++) {
                for (int j = 0; j < 8; j++) {
                    String curname = ""; // 当前文件名
                    if (disk[startidx + j * 8] > 0) curname += (char)disk[startidx + j * 8 + 0];
                    if (disk[startidx + j * 8 + 1] > 0) curname += (char)disk[startidx + j * 8 + 1];
                    if (disk[startidx + j * 8 + 2] > 0) curname += (char)disk[startidx + j * 8 + 2];
                    if (curname.equals(arr[i])) { // 若找到了当前层级目录
                        startidx = disk[startidx + j * 8 + 6] * 64; // 下一层起始位置
                        break; // 向下一层寻找
                    }
                }
            }
            new File(path).mkdir();
        }

        disk[idx] = (byte)1; // 先占用了

        // startidx 找到了存放位置，开始建立目录，找空位置,根据是否占有磁盘来查看是否为空位置
        for (int i = 0; i < 8; i++) {
            if (disk[startidx + i * 8 + 6] == 0) {
                int j = 0;
                for (char c: name.toCharArray()) { // 放名字
                    disk[startidx + i * 8 + j] = (byte) c;
                    j++;
                }
                disk[startidx + i * 8 + 6] = (byte)idx; // 填入 起始块号
                disk[startidx + i * 8 + 5] = 1; // 标记为目录
                disk[startidx + i * 8 + 7] = 64; // 目录的长度，一块，64
                break;
            }
        }
    }

    /**
     * 展示磁盘使用
     */
    public static void show() {
        for (int i = 0; i < 64 * 2; i++) {
            System.out.print(disk[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * 创建文件
     * @param path 传入路径（包含文件名）
     *        content 写入内容
     */
    public static void touch(String path, String content) throws IOException {
        String[] arr = path.split("/");
        String truename = arr[arr.length - 1]; // 获取文件名（全名包含拓展）
        // System.out.println(truename);
        String ex = truename.split("\\.")[1]; // 获取拓展名
        // System.out.println(ex);
        String name = truename.split("\\.")[0]; // 这才是名字
        int idx = findEmptyBlock();

        int startidx = 2 * 64; // 从root根目录寻找,找到父级文件
        // 创建逻辑文件夹
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < 8; j++) {
                String curname = ""; // 当前文件名
                if (disk[startidx + j * 8] > 0) curname += (char)disk[startidx + j * 8 + 0];
                if (disk[startidx + j * 8 + 1] > 0) curname += (char)disk[startidx + j * 8 + 1];
                if (disk[startidx + j * 8 + 2] > 0) curname += (char)disk[startidx + j * 8 + 2];
                if (curname.equals(arr[i])) { // 若找到了当前层级目录
                    startidx = disk[startidx + j * 8 + 6] * 64; // 下一层起始位置
                    break; // 向下一层寻找
                }
            }
        }

        // 真实创建
        String parent = ""; // 拼接父级路径
        for (int i = 0; i < arr.length - 1; i++) {
            parent += arr[i] + "/";
        }
        new File(parent, truename).createNewFile(); // 创建文件

        int num = getEmptynum(); // 空白的数量
        int neednum = (content.length() + 63) / 64; // 需要的磁盘数
        if (num >= neednum) {
            // 先写入内容再填写pcb,链式
            int pre = idx;
            int z = 0; // 遍历字符串
            int q = idx; // 记录最后一个位置
            for (int i = idx; i < 2 * 64; i++) {
                if (disk[i] == 0) {
                    if (pre != i) {
                        disk[pre] = (byte) i;
                    }
                    for (int j = 0; j < 64 && z < content.length(); j++) {
                        disk[i * 64 + j] = (byte) content.charAt(z++); // 填写内容
                    }
                    pre = i;
                    q = i;
                }
                if (z == content.length()) break; // 存放完了
            }
            disk[q] = -1; // 结束标志

            // startidx 找到了存放位置
            for (int i = 0; i < 8; i++) {
                if (disk[startidx + i * 8 + 6] == 0) {
                    int j = 0;
                    for (char c: name.toCharArray()) { // 放名字
                        disk[startidx + i * 8 + j] = (byte) c;
                        j++;
                    }
                    int k = 3;
                    for (char c: ex.toCharArray()) { // 放拓展名
                        disk[startidx + i * 8 + k] = (byte) c;
                        k++;
                    }
                    disk[startidx + i * 8 + 5] = 0; // 标记为文件
                    disk[startidx + i * 8 + 6] = (byte)idx; // 填入 起始块号
                    disk[startidx + i * 8 + 7] = (byte) content.length(); // 文件的长度
                    // System.out.println("输入的文件长度：" + disk[startidx + i * 8 + 7]);
                    break; // 一开始忘写了，找半天错，绷不住了
                }
            }
        }
    }

    /**
     * 返回剩余空块的个数
      */
    public static  int getEmptynum() {
        int res = 0;
        for (int i = 3; i < 64 * 2; i++) {
            if (disk[i] == 0) {
                res ++;
            }
        }
        return res;
    }

    /**
     * 查看文件内容
     * @param path 传入路径（包含要查看的文件名）
     * @return
     */
    public static String cat(String path) {

        String res = "";
        String[] arr = path.split("/");
        String truename = arr[arr.length - 1]; // 获取文件名（全名包含拓展）
        // System.out.println(truename);
        String name = truename.split("\\.")[0]; // 这才是名字

        arr[arr.length - 1] = arr[arr.length - 1].split("\\.")[0]; // 去掉拓展名，不做为对比，方便一点，不然下面代码还要改特殊处理
        // rot aaa cc
        // 查找文件位置
        int startidx = 2 * 64; // 从root根目录寻找，最终表示文件位置
        int len = 0; // 文件长度
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < 8; j++) {
                String curname = ""; // 当前文件名
                // System.out.println("进入循环");
                if (disk[startidx + j * 8] > 0) curname += (char)disk[startidx + j * 8 + 0];
                if (disk[startidx + j * 8 + 1] > 0) curname += (char)disk[startidx + j * 8 + 1];
                if (disk[startidx + j * 8 + 2] > 0) curname += (char)disk[startidx + j * 8 + 2];
                // System.out.println("curname: " + curname);
                if (curname.equals(arr[i])) { // 若找到了当前层级目录
                    // System.out.println("curname: " + curname);
                    len = disk[startidx + j * 8 + 7]; // 这个一定，要放在，下一行代码的前面，擦，又找半天错
                    startidx = disk[startidx + j * 8 + 6] * 64; // 下一层起始位置

                    break; // 向下一层寻找
                }
            }
        }
        // System.out.println(startidx / 64);
        // System.out.println("长度：" + len);
        int startBlockIdx = startidx / 64; // 文件起始块号
        // 递归
        while (true) {
            // System.out.println("现在正在遍历的盘号：" + startBlockIdx);
            int cur = startBlockIdx * 64; // 每一块的起始位置
            // 读块
            for (int i = 0; i < 64 && len > 0; i++, len--) {
                res += (char)disk[cur + i];
            }
            if (disk[startBlockIdx] == -1) break; // 跳出循环
            startBlockIdx = disk[startBlockIdx];
        }
        return res;
    }

    /**
     * 展示全部bytes数组，用于测试
     */
    public static void showAll() {
        for (int i = 0; i < 128 * 64; i++) {
            System.out.print((char)disk[i]);
        }
    }

    /**
     * 删除文件 或 目录，在此方法中，是判断是删除的文件还是目录，若是目录再bfs删除。
     * @param path
     */
    public static void rm(String path) {
        String[] arr = path.split("/");
        String truename = arr[arr.length - 1]; // 获取文件名（全名包含拓展）
        // System.out.println(truename);
        String name = truename.split("\\.")[0]; // 这才是名字

        arr[arr.length - 1] = arr[arr.length - 1].split("\\.")[0]; // 去掉拓展名，不做为对比，方便一点，不然下面代码还要改特殊处理

        int startidx = 2 * 64;
        int len = 0; // 文件长度
        int flag = 0; // 判断删除的文件还是目录，0为文件，1为目录
        // 查找文件位置
        int delIdx = -1; // 删除的fcb位置起始

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < 8; j++) {
                String curname = ""; // 当前文件名
                // System.out.println("进入循环");
                if (disk[startidx + j * 8] > 0) curname += (char)disk[startidx + j * 8 + 0];
                if (disk[startidx + j * 8 + 1] > 0) curname += (char)disk[startidx + j * 8 + 1];
                if (disk[startidx + j * 8 + 2] > 0) curname += (char)disk[startidx + j * 8 + 2];
                // System.out.println("curname: " + curname);
                if (curname.equals(arr[i])) { // 若找到了当前层级目录
                    // System.out.println("curname: " + curname);
                    len = disk[startidx + j * 8 + 7];
                    flag = disk[startidx + j * 8 + 5]; // 给出文件类型
                    delIdx = startidx + j * 8;
                    startidx = disk[startidx + j * 8 + 6] * 64; // 下一层起始位置
                    break; // 向下一层寻找
                }
            }
        }
        // System.out.println("len: " + len);
        if (flag == 0) { // 删除文件 ，直接复用cat的代码

            delFile(startidx, delIdx);
            File f = new File(path);
            f.delete();

        } else { // 如果是目录
            // bfs 删除
            delDir(delIdx); // 此层fcb位置
            deleteDirectory(path);
        }
    }

    /**
     * 删除文件，私有
     * @param startidx 删除文件的位置
     * @param delIdx 需要删除的fcb位置
     *
     */
    private static void delFile(int startidx, int delIdx) {

        // 删除fcb先
        for (int i = 0; i < 8; i++) {
            disk[delIdx + i] = 0; // 消除该文件fcb
        }
        int startBlockIdx = startidx / 64; // 文件起始块号
        // System.out.println("文件起始块号" + startBlockIdx);
        // 递归
        while (true) {
            // System.out.println("现在正在遍历的盘号：" + startBlockIdx);
            int t = disk[startBlockIdx]; // 零时的 7
            disk[startBlockIdx] = 0; // 赋值为0咯
            if (t == -1) break; // 跳出循环
            startBlockIdx = t;
        }
        // 真实删除文件，这是目录删除，我们真实直接把目录删了，文件就不用了
    }

    /**
     * 删除当前目录，私有,我这里所以删除操作，都是基于文件FCB位置删除，而不是块号，方便寻找
     * @param delIdx 需要删除的fcb位置,当前fcb位置
     * @param
     */
    private static void delDir(int delIdx){

        // System.out.println("起始块号：" + disk[delIdx + 6]);
        // BFS
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(delIdx);
        while (!queue.isEmpty()) {
            int t = queue.peek(); // 获取fcb位置开头delIdx
            queue.poll(); // 出队
            int startidx = disk[t + 6] * 64; // 此文件内存所在位置
            // System.out.println("文件内容位子=" + startidx);
            // System.out.println("delIdx=" + t);
            for (int i = 0; i < 8; i++) { // bfs一层之后
                int nextstartidx = disk[startidx + i * 8 + 6] * 64; // 此文件所在位置
                int nextdelIdx = startidx + i * 8; // fcb位置，当前层
                int ha = disk[startidx + i * 8 + 6]; // 是否存在
                int flag2 = disk[startidx + i * 8 + 5]; // 文件还是目录 1为目录，0为文件
                if (ha != 0) { // 存在此fcb
                    if (flag2 == 0) { // 文件，直接删除
                        delFile(nextstartidx, nextdelIdx);
                    } else { // 为目录，加入q
                        // System.out.println("nextdelIdx" + nextdelIdx);
                        queue.offer(nextdelIdx);
                    }
                }
            }

            disk[startidx / 64] = 0; // 位视图置为0
            // bfs一个树之后，删除fcb，一定要遍历完，再删
            for (int i = 0; i < 8; i++) {
                disk[t + i] = 0; // 这里是 t!!!!!不是delidx,每次删除已经遍历完的树的根节点
            }

        }
    }

    /**
     * 删除单个文件
     * @param fileName：要删除的文件的文件名,用于真实删除,千万别调用
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                 System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                 System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
    /**
     * 删除目录及目录下的文件
     * @param dir：要删除的目录的文件路径，用于真实删除,千万别调用
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 移动文件位置，只能移动文件
     * @param p1 原路径
     * @param p2 目标路径
     */
    public static void mv(String p1, String p2) throws IOException {
        String[] arr = p2.split("/");
        String truename = arr[arr.length - 1]; // 获取文件名（全名包含拓展）
        String tmp = cat(p1); // 接收内容
        rm(p1); // 删除原内容
        touch(p2, tmp);
        File f1 = new File(p1);
        f1.delete();
        // 真实创建
        String parent = ""; // 拼接父级路径
        for (int i = 0; i < arr.length - 1; i++) {
            parent += arr[i] + "/";
        }
        new File(parent, truename).createNewFile(); // 创建文件
    }
    /**
     * 拷贝文件
     * @param p1 原路径
     * @param p2 目标路径
     */
    public static void cp(String p1, String p2) throws IOException {
        String[] arr = p2.split("/");
        String truename = arr[arr.length - 1]; // 获取文件名（全名包含拓展）
        String tmp = cat(p1); // 接收内容
        touch(p2, tmp);
        // 真实创建
        String parent = ""; // 拼接父级路径
        for (int i = 0; i < arr.length - 1; i++) {
            parent += arr[i] + "/";
        }
        new File(parent, truename).createNewFile(); // 创建文件
    }
    
    /**
     * 获取磁盘使用状况
     */
    public static byte[] getDisk() {
    	
    	byte[] res = new byte[128];
    	for (int i = 0; i < 128; i++) {
    		res[i] = disk[i];
    	}
    	return res;
    }
    /**
     * 修改文件
     */
    public static void vi(String path, String content) throws IOException {
        FileUtil.rm(path);
        FileUtil.touch(path, content);
    }
    
    // 加载目录树
    public static String  getTree(int index) {
        int num = 0;
        String dir = ",\"children\":[";

        for(int k = 0; k < 8; k++) {
            if(disk[index * 64 + k * 8] > 0) {
                num++;
                String xname = "";
                xname += (char)disk[index * 64 + k * 8];
                if(disk[index * 64 + k * 8 + 1] > 0) xname += (char)disk[index * 64 + k * 8 + 1];
                if(disk[index * 64 + k * 8 + 2] > 0) xname += (char)disk[index * 64 + k * 8 + 2];
                if(disk[index * 64 + k * 8 + 3] > 0) {
                    xname += "." + (char)disk[index * 64 + k * 8 + 3];
                }
                if (disk[index * 64 + k * 8 + 4] > 0) {
                	xname += (char)disk[index * 64 + k * 8 + 4];
                }
                // System.out.println("getTree:xname=" + xname );
                dir += "{\"name\":\""+  xname + "\"";
                if(disk[index * 64 + k * 8 + 5] == 1 && disk[index * 64 + k * 8 + 6] > 0) {
                    dir	+= getTree(disk[index * 64 + k * 8 + 6]);
                }
                dir	+= "},";
            }
        }
        // System.out.println("getTree:num=" + num );
        if(num == 0) {
            return "";
        }
        dir = dir.substring(0,dir.length() - 1);
        dir += "]";
        return dir;
    }
}
