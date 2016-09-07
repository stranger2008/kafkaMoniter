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
	<script type="text/javascript" src="js/machineWatch.js"></script>

<%String pageName = "machineWatch";%>
<%@ include file="top.jsp"%>

<div id="clusterList"></div>

	<table class="table table-hover" id="machineWatchList">
	<thead>
	<tr>
		<th>机器IP</th>
		<th>CPU</th>
		<th>内存</th>
		<th>IO</th>
		<th>负载</th>
		<th>TCP连接数</th>
		<th>网络</th>
	</tr>
	</thead>
</table>

	</body>
</html>
