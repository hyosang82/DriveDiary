<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%
	if(request.getUserPrincipal() == null) {
		//not logged in
		response.sendRedirect("/page=401");
	}
	
%>