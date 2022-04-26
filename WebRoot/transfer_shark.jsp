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
    <title>转鱼</title>
    <link href="css/bootstrap.min.css?v=3.3.7" rel="stylesheet">
    <link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">

    <link href="css/animate.css" rel="stylesheet">
    <link href="css/style.css?v=4.1.0" rel="stylesheet">
    <link href="css/plugins/iCheck/custom.css" rel="stylesheet">
    <link href="css/layer.css" rel="stylesheet">
    <style type="">
        #editable1 tr td{
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
            font-size: 11px;
        }
        #editable2 tr td{
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
            font-size: 11px;
        }
        .css-1paq6c8 {
            display: flex;
            -webkit-box-align: center;
            align-items: center;
            flex-direction: row;
            background: var(--chakra-colors-blackAlpha-600);
            font-size: var(--chakra-fontSizes-xs);
            border-radius: var(--chakra-radii-3xl);
            padding-inline-start: var(--chakra-space-2);
            padding-inline-end: var(--chakra-space-2);
            padding-top: var(--chakra-space-1);
            padding-bottom: var(--chakra-space-1);
        }
        .css-17dhe2o {
            font-size: 0px;
            width: var(--chakra-sizes-2-5);
            height: var(--chakra-sizes-2-5);
            margin-left: var(--chakra-space-2);
        }
    </style>

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content  animated fadeInRight">
        <div class="row">
            <div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-content">
                        <div class="row">
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">账号查询</button>
	                                  </span>
                                    <input type="text" value="" class="input-sm form-control" id="address1">
                                </div>
                            </div>
                            <div class="col-sm-1 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                     <button type="button" class="btn btn-sm btn-primary" id="search1">查询</button>
	                                  </span>
                                </div>
                            </div>
                        </div>
						<div  style="overflow-y:scroll;">
							<table class="table table-striped table-bordered table-hover dataTables-example " style="table-layout:fixed;margin-bottom: 0px;">
							    <tbody>
							        <tr>
                                        <th width="10%" colspan="2">序号</th>
                                        <th width="45%">账号</th>
                                        <th width="15%">拥有</th>
                                        <th width="15%">租用</th>
                                        <th width="15%">BNB</th>
								    </tr>
							    </tbody>
							</table>
						</div>
						<div style="height:580px;overflow-y:scroll;">
							<table  class="table table-striped table-bordered table-hover dataTables-example " id="editable1" style="table-layout:fixed;">
                                <tbody id="container1" class="list">
                                    <!--
                                    <tr>
                                        <td width="100%" colspan="8">
                                            <div style="width:240px;border:1px #C3D2D1FF solid;border-radius: 3px;display: inline-block;">
                                                <div class="pull-left">
                                                    <a href="https://starsharks.com/zh-Hant/market/sharks/724941" target="_blank">
                                                        <img src="https://starsharks.com/nft/img/0x1002020261a4a20062a14340856062a4820221a0608300000000000000000000.png?w=100">
                                                    </a>
                                                </div>
                                                <div class="input-group" style="width:120px;">
                                                    <div>#724941</div>
                                                    <div>
                                                        <div style="display: inline-block;">
                                                            <img alt="shark level" src="img/star.png" style="width:20px;">
                                                        </div>
                                                        <div style="display: inline-block;">
                                                            <img alt="shark level" src="img/star.png" style="width:20px;">
                                                        </div>
                                                    </div>
                                                    <div>
                                                        <div style="display: inline-block;">
                                                            <img alt="shark level" src="img/power.png">
                                                        </div>
                                                        <div style="display: inline-block;">
                                                            <p>2/20</p>
                                                        </div>
                                                    </div>
                                                    <span class="input-group-btn">
                                                        <button type="button" class="btn btn-sm btn-default">租用</button>
                                                    </span>
                                                </div>
                                                <div class="css-1jxgzd7 timeout">
                                                    <div class="chakra-stack css-1paq6c8">
                                                        <input type="hidden" value="1648189204">
                                                        <p class="chakra-text css-1wu7cx7">倒计时:</p>
                                                        <p class="chakra-text css-1p2vcsf">00</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                    -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-content">
                        <div class="row">
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
                                    <select class="input-sm form-control m-b" id="userSelect">
                                        <option value="">选择账号</option>
                                        <c:forEach items="${users}" var="item">
                                            <option value="${item.id}">${item.userName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">账号查询</button>
	                                  </span>
                                    <input type="text" value="" class="input-sm form-control" id="address2">
                                </div>
                            </div>
                            <div class="col-sm-1 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                     <button type="button" class="btn btn-sm btn-primary" id="search2">查询</button>
	                                  </span>
                                </div>
                            </div>
                        </div>
                        <div  style="overflow-y:scroll;">
                            <table class="table table-striped table-bordered table-hover dataTables-example " style="table-layout:fixed;margin-bottom: 0px;">
                                <tbody>
                                <tr>
                                    <th width="10%" colspan="2">序号</th>
                                    <th width="45%">账号</th>
                                    <th width="15%">拥有</th>
                                    <th width="15%">租用</th>
                                    <th width="15%">BNB</th>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <div style="height:580px;overflow-y:scroll;">
                            <table  class="table table-striped table-bordered table-hover dataTables-example " id="editable2" style="table-layout:fixed;">
                                <tbody id="container2" class="list">
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--遮罩层-->
    <div id="modal-form" class="modal fade in" aria-hidden="true" style="display: none; padding-right: 6px;background-color: #676a6c;opacity: 0.9;">
        <div class="modal-dialog">
            <div style="margin-top:50%;">
                <div class="sk-spinner sk-spinner-wandering-cubes" style="height:0px;">
                    <div class="sk-cube1"></div>
                    <div class="sk-cube2"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- 全局js -->
    <script src="js/jquery.min.js?v=2.1.4"></script>
    <script src="js/bootstrap.min.js?v=3.3.7"></script>
    <script src="js/plugins/peity/jquery.peity.min.js"></script>
    <script src="js/plugins/iCheck/icheck.min.js"></script>
    <script src="js/layer.js"></script>
	<script type="text/javascript" src="js/web3.min.js"> </script>
	<script type="text/javascript" src="abi/abi.js"> </script>
    <link href="css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    <script src="js/plugins/sweetalert/sweetalert.min.js"></script>

    <script src="js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
	<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
	<link href="css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<script src="js/plugins/toastr/toastr.min.js"></script>
    <script src="js/shark.js"></script>
	<script src="js/transfer_shark.js"></script>

    <script>
        $(function () {
            $('.full-height-scroll').slimScroll({
                height: '100%'
            });
            $("#checkedAll2").click(function(){
                var is = $(this).is(":checked");
                $("#editable2").find(":checkbox").prop("checked",is);
            });
			$("#search1").click(function(){
				list($("#address1").val(),"container1");
			});
            $("#search2").click(function(){
                list($("#address2").val(),"container2",$("#userSelect").val());
            });
            $("#transfer").click(function(){
                var type=$("#addressType").val();
                var count=parseFloat($("#count").val());
                var from=[];
                var to=[];
                $("#editable1").find(":checkbox").each(function() {
                    if ($(this).is(":checked")) {
                        var id=$(this).val();
                        if(id){
                            from.push(id);
                        }
                    }
                });
                $("#editable2").find(":checkbox").each(function() {
                    if ($(this).is(":checked")) {
                        var id=$(this).val();
                        if(id){
                            to.push(id);
                        }
                    }
                });
                if(from.length==0){
                    swal({
                        title: "请选择转账账户"
                    })
                    return;
                }
                if(to.length==0){
                    swal({
                        title: "请选择目标账户"
                    })
                    return;
                }
                if(count>0){
                    if(type=="1"){
                        step1(from,to,count,1);
                    }else if(type=="2"){
                        step1(from,to,count,2);
                    }else{
                        swal({
                            title: "请选择转账币种"
                        })
                    }
                }else{
                    swal({
                        title: "请输入转账数量"
                    })
                }
            });
        });
    </script>
</body>

</html>

