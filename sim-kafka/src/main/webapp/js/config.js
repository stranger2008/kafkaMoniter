$(function () {
    loadServerList();
});

//加载机器列表
function loadServerList(){
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        url: '/serverList.action',
        success: function(response){
            var smgr = "";
            $.each(response,function(index,jmxconf) {
                var port = jmxconf.port;
                var host = jmxconf.host;
                var id = jmxconf.id;
                var cname = "";
                if(jmxconf.cname){
                    cname = jmxconf.cname;
                }
                var checked = "";
                if(index == 0){
                    checked = "checked='checked'";
                }
                smgr += cname + "&nbsp;" + host + ":" + port + "&nbsp;<a href='javascript:void(0);' onclick='deleteJmxServer("+id+")'>删除</a><br/>";
            });
            smgr += addText();
            $("#serverListMgr").html(smgr);
            //加载集群列表
            loadSpanClusterList();
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

//加载集群列表
function loadSpanClusterList(){
    var str = "";
    $("#jmx_cid").empty();
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        url: '/clusterList.action',
        success: function(response){
            $.each(response,function(index,cluster) {
                var cname = cluster.cname;
                var cdesc = "",zkhost = "";
                if(cluster.cdesc){
                    cdesc = cluster.cdesc + "<br/>";
                }
                if(cluster.zkhost){
                    zkhost = cluster.zkhost + "<br/>";
                }
                var id = cluster.id;
                str += cname + "&nbsp;<a href='javascript:void(0);' onclick='deleteCluster("+id+")'>删除</a><br/>" + zkhost + cdesc;
                $("#jmx_cid").append("<option value='"+id+"'>"+cname+"</option>");
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
    $("#spanClusterList").html(str);
}

function addServerInfo(){
    var jmxConf = $.trim($("#jmx_host_conf").val());
    if(jmxConf == ""){
        alert("机器不能为空");
        $("#jmx_host_conf").focus();
        return false;
    }
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            jmxConf : jmxConf,
            cid : $("#jmx_cid").val()
        },
        url: '/addServerInfo.action',
        success: function(response){
            if(response == true){
                alert("添加成功");
                loadServerList();
            }else{
                alert("格式不正确或连接不可用");
            }
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

function setSpanClusterList(){
    $("#spanClusterList").html(loadSpanClusterList());
}

function addClusterInfo(){
    var cluster_cname = $.trim($("#cluster_cname").val());
    var cluster_cdesc = $.trim($("#cluster_cdesc").val());
    var cluster_zkhost = $.trim($("#cluster_zkhost").val());
    if(cluster_cname == ""){
        alert("集群名称不能为空");
        $("#cluster_cname").focus();
        return false;
    }
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        data:{
            cname : cluster_cname,
            zkhost : cluster_zkhost,
            cdesc : cluster_cdesc
        },
        url: '/addCluster.action',
        success: function(response){
            if(response == true){
                alert("添加成功");
                setSpanClusterList();
                $("#cluster_cname").val("");
                $("#cluster_cdesc").val("");
            }else{
                alert("添加失败");
            }
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

//添加机器配置文本
function addText(){
    var s = "<h3>添加机器</h3>";
    s += "<select id='jmx_cid'></select><br/>";
    s += "<textarea name='jmx_host_conf' id='jmx_host_conf' style='width:500px;'></textarea><br/>";
    s += "支持批量添加，格式：host:port,host:port ...<br/>";
    s += "<input type='button' value='添加' onclick='return addServerInfo();'/>";

    s += "<h3>添加集群</h3>";
    s += "<span id='spanClusterList'></span>";
    s += "名称:<input type='text' name='cluster.cname' id='cluster_cname' /><br/>";
    s += "ZK地址:<input type='text' name='cluster.zkhost' id='cluster_zkhost' style='width:300px;'/><br/>";
    s += "备注:<input type='text' name='cluster.cdesc' id='cluster_cdesc' style='width:300px;'/><br/>";
    s += "<input type='button' value='添加' onclick='return addClusterInfo();'/><br/>";
    return s;
}

//删除机器配置
function deleteJmxServer(id){
    $.ajax({
        type: 'post',
        async:false,
        url: '/delServer.action?id='+id,
        success: function(response){
            if(response == "true"){
                alert("删除成功");
                loadServerList();
            }else{
                alert("删除失败");
            }
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

//删除集群配置
function deleteCluster(id){
    $.ajax({
        type: 'post',
        async:false,
        url: '/delCluster.action?id='+id,
        success: function(response){
            if(response == "true"){
                alert("删除成功");
                setSpanClusterList();
            }else{
                alert("删除失败");
            }
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}