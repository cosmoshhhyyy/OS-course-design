package com.os.servlet;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.os.file.FileUtil;
import com.os.process.Cpu;
import com.os.process.Pcb;

/**
 * Servlet implementation class MainServlet
 * 21bigData lpy
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
				
		response.setContentType("text/json;charset=utf-8");
		
		String action = request.getParameter("action");
		String data = "";
		System.out.println("getaction:" + action);
		switch (action) {
		case "loadBlock": // 加载disk块状态
			byte[] diskState = FileUtil.getDisk();
			data += "{\"arr\":[";
			for (int i = 0; i < diskState.length - 1; i++) {
				data += String.valueOf(diskState[i]);
				data +=",";
			}
			data += String.valueOf(diskState[diskState.length - 1]);
			data += "]}";
			break;
		case "takeOnce":
			String[] arr = Cpu.startOnce();// rot/bb.ex a设备运行一秒 3
			// 当前进程信息
			data += "{" + "\"nowJcArr\":[\"" + arr[0] + "\",\"" + arr[1] + "\",\"" + arr[2] + "\"],";
			
			int[] devState = Pcb.abc; // 设备信息
			String rqInfo = Cpu.rq.toString(); // 获取队列中信息
			String bqInfo = Cpu.bq.toString(); // 获取队列中信息
			data += "\"devArr\":[";
			for (int i = 0; i < 2; i++) {
				data += devState[i] + ",";
			}
			data += devState[2] + "],";
			data += "\"rq\":\"" + rqInfo + "\"," + "\"bq\":\"" + bqInfo + "\"";
			data += "}";
			System.out.println(data);
			break;
		case "loadTree":
			String tt = FileUtil.getTree(2);
			String tree = tt.substring(12, tt.length());
			data += "{\"node\":" + tree + "}"; // {"node":[{"name":"rot","children":[{"name":"aa"}]}]}
			break;
		}
		System.out.println(data);
		response.getWriter().write(data);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
				
		response.setContentType("text/json;charset=utf-8");
		
		String action = request.getParameter("action");
		String data = "";
		System.out.println("postaction:" + action);
		switch (action) {
		case "cli": // 处理命令
			String ml = request.getParameter("ml");
			String path = request.getParameter("path");
			System.out.println("执行的ml:"+ml);
			
			switch (ml) {
			case "mkdir": // 创建目录
				FileUtil.mkdir(path);
				data += "{\"msg\":\"目录创建成功\"} ";
				break;
			case "touch": // 创建文件
				String code = request.getParameter("code");
				// System.out.println("执行的code:"+code);
				FileUtil.touch(path, code);
				data += "{\"msg\":\"文件创建成功\"} ";
				break;
			case "rm": // 删除文件或目录
				FileUtil.rm(path);
				data += "{\"msg\":\"文件或目录删除成功\"} ";
				break;
			case "cp": // 拷贝文件
				String path2 = request.getParameter("path2");
				FileUtil.cp(path, path2);
				data += "{\"msg\":\"文件拷贝成功\"} ";
				break;
			case "mv": // 移动文件
				path2 = request.getParameter("path2");
				FileUtil.mv(path, path2);
				data += "{\"msg\":\"文件移动成功\"} ";
				break;
			case "cat": // 查看文件
				String res = FileUtil.cat(path);
				System.out.println("res" + res);
				data += "{\"msg\":\"查看文件成功\", \"content\":\"" + res + "\"} ";
				break;
			case "vi": // 查看文件
				res = FileUtil.cat(path);
				System.out.println("res" + res);
				data += "{\"msg\":\"查看文件成功\", \"content\":\"" + res + "\"} ";
				break;
			case "update": // 修改文件
				path = request.getParameter("path");
				String content = request.getParameter("content");
				FileUtil.vi(path, content);
				data += "{\"msg\":\"修改文件成功\"}";
				break;
			default:
				data += "{\"msg\":\"命令错误\"} ";
				break;
			}
			break;
		case "addq": // 加入到就绪队列
			path = request.getParameter("path");
			System.out.println(path);
			Cpu.addProcessToRq(path);
			data += "{\"msg\":\"加入就绪队列成功\"}";
			break;
		}
		System.out.println(data);
		response.getWriter().write(data);
	}

}
