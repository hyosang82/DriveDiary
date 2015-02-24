<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="kr.hyosang.cardiary.server.Util" %>
<%@ page import="kr.hyosang.cardiary.server.data.VehicleData" %>
<%@ page import="kr.hyosang.cardiary.server.data.StationData" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.VehicleInfo" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.StationInfo" %>
<%@ page import="kr.hyosang.cardiary.server.data.MaintainData" %>
<%
	String user = Util.getLoggedUserEmail();
    List<VehicleInfo> vList = VehicleData.getList(user);
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String currDate = sdf.format(c.getTime());
    
    List<String> garageList = MaintainData.getGarageList(user);
%>

<script type="text/javascript">
function addNewPartItem() {
	var item = $("#partsName").val();
	if(item == undefined || item == "") {
		alert("항목명을 입력하세요");
		$("#partsName").focus();
	}else {
		$.ajax({
			type:"POST",
			url:"/AddPartsItem",
			data: {
				itemName:item
			}
		}).done(function(data) {
			if(data == "OK") {
				alert("추가되었습니다.");
				$("#partsName").val("");
				refreshParts();
			}else {
				alert(data);
			}
		});
	}
}

function refreshParts() {
	$.ajax({
		type:"POST",
		dataType:"json",
		url:"/GetPartsList"
	}).done(function (data) {
		makePartCheckbox(data);
	});
}

function makePartCheckbox(data) {
	var content = "";
	
	for(var i=0;i<data.length;i++) {
		var id = data[i]["id"];
		var nm = decodeURIComponent(data[i]["name"]).replace("+", " ");
		
		content += '<div class="checkbox"><label><input type="checkbox" name="partSel" data-name="'+nm+'" value="'+id+'" />'+nm+'</label></div>';
	}
	
	$("#partCheckDiv").html(content);
}

function onSelectParts() {
	var chkval = "";
	var items = "";
	
	var cnt = $("input[name='partSel']:checked").each(
		function(index, obj) {
			chkval += obj.value + "|";
			items += $(this).attr("data-name") + ",";
		}
	).length;

	$("#inputParts").val(chkval);
	$("#selectedPartName").html(items);
	
	$("#partsModal").modal("hide");
}

function onGarageSel() {
	if($("#inputGarage").val() == "") {
		$("#inputGarageAdd").prop("readonly", false);
		$("#inputGarageAdd").val("");
	}else {
		$("#inputGarageAdd").prop("readonly", true);
		$("#inputGarageAdd").val($("#inputGarage").val());
	}
}

$(document).ready(function() {
	refreshParts();
	onGarageSel();
});

</script>
<form class="form-horizontal" role="form" action="/SaveMaintenance" method="POST">
	<input type="hidden" id="inputParts" name="inputParts" />
    <div class="form-group">
        <label for="inputVehicle" class="col-sm-2 control-label">Vehicle</label>
        <div class="col-sm-10">
            <select class="form-control" name="inputVehicle">
<%
    for(VehicleInfo info : vList) {
%>
                <option value="<%=info.vin%>">[<%=info.vin%>] <%=info.vendor%> <%=info.model%> (<%=info.year%>)</option>

<%
    }
%>
            </select>
        </div>
    </div>
    <div class="form-group">
        <label for="inputOdo" class="col-sm-2 control-label">ODO</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" id="inputOdo" name="inputOdo" placeholder="Current ODO" />
        </div>
        <label for="inputDate" class="col-sm-2 control-label">Date</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" id="inputDate" name="inputDate" placeholder="Date" value="<%=currDate%>" />
        </div>
    </div>
    <div class="form-group">
        <label for="inputGarage" class="col-sm-2 control-label">Garage</label>
        <div class="col-sm-10">
            <select class="form-control" style="display:inline;width:45%;" name="inputGarage" id="inputGarage" onchange="onGarageSel();">
<%
	for(String g : garageList) {
%>		
				<option value="<%=g%>"><%=g%></option>
<%
	}
%>
                <option value="">추가</option>

            </select>
            <input type="text" class="form-control" style="display:inline;width:45%;" id="inputGarageAdd" name="inputGarageAdd" />
        </div>
    </div>
    <div class="form-group">
    	<label class="col-sm-2 control-label">Parts</label>
    	<div class="col-sm-10">
    		<button type="button" class="btn btn-default" data-toggle="modal" data-target="#partsModal">선택</button>
    		<span id="selectedPartName"></span>
    	</div>
    </div>
    <div class="form-group">
        <label for="inputPrice" class="col-sm-2 control-label">Total Price</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="inputPrice" name="inputPrice" placeholder="Total price" />
        </div>
    </div>    
    <div class="form-group">
    	<label for="inputMemo" class="col-sm-2 control-label">MEMO</label>
    	<div class="col-sm-10">
    		<textarea class="form-control" id="inputMemo" name="inputMemo" style="width:100%;height:100px;"></textarea>
    	</div>
    </div>
    <button type="submit" class="btn btn-default">Save</button>
</form>

<div class="modal fade" id="partsModal" tabindex="-1" role="dialog" aria-labelledby="partsDialogLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="partsDialogLabel">Select parts</h4>
			</div>
			<div class="modal-body">
				<input type="text" class="form-control" style="display:inline;width:70%;" id="partsName" placeholder="항목명" />
				<button type="button" class="btn btn-primary" onclick="addNewPartItem();">항목 추가</button>
				<div id="partCheckDiv">
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary" onclick="onSelectParts();">Select</button>
			</div>
		</div>
	</div>
</div>