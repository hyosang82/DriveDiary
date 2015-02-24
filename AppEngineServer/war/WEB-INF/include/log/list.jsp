<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.TimeZone"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="kr.hyosang.cardiary.server.Util" %>
<%@ page import="kr.hyosang.cardiary.server.data.VehicleData" %>
<%@ page import="kr.hyosang.cardiary.server.data.TrackLog" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.VehicleInfo" %>
<%
	String vinNo = request.getParameter("vin");
	String user = Util.getLoggedUserEmail();
	int year, month;
	Calendar now = Calendar.getInstance();
	
	List<VehicleInfo> vList = VehicleData.getList(user);
	
	if(vinNo == null) {
		if(vList.size() > 0) {
			vinNo = vList.get(0).vin;
		}else {
			vinNo = "";
		}
	}

    String dateFilter = request.getParameter("date");
	if(dateFilter != null && dateFilter.length() == 6) {
		year = Util.parseInt(dateFilter.substring(0, 4));
		month = Util.parseInt(dateFilter.substring(4));
	}else {
		year = now.get(Calendar.YEAR);
		month = now.get(Calendar.MONTH) + 1;
		dateFilter = String.format("%d%02d", year, month);
	}
	
	List<TrackLog> list = TrackLog.getList(vinNo, dateFilter);

%>
<script type="text/javascript">
function goDetail(key) {
	location.href="/?page=31&key=" + key;
}

function loadList() {
	location.href = "/?page=3&" + makeGetParams();
}

function makeGetParams() {
	var vin = $("#vehicle").val();
	var year = $("#filterYear").val();
	var month = $("#filterMonth").val();
	
	var params = [];
	params["vin"] = vin;
	if(year != "") {
		if(month.length == 0) {
			params["date"] = year;
		}else if(month.length == 1) {
			params["date"] = year + "0" + month;
		}else {
			params["date"] = year + "" + month;
		}
	}
	
	var p = "";
	for(var k in params) {
		p += k + "=" + params[k] + "&";
	}
	
	return p;
}

</script>
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
    
    <select id="filterYear" onchange="loadList();">
<%
    for(int i=2014;i<=now.get(Calendar.YEAR);i++) {
%>
        <option value="<%=i%>" <%=(i==year ? "selected":"")%> ><%=i%>년</option>
<%
    }
%>
    </select>
    
    <select id="filterMonth" onchange="loadList();">
        <option value="">선택</option>
<%
    for(int i=1;i<=12;i++) {
%>
        <option value="<%=i%>" <%=(i==month ? "selected":"")%> ><%=i%>월</option>
<%
    }
%>
    </select>
    
</div>
<table class="table table-hover">
    <thead>
        <tr>
            <th>Track No.</th>
            <th>Distance</th>
            <th>Date/Time</th>
            <th>Log Length</th>
            <th>Tag</th>
        </tr>
    </thead>
    <tbody>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    sdf.setTimeZone(TimeZone.getTimeZone("JST"));   //KST는 없음

    long groupKey = -1;
    String buffer = "";
    int colCnt = 1;
    for(TrackLog info : list) {
%>
		<tr style="cursor:pointer;" onclick="goDetail('<%=info.encodedKey%>');">
			<td><%=Util.getFormattedDateTime(info.timestamp) %></td>
			<td><%=String.format("%.2f", (info.distance/1000f)) %></td>
			<td>-</td>
			<td>-</td>
			<td>
<%
		List<String> tagList = TrackLog.Tag.getList(info.key);
		for(String tag : tagList) {
			out.print(tag);
		}
%>
			</td>
		</tr>

<%    	
    }
%>
    
    </tbody>
</table>
