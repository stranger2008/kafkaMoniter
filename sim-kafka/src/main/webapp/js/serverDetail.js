$(function () {
    //加载机器配置列表
    loadServerList();

    //页面刷新默认按照分钟维度、最近十分钟
    $("input[name='dateType'][value='1']").attr("checked",true);
    $("#conditionTime1").children(":first").attr("checked",true);

    //根据传入的机器ID参数设置radio
    var sid = getUrlParam("sid");
    if(sid != ""){
        $("input[name='serverConf'][value='"+sid+"']").attr("checked",true);
    }

    //加载图表
    reloadPicData();

    //捕获各个radio时间，重新加载图表数据
    $("input[type='radio']").click(function(){
        if($(this).attr("name") == "dateType"){
            for(var i=1;i<=3;i++){
                if($(this).val() == i){
                    $("#conditionTime" + i).show();
                }else{
                    $("#conditionTime" + i).hide();
                }
            }
            $("input[name='searchTime']").attr("checked",false);
            $("#conditionTime" + $(this).val()).children(":first").attr("checked",true);
        }
        reloadPicData();
    });

    //定时刷新，一分钟一次
    setAutoReload();
    $("#autoReload").click(function(){
        if($(this).is(":checked")){
            setAutoReload();
        }else{
            clearAutoReload();
        }
    });

});

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}

//加载机器列表
function loadServerList(){
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        url: '/serverList.action',
        success: function(response){
            var shtml = "机器列表：";
            $.each(response,function(index,jmxconf) {
                var port = jmxconf.port;
                var host = jmxconf.host;
                var id = jmxconf.id;
                var checked = "";
                if(index == 0){
                    checked = "checked='checked'";
                }
                shtml += "<input type='radio' name='serverConf' value='"+id+"' "+checked+"/>" + host + ":" + port;
            });
            $("#serverList").html(shtml);
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

//定时刷新图表
var reloadInt;
function setAutoReload(){
    reloadInt = window.setInterval("reloadPicData()",60000)
}
function clearAutoReload(){
    window.clearInterval(reloadInt);
}

//加载图表数据
function reloadPicData(){
    var pid = $('input:radio[name="serverConf"]:checked').val();
    var dateType = $('input:radio[name="dateType"]:checked').val();
    var dataType = $('input:radio[name="dataType"]:checked').val();
    var searchTime = $('input:radio[name="searchTime"]:checked').val();

    $.ajax({
        type: 'get',
        dataType: 'json',
        url: '/showMonitor.action',
        data:{
            pid : pid,
            dateType : dateType,
            dataType : dataType,
            searchTime : searchTime
        },
        success: function(response){
//            alert(JSON.stringify(response));
            $("#wsfgx").html("");
            $.each(response, function(key, value) {
                var dateList = value.dateList;
//                var dataList = value.dataList;
                var manyAttrMap = value.manyAttrMap;
//                alert(JSON.stringify(manyAttrMap));
                var yyData = new Array();
                $.each(manyAttrMap, function(key, value) {
                    var yData = new Array();
                    $.each(value,function(index,dataStr){
                        yData.push(parseFloat(dataStr));
                    });
                    var attrObj = {name:key,data:yData};
                    yyData.push(attrObj);
                });
                var desc = value.desc;
                var unit = value.unit;
                var xData = new Array();
                $.each(dateList,function(index,dateStr){
                    xData.push(dateStr);
                });
//                var yData = new Array();
//                $.each(dataList,function(index,dataStr){
//                    yData.push(parseFloat(dataStr));
//                });
                $("#wsfgx").append("<div id='"+key+"' style='min-width: 800px; min-height: 400px; float:left'></div>");
                showPic(key,xData,desc,unit,yyData);
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

//调用highChart组件展示图表
function showPic(containerName,categoriesArray,desc,unit,dataArray){
    $('#'+containerName).highcharts({
        title: {
            text: desc,
            x: -20 //center
        },
        xAxis: {
            categories: categoriesArray
        },
        yAxis: {
            title: {
                text: '单位 ('+unit+')'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix:unit
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: dataArray
    });
}