<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="kr.hyosang.cardiary.server.Util" %>
<%@ page import="kr.hyosang.cardiary.server.data.VehicleData" %>
<%@ page import="kr.hyosang.cardiary.server.data.StationData" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.VehicleInfo" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.StationInfo" %>
<%
	String user = Util.getLoggedUserEmail();
    List<VehicleInfo> vList = VehicleData.getList(user);
    List<StationInfo> stnList = StationData.getList();
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String currDate = sdf.format(c.getTime());

%>
<script type="text/javascript">
function calcTotalPrice() {
    var perLiter = 0;
    var volume = 0;
    
    try{
        perLiter = parseInt($("#inputPrice").val(), 10);
        if(isNaN(perLiter)) perLiter = 0;
    }catch(e){ }
    
    try{
        volume = parseFloat($("#inputVolume").val(), 10);
        if(isNaN(volume)) volume = 0;
    }catch(e){ }
    
    $("#inputTotalPrice").val(Math.floor(perLiter * volume));
}

function calcVolume() {
    var perLiter = 0;
    var totalPrice = 0;
    
    try{
        perLiter = parseInt($("#inputPrice").val(), 10);
        if(isNaN(perLiter)) perLiter = 0;
    }catch(e){ }
    
    try{
        totalPrice = parseInt($("#inputTotalPrice").val(), 10);
        if(isNaN(totalPrice)) totalPrice = 0;
    }catch(e){ }
    
    $("#inputVolume").val( Math.floor(totalPrice / perLiter * 100) / 100 );
}

</script>
<form class="form-horizontal" role="form" action="/SaveFuelLog" method="POST">
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
        <label for="inputPrice" class="col-sm-2 control-label">Price</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" id="inputPrice" name="inputPrice" placeholder="Price(won/l)" />
        </div>
    </div>
    <div class="form-group">
        <label for="inputTotalPrice" class="col-sm-2 control-label">Total Price</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="inputTotalPrice" name="inputTotalPrice" placeholder="Total price" onkeyup="calcVolume();" />
        </div>
    </div>
    <div class="form-group">
        <label for="inputVolume" class="col-sm-2 control-label">Volume</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="inputVolume" name="inputVolume" placeholder="Volume(l)" onkeyup="calcTotalPrice();" />
        </div>
        <div class="checkbox col-sm-3">
            <label>
                <input type="checkbox" id="inputIsFull" name="inputIsFull" value="Y" /> Full
            </label>
        </div>
    </div>
    <div class="form-group">
        <label for="inputDate" class="col-sm-2 control-label">Date</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" id="inputDate" name="inputDate" placeholder="Date" value="<%=currDate%>" />
        </div>
        <label for="inputStation" class="col-sm-2 control-label">Station</label>
        <div class="col-sm-4">
            <select class="form-control" name="inputStation" id="inputStation">
                <option value="">미등록</option>
<%
    for(StationInfo info : stnList) {
%>
                <option value="<%=info.getEncodedKey()%>"><%=info.name%> (<%=info.company%>)</option>

<%
    }
%>
            
            </select>
        </div>
    </div>    
    <button type="submit" class="btn btn-default">Save</button>
</form>