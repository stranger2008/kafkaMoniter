<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Kafka Monitor</title>
		<%@ include file="inc.jsp"%>
	</head>
	<body>
	<script type="text/javascript" src="js/consumerDetail.js"></script>

<%String pageName = "consumer";%>
<%@ include file="top.jsp"%>

<table class="table table-hover" id="consumerDetail">
	<thead>
	<tr>
		<th>Topic</th>
		<th>Partition</th>
		<th>Offset</th>
		<th>Logsize</th>
		<th>Lag</th>
		<th>Owner</th>
	</tr>
	</thead>
</table>

	</body>
</html>
