$(function () {
    loadClusterList();
    loadMonitorAttr();

    //页面刷新默认按照分钟维度、最近十分钟
    $("input[name='dateType'][value='1']").attr("checked",true);
    $("#conditionTime1").children(":first").attr("checked",true);

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
        if($(this).attr("name") == "dataType"){
            loadMonitorAttr();
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

    $(".highcharts-title").delegate("tspan","click",function(){
        alert('action');
    });
});

//定时刷新图表
var reloadInt;
function setAutoReload(){
    reloadInt = window.setInterval("reloadPicData()",60000)
}
function clearAutoReload(){
    window.clearInterval(reloadInt);
}

//加载指标属性
function loadMonitorAttr(){
    var dataType = $("input[name='dataType']:checked").val();
    $("#monitorAttr").empty();
    $.ajax({
        type: 'post',
        dataType: 'json',
        async:false,
        url: '/getMonitorAttr.action',
        success: function(response){
            //alert(JSON.stringify(response));
            $.each(response,function(key,value) {
                if(key == dataType){
                    $.each(value,function(ckey,cvalue) {
                        $("#monitorAttr").append("<option value='"+ckey+"'>"+cvalue+"</option>");
                    });
                }
            });
        },
        error: function(response){
            alert("请求服务器异常，请重试。");
        }
    });
}

//加载图表数据
function reloadPicData(){
    var clusterId = $('input:radio[name="clusterId"]:checked').val();
    var dateType = $('input:radio[name="dateType"]:checked').val();
    var dataType = $('input:radio[name="dataType"]:checked').val();
    var searchTime = $('input:radio[name="searchTime"]:checked').val();

    $.ajax({
        type: 'get',
        dataType: 'json',
        url: '/showClusterMonitor.action',
        data:{
            clusterId : clusterId,
            monitorKey : $("#monitorAttr").val(),
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
                var server_str = key.split("_");
                var sid = server_str[0];
                var desc = value.desc + "&nbsp;<a href='serverDetail.jsp?sid="+sid+"'>详细</a>";
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
            useHTML: true,
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