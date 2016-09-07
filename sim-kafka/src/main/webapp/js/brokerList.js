$(function () {
    loadClusterList();
    loadBrokerList();

    //捕获各个radio时间，重新加载数据
    $("input[type='radio']").click(function(){
        loadBrokerList();
    });

});

function loadBrokerList(){
    $("#brokerList tr:gt(0)").remove();
    var zkCluster = $('input:radio[name="clusterId"]:checked').attr("zkCluster");
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            zk : zkCluster
        },
        url: '/getBrokerList.action',
        success: function(response){
            $.each(response,function(index,broker) {
                var port="",version="",jmx_port="",timestamp="",state="";
                if(broker.port){
                    port = broker.port;
                }
                if(broker.version){
                    version = broker.version;
                }
                if(broker.jmx_port){
                    jmx_port = broker.jmx_port;
                }
                if(broker.timestamp){
                    timestamp = broker.timestamp;
                }
                if(broker.state=="1"){
                    state = "<font color='#006400'>Alive</font>";
                }
                if(broker.state=="2"){
                    state = "<font color='red'>Dead</font>";
                }
                if(broker.state=="3"){
                    state = "<font color='#8b008b'>Uncontrol</font>";
                }
                $("#brokerList").append("<tr><td>"+state+"</td><td>"+broker.id+"</td><td>"+broker.host+"</td><td>"+port+"</td><td>"+version+"</td><td>"+jmx_port+"</td><td>"+timestamp+"</td></tr>");
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}