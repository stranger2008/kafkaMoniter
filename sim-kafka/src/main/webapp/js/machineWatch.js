$(function () {
    loadClusterList();
    loadMachineInfoList();
    //捕获各个radio时间，重新加载数据
    $("input[type='radio']").click(function(){
        loadMachineInfoList();
    });
});

function loadMachineInfoList(){
    $("#machineWatchList tr:gt(0)").remove();
    var cluster_id = $('input:radio[name="clusterId"]:checked').val();
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            cid : cluster_id
        },
        url: '/getMachineInfo.action',
        success: function(response){
            $.each(response,function(index,obj) {
                if(obj == null){
                    return false;
                }
                var str = "";
                str += "<tr>";
                str += "<td>"+obj.ip+"</td>";
                str += "<td>使用率："+obj.cpuUsage+"</td>";
                str += "<td>使用率："+obj.memUsage+"<br/>总大小："+obj.memTotal+"<br/>已使用："+obj.memUse+"</td>";
                str += "<td>使用率："+obj.ioUsage+"</td>";
                str += "<td>1分钟："+obj.loadAverage1m+"<br/>5分钟："+obj.loadAverage5m+"<br/>15分钟："+obj.loadAverage15m+"</td>";
                str += "<td>"+obj.establishedCount+"</td>";
                var netStr = "";
                $.each(obj.netUsages,function(index,objInner) {
                    netStr += "<b>网卡：" + objInner.deviceName + "</b><br/>下行数据：" + objInner.receive + "&nbsp;上行数据：" + objInner.transmit + "<br/>下行数据/秒：" + objInner.receivePS;
                    netStr += "&nbsp;上行数据/秒：" + objInner.transmitPS + "<br/>网卡带宽：" + objInner.deviceSpeed + "&nbsp;下行带宽使用率：" + objInner.receiveUsage + "&nbsp;上行带宽使用率：" +objInner.transmitUsage + "<br/>";
                });
                str += "<td>"+netStr+"</td>";
                str += "</tr>";
                $("#machineWatchList").append(str);
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}