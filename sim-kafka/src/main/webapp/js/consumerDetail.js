$(function () {
    loadConsumerList();
});

//获取消费组-topic信息
function loadConsumerList(){
    var consumer_grp = getUrlParam("consumer_grp");
    var cluster_id = getUrlParam("cid");
    var zkhost = getClusterZk(cluster_id);
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            zk : zkhost,
            consumer_grp : consumer_grp
        },
        url: '/getConsumerDetail.action',
        success: function(response){
            $.each(response,function(index,objVo) {
                var _partition = "",_owner = "",_topic = "";
                if(objVo.topic){
                    _topic = objVo.topic;
                }
                if(typeof(objVo.partition) != "undefined"){
                    _partition = objVo.partition;
                }
                if(objVo.owner){
                    _owner = objVo.owner;
                }
                $("#consumerDetail").append("<tr><td>"+_topic+"</td><td>"+_partition+"</td><td>"+objVo.offset+"</td><td>"+objVo.logsize+"</td><td>"+objVo.lag+"</td><td>"+_owner+"</td></tr>");
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

//获取zkhost
function getClusterZk(cluster_id){
    var zkhost = "";
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            id : cluster_id
        },
        url: '/getCluster.action',
        success: function(response){
            if(response.zkhost){
                zkhost = response.zkhost;
            }
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
    return zkhost;
}

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}