<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
    <base href="http://${pageContext.request.serverName }:${pageContext.request.serverPort}${pageContext.request.contextPath}/"/>
    <style type="text/css">
        body {
            margin: 0px auto 0px auto;
            width: 50%;
        }
        textarea {
            width: 500px;
            height: 300px;
        }
    </style>
</head>
<body>
    <form action="/demo/user/update" method="post" enctype="multipart/form-data">
        <input type="hidden" name="userId" value="${sessionScope.loginUser.userId }"/>
        <input type="hidden" name="userPicGroup" value="${sessionScope.loginUser.userPicGroup }"/>
        <input type="hidden" name="userPicFilename" value="${sessionScope.loginUser.userPicFilename }"/>
        昵称：<input type="text" name="userNick" value="${sessionScope.loginUser.userNick}"/><br/>
        性别：<input type="radio" name="userGender" value="0"
                  <c:if test="${sessionScope.loginUser.userGender==0}">checked="checked"</c:if>
              />男
              <input type="radio" name="userGender" value="1"
                   <c:if test="${sessionScope.loginUser.userGender==1}">checked="checked"</c:if>
              />女<br/>
        职业：<input type="text" name="userJob" value="${sessionScope.loginUser.userJob}"/><br/>
        家乡：<input type="text" name="userHometown" value="${sessionScope.loginUser.userHometown}"/><br/>
        自我介绍：<textarea name="userDesc">${sessionScope.loginUser.userDesc }</textarea>
        <br/>

        <c:if test="${!empty sessionScope.loginUser.userPicGroup}">
            <img src="http://192.168.6.142/${sessionScope.loginUser.userPicGroup}
                    /${sessionScope.loginUser.userPicFilename}"/>
        </c:if>
        <c:if test="${empty sessionScope.loginUser.userPicGroup}">
            尚未上传图像
        </c:if><br/>
        头像：<input type="file" name="headPicture"/><br/>
        <input type="submit" value="更新"/>

    </form>
    <a href="index.jsp">回首页</a>
</body>
</html>
