<!-- 
	Author：黄辉
	Date:2009.11.28
	Action:当JSP发生错误时，跳转至此页面提示。
 -->
<%@ page contentType="text/html;charset=UTF-8"%>
<%
 	String path = request.getContextPath() ;
%>
<html>
  
  <head>
  	
    <title>系统错误</title>
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="this is my page">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="<%= path %>/resources/css/msg-tip.css" />
  </head>
  
  <body style="">
  
   	<div class="x-div-screen-center">
   		<div class="left-div-img-icon x-img-failure" ></div>
   		<div class="msg-div"><br />
   			<label>友情提示：</label><br />
   			<div>
			 没有找到与请求资源名所匹配的Action或Result，<br/>
			 请检查请求路径或配置文件是否正确!</div>
   		</div>
   	</div>
  </body>
</html>

