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
	<script type="text/javascript" src="js/consumerList.js"></script>

<%String pageName = "consumer";%>
<%@ include file="top.jsp"%>

<div id="clusterList"></div>

<table class="table table-hover" id="consumerList">
	<thead>
	<tr>
		<th>消费组名称</th>
	</tr>
	</thead>
</table>

	</body>
</html>
