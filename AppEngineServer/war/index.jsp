<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="kr.hyosang.cardiary.server.PageUtil" %>
<%@ page import="kr.hyosang.cardiary.server.PageUtil.PageInfo" %>

<%
  UserService userService = UserServiceFactory.getUserService();
  User user = userService.getCurrentUser();
  
  String pageId = request.getParameter("page");
  PageInfo info = PageUtil.getPageInfo(pageId);
  
  if(info.bLoginRequired  && request.getUserPrincipal() == null) {
	  //로그인 필수 페이지에 로그인하지 않음
	  info = PageUtil.getErrorPage(401);
  }
%>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>
    
    <!-- Bootstrap -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <link rel="stylesheet" type="text/css" href="/css/style.css" />
    
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="/js/jquery-1.11.0.min.js"></script>    
    
  </head>
  <body>

    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Project name</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-right">
<%
  if(user == null) {
%>            
            <li><a href="<%=userService.createLoginURL("/")%>">Login</a></li>
<%
  }else {
%>
            <li><a href="#"><%=user.getEmail()%></a></li>
            <li><a href="<%=userService.createLogoutURL("/")%>">Logout</a></li>

<%
  }
%>
          </ul>
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row">


        <div class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li <%=(info.isMain() ? "class=\"active\"" : "")%> ><a href="/">Overview</a></li>
            <li <%=(info.isVehicle() ? "class=\"active\"" : "")%> ><a href="/?page=1">Vehicle</a></li>
            <li <%=(info.isFuel() ? "class=\"active\"" : "")%> ><a href="/?page=2">Fuel</a></li>
            <li <%=(info.isLog() ? "class=\"active\"" : "")%>><a href="/?page=3">Track Log</a></li>
            <li <%=(info.isMaintain() ? "class=\"active\"" : "")%>><a href="/?page=4">Maintenance</a></li>
          </ul>
          <ul class="nav nav-sidebar">
            <li><a href="">Nav item</a></li>
            <li><a href="">Nav item again</a></li>
            <li><a href="">One more nav</a></li>
            <li><a href="">Another nav item</a></li>
            <li><a href="">More navigation</a></li>
          </ul>
          <ul class="nav nav-sidebar">
            <li><a href="">Nav item again</a></li>
            <li><a href="">One more nav</a></li>
            <li><a href="">Another nav item</a></li>
          </ul>
        </div>

        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">

          <h2 class="sub-header"><%=info.pageTitle%></h2>

        
<%
  pageContext.include(info.includePath);
%>          
         
        </div>
      </div>
    </div>


    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="/bootstrap/js/bootstrap.min.js"></script>
  </body>
</html>    