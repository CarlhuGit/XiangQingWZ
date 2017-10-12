<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<html>
<head>
    <title>用户注册</title>

    <base href="http://${pageContext.request.serverName }:${pageContext.request.serverPort}${pageContext.request.contextPath}/"/>
    <style type="text/css">
        body {
            margin: 0px auto 0px auto;
            width: 50%;
        }
    </style>
</head>
<body>
    <p>${requestScope.msg}</p>

    <form action="/demo/user/regist" method="post">
        账号：<input type="text" name="userName"/><br/>
        密码：<input type="text" name="userPwd"/><br/>
        昵称：<input type="text" name="userNick"/><br/>
        <input type="submit" value="注册"/>
    </form>

    <a href="index.jsp">回首页</a>
</body>
</html>
