<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h1>This file shows dynamic includes</h1>

<%
	int x = 12;
%>

<jsp:include page="dynfooter.jsp">
    <jsp:param value="Dynamic Include Examples" name="title"></jsp:param> 
</jsp:include>


</body>
</html>