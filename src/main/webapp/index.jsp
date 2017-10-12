<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<html>
<head>
    <title>网站首页显示</title>

    <base href="http://${pageContext.request.serverName }:${pageContext.request.serverPort}${pageContext.request.contextPath}/"/>
    <style type="text/css">
        body {
            margin: 0px auto 0px auto;
            width: 50%;
        }
    </style>
</head>
<body>
    <%--判断用户是否登录--%>
    <c:if test="${sessionScope.loginUser == null}">
        <a href="demo/user/toLoginPage">登录</a>
        <a href="demo/user/toRegistPage">注册</a>
    </c:if>
    <c:if test="${sessionScope.loginUser != null}">
        欢迎：${sessionScope.loginUser.userNick}
        <a href="demo/user/showDetail">个人中心</a>
        <a href="demo/user/logout">退出</a>
    </c:if>
    <br/>

    <%--设置搜索框--%>
    <form action="demo/user/search" method="post">
        <input type="text" name="keywords"/>
        <input type="submit" value="搜索"/>
    </form>
</body>
</html>
