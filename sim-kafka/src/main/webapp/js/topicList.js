$(function () {
    loadClusterList();
    loadTopicList();
    //捕获各个radio时间，重新加载数据
    $("input[type='radio']").click(function(){
        loadTopicList();
    });
    //创建topic
    $("#createTopicBtn").click(function(){
        var zk_topic_name = $.trim($("#zk_topic_name").val());
        var zk_partitions = $.trim($("#zk_partitions").val());
        var zk_replicas = $.trim($("#zk_replicas").val());
        var zkCluster = $('input:radio[name="clusterId"]:checked').attr("zkCluster");
        if(zk_topic_name == ""){
            alert("请输入主题名称");
            $("#zk_topic_name").focus();
            return;
        }
        if(zk_partitions == ""){
            alert("请输入分区数");
            $("#zk_partitions").focus();
            return;
        }
        if(zk_replicas == ""){
            alert("请输入broker副本数");
            $("#zk_replicas").focus();
            return;
        }
        $.ajax({
            type: 'post',
            dataType: 'json',
            async:false,
            data:{
                zk : zkCluster,
                topic_name : zk_topic_name,
                partitions : zk_partitions,
                replicas : zk_replicas
            },
            url: '/createTopic.action',
            success: function(response){
                if(response == true){
                    alert("主题创建成功");
                    $("#zk_topic_name").val("");
                    $("#zk_partitions").val("");
                    $("#zk_replicas").val("");
                    loadTopicList();
                }else{
                    alert("主题创建失败");
                }
            },
            error: function(response){
                alert("请求服务器异常，请重试。");
            }
        });
    });
});

function loadTopicList(){
    $("#topicList tr:gt(0)").remove();
    var zkCluster = $('input:radio[name="clusterId"]:checked').attr("zkCluster");
    var cluster_id = $('input:radio[name="clusterId"]:checked').val();
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            zk : zkCluster
        },
        url: '/getTopicList.action',
        success: function(response){
            $.each(response,function(index,topicVo) {
                var consumerObj = "";
                if(topicVo.consumers){
                    $.each(topicVo.consumers,function(index,consVo){
                        consumerObj += "<a href='/consumerDetail.jsp?consumer_grp="+consVo+"&cid="+cluster_id+"'>" + consVo + "<br/>";
                    });
                }
                $("#topicList").append("<tr><td><span id='list_topic_name"+index+"'>"+topicVo.topic+"</span></td><td><span id='list_partitions"+index+"'>"+topicVo.partitions + "</span>" + addPartitionStr(index) + "</td><td>"+consumerObj+"</td><td>"+topicVo.replicas+"</td><td><a href='javascript:void(0);' onclick='return deleteTopic(\""+topicVo.topic+"\")'>删除</a></td></tr>");
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

function addPartitionStr(val){
    var str = "<br/><button type=\"button\" onclick=\"return appPartitionAction("+val+")\" class=\"btn btn-default\">添加</button>";
    str += "<input type=\"text\" class=\"form-control\" id=\"new_partitions"+val+"\" onblur=\"if(isNaN(this.value)){this.value=''}\" maxlength=\"3\" style=\"width:10%;display:inline;margin-left:2px;\"/>个";
    return str;
}

//val：列表索引号
function appPartitionAction(val){
    var zkCluster = $('input:radio[name="clusterId"]:checked').attr("zkCluster");
    var topic_name = $("#list_topic_name"+val).html();
    var oldPartitions = $("#list_partitions"+val).html();
    var numPartitions = $.trim($("#new_partitions"+val).val());
    if(numPartitions == ""){
        alert("请输入添加的分区数");
        $("#new_partitions"+val).focus();
        return;
    }
    var finalNum = numPartitions*1 + oldPartitions*1;
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            zk : zkCluster,
            topic_name : topic_name,
            numPartitions : finalNum
        },
        url: '/addPartitions.action',
        success: function(response){
            if(response == true){
                alert("分区数添加成功");
                $("#list_partitions"+val).html(finalNum);
                $("#new_partitions"+val).val("");
            }else{
                alert("分区数添加失败");
            }
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

function deleteTopic(topicName){
    if(confirm("主题删除后数据无法恢复，真的确定删除吗?")){
        var zkCluster = $('input:radio[name="clusterId"]:checked').attr("zkCluster");
        $.ajax({
            type: 'post',
            dataType: 'json',
            async:false,
            data:{
                zk : zkCluster,
                topic_name : topicName
            },
            url: '/deleteTopic.action',
            success: function(response){
                if(response == true){
                    alert("主题删除成功");
                    loadTopicList();
                }else{
                    alert("主题删除失败");
                }
            },
            error: function(response){
                alert("请求服务器异常，请重试。");
            }
        });
    }
}