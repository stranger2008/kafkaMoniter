$(function () {
    loadClusterList();
    loadConsumerList();
    //捕获各个radio时间，重新加载数据
    $("input[type='radio']").click(function(){
        loadConsumerList();
    });
});

function loadConsumerList(){
    $("#consumerList tr:gt(0)").remove();
    var zkCluster = $('input:radio[name="clusterId"]:checked').attr("zkCluster");
    var clusterId = $('input:radio[name="clusterId"]:checked').val();
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            zk : zkCluster
        },
        url: '/getConsumerList.action',
        success: function(response){
            $.each(response,function(index,topicVo) {
                $("#consumerList").append("<tr><td><a href='/consumerDetail.jsp?consumer_grp="+topicVo+"&cid="+clusterId+"'>"+topicVo+"</a></td></tr>");
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}