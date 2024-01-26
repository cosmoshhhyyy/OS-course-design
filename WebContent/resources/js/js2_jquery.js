
// 加载磁盘状态
function loadDisk() {
	$.ajax({
		url:"MainServlet?action=loadBlock",
		type:"get", 
		success:function(value){
			// console.log(value)
			var disk = value.arr;
			var html = "";
			for (var i = 0; i < 8; i ++) {
			    for (var j = 0; j < 16; j++) {
			    	if (disk[i * 16 + j] > 0 || disk[i * 16 + j] == -1) { // 此块被占用
			    		html += "<div class='block' style='background: #fab1a0;'></div>"
			    	}else { // 此块没被占用
			    		html += "<div class='block'></div>"
			    	}			        
			    }
			}
			// console.log(html)
			$(".main .wz .show").empty().append( 
				    html
			)
		},
		error:function(){
			alert("请联系管理员")
		}
	})
}
//加载树

function loadTree() {
	$.ajax({
		url:"MainServlet?action=loadTree",
		type:"get", 
		sync:true,
		success:function(value){
			
			console.log(value)
			
			var zTreeObj;
			var setting = {};        // zTree 的参数配置，后面详解
			var zNodes = [];
			zNodes = value.node // 一定要放在json里面，否则异步，加载完才获取就不对了
			$(document).ready(function () {
			    zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, zNodes); //初始化zTree，三个参数一次分别是容器(zTree 的容器 className 别忘了设置为 "ztree")、参数配置、数据源
			});
		},
		error:function(){
		}
	})
}

loadTree()

loadDisk()


// 处理输入的命令
$(".wz .cli .sbt").on("click", function () {
	var txt = $(".wz .cli .ml").val()
	var arr = txt.split(' ')
	var ml = arr[0]
	var path = arr[1] 
	var path2 = arr[2] // cp,mv的目标路径
	var code = "";
	if (ml === "touch") {
		code = prompt("输入文件内容：");
	}
	// console.log(code)
	$.ajax({
		url:"MainServlet",
		type:"post", 
		data:{
			action:"cli",
			ml,
			path,
			code,
			path2
		},
		success:function(value){
			console.log(value.msg)
			if (value.msg === "查看文件成功") {
				$(".wz .cli .ml").empty();
				$(".content .txt").val(value.content) // 回显
				$(".content .update").attr("path", path)
			}else {
				location.reload();
			}
		
		},
		error:function(){
			alert("请联系管理员")
		}
	})
})

// 修改文件内容按钮相应
$(".content .update").on("click", function() {
	if ($(".content .update").attr("path") != "") { 
		// 不为空，说明正在查看文件
		var ml = "update"
		var content = $(".content .txt").val()
		var path = $(".content .update").attr("path")
		$.ajax({
			url:"MainServlet",
			type:"post", 
			data:{
				action:"cli",
				ml,
				path,
				content
			},
			success:function(value){
				console.log(value.msg)
				var path = $(".content .update").attr("path", "") // 修改后，就置为空
				location.reload();
			},
			error:function(){
				alert("请联系管理员")
			}
		})
	}
})

// 加入就绪队列
$(".jc .add_q").on("click", function() {
	var path = $(".jc .ex_path").val()
	$.ajax({
		url:"MainServlet",
		type:"post", 
		data:{
			action:"addq",
			path
		},
		success:function(value){
			$(".jc .ex_path").val("")
			$(".jc .que .rq").append("{[" + path + "]}")
			
		},
		error:function(){
			alert("请联系管理员")
		}
	})
})



var timer
// 循环运行cpu执行，一秒一次
$(".jc .start").on("click", function(){
	timer = setInterval(function() { //定时器
		$.ajax({
			url:"MainServlet?action=takeOnce",
			type:"get", 
			success:function(value){
				console.log(value)
				var name = value.nowJcArr[0] // 进程名
				var opt = value.nowJcArr[1] // 操作
				var x = parseInt(value.nowJcArr[2]) // 变量值
				if (name === "") { // 当前没有正在执行的
					$(".jc .runbox .nowJc").empty().text("无进程在cpu运行")
				} else {
					$(".jc .runbox .nowJc").empty().text("进程：" + name + ", 操作：" + opt + ", 变量值=" + x)
				}
				$(".jc .que .rq").empty().text(value.rq)
				$(".jc .que .bq").empty().text(value.bq)
				var a = value.devArr[0]; // 三个设备
				var b = value.devArr[1];
				var c = value.devArr[2];
				if (a > 0) {
					$(".jc .dev .a").css("filter","hue-rotate(80deg)")
				} else {
					$(".jc .dev .a").css("filter","none")
				}
				if (b > 0) {
					$(".jc .dev .b").css("filter","hue-rotate(80deg)")
				} else {
					$(".jc .dev .b").css("filter","none")
				}
				if (c > 0) {
					$(".jc .dev .c").css("filter","hue-rotate(80deg)")
				} else {
					$(".jc .dev .c").css("filter","none")
				}
			},
			error:function(){
				alert("请联系管理员")
			}
		})
	}, 1000)		
})

// 停止运行cpu
$(".jc .stop").on("click", function() {
	clearTimeout(timer)
})

