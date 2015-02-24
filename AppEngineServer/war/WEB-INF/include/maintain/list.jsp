<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="kr.hyosang.cardiary.server.Util" %>
<%@ page import="kr.hyosang.cardiary.server.data.MaintainData" %>
<%@ page import="kr.hyosang.cardiary.server.data.VehicleData" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.MaintainInfo" %>
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

    
    List<MaintainInfo> mList = MaintainData.getList(vinNo);
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
            <th>Date</th>
            <th>ODO</th>
            <th>Garage</th>
            <th>Parts</th>
            <th>Memo</th>
        </tr>
    </thead>
    <tbody>
<%
    for(MaintainInfo info : mList) {
    	String parts = Util.join(MaintainData.getMaintainParts(info.getEncodedKey()), ", ");
%>
        <tr>
            <td><%=info.date%></td>
            <td><%=info.odo %></td>
            <td><%=info.garage %></td>
            <td><%=parts %></td>
            <td><%=info.memo %></td>
        </tr>
<%
    }
%>
    
    </tbody>
</table>
<button class="btn" onclick="location.href='/?page=41';">Add</button>
