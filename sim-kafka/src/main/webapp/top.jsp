<%@ page contentType="text/html; charset=UTF-8"%>
<nav class="navbar navbar-inverse" role="navigation">
    <div class="navbar-header">
        <a class="navbar-brand" href="#">Kafka监控</a>
    </div>
    <div>
        <ul class="nav navbar-nav">
            <li <%if(pageName.equals("index")){out.print("class=\"active\"");}%>><a href="index.jsp">集群监控</a></li>
            <li <%if(pageName.equals("broker")){out.print("class=\"active\"");}%>><a href="brokerList.jsp">Broker列表</a></li>
            <li <%if(pageName.equals("topic")){out.print("class=\"active\"");}%>><a href="topicList.jsp">主题列表</a></li>
            <li <%if(pageName.equals("consumer")){out.print("class=\"active\"");}%>><a href="consumer.jsp">消费组列表</a></li>
            <li <%if(pageName.equals("machineWatch")){out.print("class=\"active\"");}%>><a href="machineWatch.jsp">机器监控</a></li>
            <li <%if(pageName.equals("config")){out.print("class=\"active\"");}%>><a href="config.jsp">配置管理</a></li>
        </ul>
    </div>
</nav>