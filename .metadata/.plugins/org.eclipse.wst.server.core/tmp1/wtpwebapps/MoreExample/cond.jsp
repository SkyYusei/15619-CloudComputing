<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<% 
	if(Integer.parseInt(request.getParameter("age")) >= 18) { 
%>
<h1>You can Vote!</h1>
<%
	} else {
%>
<h1>Sorry you can't vote.</h1>
<%
	}
%>
</body>
</html>