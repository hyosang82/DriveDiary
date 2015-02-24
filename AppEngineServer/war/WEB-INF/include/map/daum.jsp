<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
/*
	192.168.123.1 : 1e74edb2061b0ccdf2e7b14bf92dd7deb5f18994
	appspot.com : dec9f3b7d816ac4c09175ab8a461065dd0d55c40
*/
String apiKey = "";
if(request.getServerName().startsWith("192.168.123")) {
	apiKey = "fac8510319f54b9ecf1654a2dc786fbda2af06a8";
}else if("car-diary.appspot.com".equals(request.getServerName())) {
	apiKey = "dec9f3b7d816ac4c09175ab8a461065dd0d55c40";
}
%>
<script type="text/javascript" src="http://apis.daum.net/maps/maps3.js?apikey=<%=apiKey%>"></script>
<script type="text/javascript">
var _map;
var _posMarker = null;
var _pathLength = 0;

function mapLoad() {
	_map = new daum.maps.Map(document.getElementById("mapView"), {
		center: new daum.maps.LatLng(37.3345, 127.0503),
		level: 4,
		mapTypeId: daum.maps.MapTypeId.HYBRID
	});
	
	//클릭이벤트 등록
	daum.maps.event.addListener(_map, "dblclick", function(e) {
		var pt = getNearestPos(e.latLng);
		
		var row = document.getElementById("row_" + pt.timestamp);
		if(row != null && row != undefined) {
			document.getElementById("trackList").scrollTop = row.offsetTop;
			itemSelected(row);
			mapMarkAndCenter(pt);
		}
	});
}

function getNearestPos(latLng) {
	var pt = null;
	var minLen = -1;
	
	for(var i=0;i<trackList.length;i++) {
		var ln = new daum.maps.Polyline({
			path:[new daum.maps.LatLng(trackList[i].lat, trackList[i].lng), latLng]
		})
		
		var len = ln.getLength();
		
		if(pt == null) {
			minLen = len;
			pt = trackList[i];
		}else {
			if(minLen > len) {
				minLen = len;
				pt = trackList[i];
			}
		}
	}
	
	return pt;
}

function mapDrawTrack(list) {
	var daumList = [];
	var bounds = new daum.maps.LatLngBounds();
	
	var trackList = [];
	var trackArr = [];
	var trackLevel = -1;
	var levelSpeed = [20, 60, 80, 95];
	var levelColor = ["#EEEEEE", "#FFFF00", "#339900", "#FF6600", "EE0000"];
	var thisLevel = 0;
	
	for(var i=0;i<list.length;i++) {
		for(var j=0;j<levelSpeed.length;j++) {
			if(levelSpeed[j] > list[i].speed) {
				thisLevel = j;
				break;
			}else if(j == levelSpeed.length-1) {
				thisLevel = levelColor.length-1;
			}
		}
		
        var pos = new daum.maps.LatLng(list[i].lat, list[i].lng);
		
		if(thisLevel == trackLevel) {
			//추가중
			trackArr.push(pos);
			
		}else {
			//지금까지 쌓인 데이터 정리
			if(trackArr.length > 0) {
				//트랙 끊기지 않도록
				trackArr.push(pos);
				var track = new daum.maps.Polyline({
					strokeWeight:3,
					strokeColor:levelColor[thisLevel],
					strokeOpacity:1
				});
				track.setPath(trackArr);
				trackList.push(track);
			}
			
			//초기화
			trackArr = [];
			trackArr.push(pos);
			trackLevel = thisLevel;
		}
		
		bounds.extend(pos);
	}
	
	//마지막 트랙 표시
	if(trackArr.length > 0) {
		var track = new daum.maps.Polyline({
			strokeWeight:3,
			strokeColor:levelColor[thisLevel],
			strokeOpacity:1
		});
		track.setPath(trackArr);
		trackList.push(track);
	}
	
	
	//쌓인 트랙 표시
	for(var i=0;i<trackList.length;i++) {
		trackList[i].setMap(_map);
		
		//경로 길이
		_pathLength += trackList[i].getLength();
	}
	
	_map.setBounds(bounds);
	
	requestUpdateInfo();
}

function mapMarkAndCenter(item) {
    var pos = new daum.maps.LatLng(item.lat, item.lng);
	if(_posMarker == null) {
		_posMarker = new daum.maps.Marker({position:pos});
	}else {
		_posMarker.setPosition(pos);
	}
	
	_posMarker.setMap(_map);
	
	_map.panTo(pos);	
}

function getTotalDistance() {
	return _pathLength;
}
</script>