<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>统计</title>
	<link href="css/bootstrap.min.css?v=3.3.7" rel="stylesheet">
    <link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">

    <link href="css/animate.css" rel="stylesheet">
    <link href="css/style.css?v=4.1.0" rel="stylesheet">
    <link href="css/plugins/iCheck/custom.css" rel="stylesheet">
    <link href="css/layer.css" rel="stylesheet">

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content  animated fadeInRight">
        <div class="row">
            <div class="col-sm-12">
                <div id="main" style="width: 100%;height:400px;"></div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <div id="main2" style="width: 100%;height:400px;"></div>
            </div>
        </div>
    </div>

    <!-- 全局js -->
    <script src="js/jquery.min.js?v=2.1.4"></script>
    <script src="js/bootstrap.min.js?v=3.3.7"></script>
    <script src="js/layer.js"></script>
	<script type="text/javascript" src="js/web3.min.js"> </script>
	<script type="text/javascript" src="abi/abi.js"> </script>
    <link href="css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    <script src="js/plugins/sweetalert/sweetalert.min.js"></script>

    <script src="js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
	<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
	<link href="css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<script src="js/plugins/toastr/toastr.min.js"></script>
    <script src="js/echarts.min.js" type="text/javascript" charset="utf-8"></script>

    <script>
        var myChart = echarts.init(document.getElementById('main'));
        function load1(){
            var xAxisData =  [];
            var data1 = []
            var data2=[]
            var nowDate = new Date()
            var hours=nowDate.getHours()
            var date = nowDate.getFullYear()+"-"+(nowDate.getMonth()+1)+"-"+nowDate.getDate()+" "+hours+":00:00";
            var oveDate = nowDate.getFullYear()+"-"+(nowDate.getMonth()+1)+"-"+(nowDate.getDate()+1)+" "+hours+":00:00";
            function getData(){
                console.log(date)
                console.log(oveDate)
                $.ajax({
                    url:"/statistics/chartData",
                    type:"get",
                    data:{
                        date : date,
                        oveDate : oveDate
                    },
                    dataType:"json",
                    success:function(res){
                        var data = res.data;
                        xAxisData = []
                        data1 = []
                        var temp=hours;
                        for(var i=hours;i<hours+24;i++){
                            if(temp>23){
                                temp=0
                            }
                            xAxisData.push(temp+":00");
                            if(data[temp].sea14){
                                data1.push(data[temp].sea14);
                                data2.push(data[temp].sea15);
                            }else{
                                data1.push(0);
                                data2.push(0);
                            }
                            temp++
                        }
                        myChart.setOption(getOption());
                    }
                });
            }
            getData();
            function getOption(){
                var option = {
                    title: {
                        text: '租鱼时段分布统计图['+date+'-'+oveDate+']'
                    },
                    xAxis: {
                        data:xAxisData
                    },
                    yAxis: {
                        type: 'value'
                    },
                    legend: {
                        data: ['SEA14','SEA15']
                    },
                    series: [{
                        name:"SEA14",
                        type: 'bar',
                        data: data1,
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true, //开启显示
                                    position: 'top', //在上方显示
                                    textStyle: { //数值样式
                                        color: 'black',
                                        fontSize: 12
                                    }
                                },
                                color:'red'
                            }
                        }
                    },{
                        name:"SEA15",
                        type: 'bar',
                        data: data2,
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true, //开启显示
                                    position: 'top', //在上方显示
                                    textStyle: { //数值样式
                                        color: 'black',
                                        fontSize: 12
                                    }
                                },
                                color:'blue'
                            }
                        }
                    }]
                };
                return option;
            }
            myChart.on('click', function (params) {
                console.log(params)
                load2(params.seriesName,params.name)
            })
        }
        load1();
        var myChart2 = echarts.init(document.getElementById('main2'));
        function load2(type,time) {//type(SEA14,SEA15) time(12)
            time=time.split(":")[0];
            var xAxisData = [];
            var data1 = []
            var nowDate = new Date()
            var date = nowDate.getFullYear() + "-" + (nowDate.getMonth() + 1) + "-" + nowDate.getDate()+" "+time+":00:00";
            var oveDate = nowDate.getFullYear() + "-" + (nowDate.getMonth() + 1) + "-" + nowDate.getDate()+" "+time+":59:59";
            var price=1400;
            var color='red';
            if(type=="SEA15"){
                price=1500;
                color='blue';
            }
            function getData() {
                console.log(date)
                console.log(oveDate)
                $.ajax({
                    url: "/statistics/chartData2",
                    type: "get",
                    data: {
                        price:price,
                        date: date,
                        oveDate: oveDate
                    },
                    dataType: "json",
                    success: function (res) {
                        var data = res.data;
                        xAxisData = []
                        data1 = []
                        for (var d in data) {
                            if(d>9){
                                xAxisData.push(time+":"+d);
                            }else{
                                xAxisData.push(time+":0"+d);
                            }
                            if (data[d].sea14) {
                                data1.push(data[d].sea14);
                            } else {
                                data1.push(0);
                            }
                        }
                        myChart2.setOption(getOption());
                    }
                });
            }
            getData();
            function getOption() {
                var option = {
                    title: {
                        text: '租鱼分钟分布统计图['+time+':00:00 - '+time+':59:59]'
                    },
                    xAxis: {
                        data: xAxisData
                    },
                    yAxis: {
                        type: 'value'
                    },
                    legend: {
                        data: [type]
                    },
                    series: [{
                        name: type,
                        type: 'bar',
                        data: data1,
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true, //开启显示
                                    position: 'top', //在上方显示
                                    textStyle: { //数值样式
                                        color: 'black',
                                        fontSize: 12
                                    }
                                },
                                color:color
                            }
                        }
                    }]
                };
                return option;
            }
        }
    </script>
</body>

</html>

