<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="kr.hyosang.cardiary.server.Util" %>
<%@ page import="kr.hyosang.cardiary.server.data.VehicleData" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.VehicleInfo" %>
<%
	String email = Util.getLoggedUserEmail();
    List<VehicleInfo> list = VehicleData.getList(email);

%>
<table class="table table-hover">
    <thead>
        <tr>
            <th>VIN</th>
            <th>Vendor</th>
            <th>Model</th>
            <th>Year</th>
        </tr>
    </thead>
    <tbody>
<%
    for(VehicleInfo info : list) {
%>
        <tr>
            <td><%=info.vin%></td>
            <td><%=info.vendor%></td>
            <td><%=info.model%></td>
            <td><%=info.year%></td>
        </tr>
<%
    }
%>
    
    </tbody>
</table>
<button class="btn" onclick="location.href='/?page=11';">Add</button>