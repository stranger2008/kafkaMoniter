//加载集群列表
function loadClusterList(){
    var str = "集群列表：";
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        url: '/clusterList.action',
        success: function(response){
            if(response == ""){
                location.href = "config.jsp";
            }
            $.each(response,function(index,cluster) {
                var cname = cluster.cname;
                var id = cluster.id;
                var zkhost = "";
                if(cluster.zkhost){
                    zkhost = cluster.zkhost;
                }
                var checked = "";
                if(index == 0){
                    checked = "checked='checked'";
                }
                str += "<input type='radio' name='clusterId' value='"+id+"' zkCluster='"+zkhost+"' "+checked+"/>" + cname + "&nbsp;";
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
    $("#clusterList").html(str);
}