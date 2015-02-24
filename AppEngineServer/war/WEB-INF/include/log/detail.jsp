<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="kr.hyosang.cardiary.server.Util" %>
<%@ page import="kr.hyosang.cardiary.server.data.TrackLog" %>
<%@ page import="kr.hyosang.cardiary.server.data.TrackLog.GpsLog" %>
<%
	pageContext.include("/WEB-INF/include/map/daum.jsp");
%>
<%
	String encodedKey = request.getParameter("key");

	TrackLog info = TrackLog.getByEncodedKey(encodedKey);
    List<GpsLog> logList = TrackLog.getGpsData(info.key);
    
    long time_key = info.timestamp;
    boolean procUpdate = (info.distance == 0);
    
%>
<script type="text/javascript">
var trackList = [];
var currSelRow = null;

function pageInit() {
	mapLoad();
	
	mapDrawTrack(trackList);
	
	$("#tagAddName").keydown(function(event) {
		if(event.which == 13) {
			event.preventDefault();
            addTag();
		}
	})
	
	refreshTag();
}

function trackSelected(timestamp) {
	var idx = -1;
	//find log
	for(var i=0;i<trackList.length;i++) {
		if(trackList[i].timestamp == timestamp) {
			idx = i;
			break;
		}
	}
	
	if(idx != -1) {
		mapMarkAndCenter(trackList[idx]);
		itemSelected(document.getElementById("row_" + timestamp));
	}
}

function itemSelected(row) {
	if(currSelRow != null) {
		currSelRow.style.backgroundColor = "#FFFFFF";
	}
	
	row.style.backgroundColor = "#EFEFEF";
	currSelRow = row;
}

function requestUpdateInfo() {
<%
	if(procUpdate) {
%>
	$.ajax({
		type:"POST",
		url:"/LogUpdate",
		data:{
			key:"<%=encodedKey%>",
			dist:getTotalDistance()
		}
	});
<%
	}
%>
}

function addTag() {
	if($("#tagAddName").val() != "") {
		$.ajax({
			type:"POST",
			url:"/AddTag",
			data:{
				log_key:"<%=encodedKey%>",
				tag:$("#tagAddName").val()
			}
		}).done(function() {
			$("#tagAddName").val("");
			refreshTag();
		});
	}
}

function refreshTag() {
	$.ajax({
		type:"POST",
		dataType:"json",
		url:"/GetTag",
		data:{
			log_key:"<%=encodedKey%>"
		}
	}).done(function(data) {
		$("#tagTableBody").empty();
		
		for(var key in data) {
			var tag = decodeURIComponent(data[key]).replace("+"," ");
			var tr = "<tr><td>" + tag + "</td></tr>";
			$("#tagTableBody").append(tr);
		}
	});
}

function toggleView() {
	var obj = $("#trackList");
	if(obj.css("overflow-y") == "scroll") {
		obj.css("overflow-y", "normal");
		obj.css("height", "");
	}else {
		obj.css("overflow-y", "scroll");
		obj.css("height", "300px");
	}
	
}

function mergePrevLog() {
	if(confirm("Sure?")) {
		$.ajax({
			type:"POST",
			dataType:"json",
			url:"/MergeLog",
			data:{
				log_key:"<%=encodedKey%>"
			}
		}).done(function(data) {
			if(data.result == "OK") {
				alert("Complete");
				location.href="/?page=3";
			}else {
				alert("Error : " + data.result);
			}
		});
	}
}

function deleteLog() {
	if(confirm("Delete, OK?")) {
		$.ajax({
			type:"POST",
			dataType:"json",
			url:"/DeleteLog",
			data:{
				log_key:"<%=encodedKey%>"
			}
		}).done(function(data) {
			if(data.result == "OK") {
				alert("Complete");
				location.href="/?page=3";
			}else {
				alert("Error : " + data.result);
			}
		});
	}
}

window.onload = pageInit;

</script>
<div class="row">
    <div id="mapView" class="col-sm-10" style="height:400px;"></div>
    <div class="col-sm-2">
        <table class="table">
            <thead>
	            <tr>
	                <td>
	                    <input type="text" class="form-control" id="tagAddName" placeholder="태그 추가" />
	                </td>
	            </tr>
	        </thead>
	        <tbody id="tagTableBody"></tbody>
        </table>
    </div>
</div>
    
<div id="trackList" style="height:300px;overflow-y:scroll;">
	<table class="table table-hover">
	    <thead>
	        <tr>
	            <th>Date/Time</th>
	            <th>Latitude</th>
	            <th>Longitude</th>
	            <th>Altitude</th>
	            <th>Speed</th>
	            <th width="0"></th>
	        </tr>
	    </thead>
	    <tbody>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    for(GpsLog log : logList) {
%>
	        <tr style="cursor:pointer;" id="row_<%=log.timestamp%>" onclick="trackSelected('<%=log.timestamp%>');">
	            <td><%=Util.getFormattedDateTime(log.timestamp)%></td>
	            <td><%=log.latitude%></td>
	            <td><%=log.longitude%></td>
	            <td><%=log.altitude%></td>
	            <td><%=log.speed%></td>
	            <td>
	            	<script type="text/javascript">
	            		var pos = {
	            				lat:<%=log.latitude%>,
	            				lng:<%=log.longitude%>,
	            				timestamp:<%=log.timestamp%>,
	            				speed:<%=log.speed%>
	            		};
	            		trackList.push(pos);
	        		</script>
	            </td>
	        </tr>
<%
    }
%>
    
	    </tbody>
	</table>
</div>
<div style="width:100%;height:30px;background-color:#eeeeee;text-align:center;cursor:pointer;" onclick="toggleView();">Expand/Collapse</div>
<div style="text-align:right;">
	<button class="btn" onclick="deleteLog();">삭제</button>
	<button class="btn" onclick="mergePrevLog();">이전 로그와 병합</button>
</div>