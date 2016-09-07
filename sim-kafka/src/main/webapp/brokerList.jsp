<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Kafka Monitor</title>
		<%@ include file="inc.jsp"%>
	</head>
	<body>
	<script type="text/javascript" src="js/clusterList.js"></script>
	<script type="text/javascript" src="js/brokerList.js"></script>
<%String pageName = "broker";%>
<%@ include file="top.jsp"%>

<div id="clusterList"></div>

<table class="table table-hover" id="brokerList">
	<thead>
	<tr>
		<th>State</th>
		<th>Id</th>
		<th>Host</th>
		<th>Port</th>
		<th>Version</th>
		<th>Jmx port</th>
		<th>Create time</th>
	</tr>
	</thead>
</table>

	</body>
</html>
