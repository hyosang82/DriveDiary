<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="kr.hyosang.cardiary.server.Util" %>
<%@ page import="kr.hyosang.cardiary.server.data.FuelData" %>
<%@ page import="kr.hyosang.cardiary.server.data.VehicleData" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.FuelInfo" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.VehicleInfo" %>
<%
    String vinNo = request.getParameter("vin");
    String user = Util.getLoggedUserEmail();
    
    List<VehicleInfo> vList = VehicleData.getList(user);
    
    if(vinNo == null) {
    	if(vList.size() > 0) {
    		vinNo = vList.get(0).vin;
    	}else {
    		vinNo = "";
    	}
    }

    
    List<FuelInfo> fList = FuelData.getList(vinNo);
%>
<div>
    <select id="vehicle">
<%
    for(VehicleInfo vehicle : vList) {
%>
        <option value="<%=vehicle.vin%>" <%=(vinNo.equals(vehicle.vin) ? "selected" : "")%>><%=vehicle.model%> (<%=vehicle.vin%>)</option>
<%
    }
%>
    </select> 
</div>
<table class="table table-hover">
    <thead>
        <tr>
            <th>ODO</th>
            <th>Date</th>
            <th>Liter</th>
            <th>Price</th>
            <th>WON/L</th>
            <th>Efficient</th>
            <th>Accu.Efficient</th>
            <th>Full</th>
        </tr>
    </thead>
    <tbody>
<%
    for(FuelInfo info : fList) {
%>
        <tr>
            <td><%=info.odo%></td>
            <td><%=info.date %></td>
            <td><%=info.volume %></td>
            <td><%=info.totalPrice %></td>
            <td><%=info.unitPrice %></td>
            <td><%=String.format("%.2f", info.efficient)%></td>
            <td><%=String.format("%.2f", info.accuEfficient) %></td>
            <td><%=(info.isFull ? "Y" : "N") %>
        </tr>
<%
    }
%>
    
    </tbody>
</table>
<button class="btn" onclick="location.href='/?page=21';">Add</button>
<button class="btn" onclick="location.href='/?page=22';">주유소 관리</button>