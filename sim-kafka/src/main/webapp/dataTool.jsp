<%@ page contentType="text/html; charset=UTF-8"%>
<div>
	数据粒度：<input type="radio" name="dateType" value="1" checked="checked"/>分钟
	<input type="radio" name="dateType" value="2"/>小时
	<input type="radio" name="dateType" value="3"/>天
	<input type="checkbox" name="autoReload" id="autoReload" checked="checked"/>定时刷新
</div>
<div id="conditionTime1">查询周期：<input type="radio" name="searchTime" value="10" checked="checked"/>最近十分钟
	<input type="radio" name="searchTime" value="30"/>最近三十分钟
	<input type="radio" name="searchTime" value="60"/>最近一小时
	<input type="radio" name="searchTime" value="1440"/>按天查询
</div>
<div id="conditionTime2" style="display: none;">查询周期：<input type="radio" name="searchTime" value="10"/>最近十小时
	<input type="radio" name="searchTime" value="24"/>今日
	<input type="radio" name="searchTime" value="48"/>最近两天
	<input type="radio" name="searchTime" value="72"/>最近三天
</div>
<div id="conditionTime3" style="display: none;">查询周期：<input type="radio" name="searchTime" value="7"/>最近一周
	<input type="radio" name="searchTime" value="10"/>最近十天
	<input type="radio" name="searchTime" value="15"/>最近十五天
	<input type="radio" name="searchTime" value="30"/>最近一月
</div>