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
	<script type="text/javascript" src="js/topicList.js"></script>

<%String pageName = "topic";%>
<%@ include file="top.jsp"%>

<div id="clusterList"></div>

	<form class="form-inline" role="form">
		<div class="form-group">
			<label class="sr-only" for="zk_topic_name">名称</label>
			<input type="text" class="form-control" id="zk_topic_name" placeholder="请输入主题名称">
		</div>
		<div class="form-group">
			<label class="sr-only" for="zk_partitions">名称</label>
			<input type="text" class="form-control" id="zk_partitions" placeholder="请输入分区数" onblur="if(isNaN(this.value)){this.value=''}" maxlength="3">
		</div>
		<div class="form-group">
			<label class="sr-only" for="zk_replicas">名称</label>
			<input type="text" class="form-control" id="zk_replicas" placeholder="请输入broker副本数" onblur="if(isNaN(this.value)){this.value=''}" maxlength="3">
		</div>
		<button type="button" id="createTopicBtn" class="btn btn-default">创建主题</button>
	</form>

	<table class="table table-hover" id="topicList">
	<thead>
	<tr>
		<th>主题名</th>
		<th>分区数</th>
		<th>消费组</th>
		<th>副本数</th>
		<th>操作</th>
	</tr>
	</thead>
</table>

	</body>
</html>
