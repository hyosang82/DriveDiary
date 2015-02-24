<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form class="form-horizontal" role="form" action="/SaveVehicle" method="POST">
    <div class="form-group">
        <label for="inputVin" class="col-sm-2 control-label">VIN</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="inputVin" name="inputVin" placeholder="VIN" />
        </div>
    </div>
    <div class="form-group">
        <label for="inputVendor" class="col-sm-2 control-label">Vendor</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="inputVendor" name="inputVendor" placeholder="Vendor" />
        </div>
    </div>
    <div class="form-group">
        <label for="inputModel" class="col-sm-2 control-label">Model</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="inputModel" name="inputModel" placeholder="Model" />
        </div>
    </div>
    <div class="form-group">
        <label for="inputYear" class="col-sm-2 control-label">Year</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="inputYear" name="inputYear" placeholder="Year" />
        </div>
    </div>
    <button type="submit" class="btn btn-default">Save</button>
</form>