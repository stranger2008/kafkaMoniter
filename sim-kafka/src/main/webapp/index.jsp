<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Kafka Monitor</title>
		<%@ include file="inc.jsp"%>
	</head>
	<body>
<script src="js/highcharts.js"></script>
<script src="js/exporting.js"></script>
<script src="js/clusterList.js"></script>
<script src="js/index.js"></script>
<%String pageName = "index";%>
<%@ include file="top.jsp"%>

<span id="clusterList"></span>
<div>数据类型：<input type="radio" name="dataType" value="1" checked="checked"/>JVM <input type="radio" name="dataType" value="2"/>Kafka
&nbsp;<select id="monitorAttr" onchange="reloadPicData();"></select>
</div>

<%@ include file="dataTool.jsp"%>

<div id="wsfgx"></div>

	</body>
</html>
