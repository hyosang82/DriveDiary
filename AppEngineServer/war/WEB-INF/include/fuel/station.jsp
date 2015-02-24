<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="kr.hyosang.cardiary.server.data.StationData" %>
<%@ page import="kr.hyosang.cardiary.server.data.entity.StationInfo" %>
<%
    List<StationInfo> stnList = StationData.getList();
%>
<table class="table table-hover">
    <thead>
        <tr>
            <th>Name</th>
            <th>정유사</th>
        </tr>
    </thead>
    <tbody>
<%
    for(StationInfo info : stnList) {
%>
        <tr>
            <td><%=info.name%></td>
            <td><%=info.company%></td>
        </tr>
<%
    }
%>
    
    </tbody>
</table>

<form class="form-horizontal" role="form" action="/SaveStation" method="POST">
    <div class="form-group">
        <label for="inputName" class="col-sm-2 control-label">주유소명</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="inputName" name="inputName" placeholder="주유소명" />
        </div>
    </div>
    <div class="form-group">
        <label for="inputCompany" class="col-sm-2 control-label">정유사</label>
        <div class="col-sm-10">
            <select id="inputCompany" name="inputCompany" class="form-control">
                <option value="SK">SK</option>
                <option value="GS">GS칼텍스</option>
                <option value="OILBANK">현대오일뱅크</option>
                <option value="SOIL">S-OIL</option>
                <option value="ETC">기타</option>
            </select>
        </div>
    </div>

    <button type="submit" class="btn btn-default">Save</button>
</form>