<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>查看查询结果</title>
    <base href="http://${pageContext.request.serverName }:${pageContext.request.serverPort
    }${pageContext.request.contextPath}/"/>
</head>
<body>


    <table border="1">
    <c:if test="${empty requestScope.searchResultList}">
        <tr>
            <td>没有找到匹配的查询结果</td>
        </tr>
    </c:if>
    <c:if test="${!empty requestScope.searchResultList}">
        <tr>
            <td>头像</td>
            <td>昵称</td>
            <td>性别</td>
            <td>家乡</td>
            <td>职业</td>
            <td>详情</td>
        </tr>
        <c:forEach items="${requestScope.searchResultList}" var="resultMap">
            <tr>
                <td>
                    <c:if test="${!empty resultMap['user_pic_group']}">
                        <img style="width: 350px;height: 400px" src="http://192.168.6.142/${resultMap['user_pic_group']}/${resultMap['user_pic_filename']}"/>

                    </c:if>
                    <c:if test="${empty resultMap['user_pic_group']}">
                        尚未上传图像
                    </c:if>
                </td>
                <td>${resultMap['user_nick'] }</td>
                <td>${resultMap['user_gender'] }</td>
                <td>${resultMap['user_hometown'] }</td>
                <td>${resultMap['user_job'] }</td>
                <td>
                    <a href="demo/user/search_detail/${resultMap['id'] }">详情</a>
                </td>
            </tr>
        </c:forEach>
    </c:if>
    </table>
    <a href="index.jsp">回首页</a>

    <br/>
    <br/>
    <br/>
</body>
</html>
