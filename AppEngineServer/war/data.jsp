<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Text" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="java.lang.String" %>
<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	String timestamp = request.getParameter("ts");
	String tseq = request.getParameter("tseq");
	String vin = request.getParameter("vin");
	String log = request.getParameter("log");
	
	if((timestamp != null && timestamp.length() > 0) &&
			(tseq != null && tseq.length() > 0) &&
			(vin != null && vin.length() > 0) &&
			(log != null && log.length() > 0)) {
		long trackseq = Long.parseLong(tseq, 10);
		long times = Long.parseLong(timestamp, 10);
		//save
		Entity store = new Entity(KeyFactory.createKey("GpsLog", String.format("%s_%d", vin, times)));
		store.setProperty("timestamp", times);
		store.setProperty("track_seq", trackseq);
		store.setProperty("vin", vin);
		store.setProperty("logdata", new Text(log));
		
		datastore.put(store);
	}

	
%>

<body>
<form method="POST">
	<div>
		Timestamp <input type="text" name="ts" />
	</div>
	<div>
		Track Seq <input type="text" name="tseq" />
	</div>
	<div>
		VIN <input type="text" name="vin" />
	</div>
	<div>
		Log <textarea style="width:400px;height:50px;" name="log"></textarea>
	</div>
	<div>
		<input type="submit" value="Save" />
	</div>
</form>
<table style="width:600px;">

<%
	Query query = new Query("GpsLog");
	
	PreparedQuery pq = datastore.prepare(query);
	
	for(Entity entity : pq.asIterable()) {
%>
	<tr>
		<td><%=entity.getProperty("timestamp") %></td>
		<td><%=entity.getProperty("track_seq") %></td>
		<td><%=entity.getProperty("vin")%></td>
		<td><%=((Text)entity.getProperty("logdata")).getValue() %></td>
	</tr>


<%
	}
%>
</table>
</body>