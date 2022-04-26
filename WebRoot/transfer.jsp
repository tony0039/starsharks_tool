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
    <title>转账</title>
    <link href="css/bootstrap.min.css?v=3.3.7" rel="stylesheet">
    <link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">

    <link href="css/animate.css" rel="stylesheet">
    <link href="css/style.css?v=4.1.0" rel="stylesheet">
    <link href="css/plugins/iCheck/custom.css" rel="stylesheet">
    <link href="css/layer.css" rel="stylesheet">
    <style type="">
    	#editable tr td{
	    	white-space: nowrap;text-overflow: ellipsis;overflow: hidden;
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
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">转账类型</button>
	                                  </span>
                                      <select class="input-sm form-control m-b" id="addressType">
                                          <option value="">选择币种</option>
                                          <option value="1">SEA</option>
                                          <option value="2">BNB</option>
                                      </select>
                                 </div>
                            </div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                <span class="input-group-btn">
	                                    <button type="button" class="btn btn-sm btn-default">转账数量</button>
	                                </span>
	                                <input type="text" value="" class="input-sm form-control" id="count">
                                </div>
                            </div>
                            <div class="col-sm-2 m-b-xs">
                                <div class="input-group">
	                                <span class="input-group-btn">
	                                    <button type="button" class="btn btn-sm btn-primary" style="margin-left:5px;" id="transfer">转账</button>
	                                </span>
	                          	</div>
                          	</div>
                        </div>
						<div  style="overflow-y:scroll;">
							<table class="table table-striped table-bordered table-hover dataTables-example " style="table-layout:fixed;margin-bottom: 0px;">
							    <tbody>
							        <tr>
                                        <th width="5%"><input type="checkbox" name="check_all" id="checkedAll1" value=""></th>
                                        <th width="10%">序号</th>
                                        <th width="50%">账号</th>
                                        <th width="15%">SEA</th>
                                        <th width="15%">BNB</th>
								    </tr>
							    </tbody>
							</table>
						</div>
						<div style="height:580px;overflow-y:scroll;">
							<table  class="table table-striped table-bordered table-hover dataTables-example " id="editable1" style="table-layout:fixed;">
                                <tbody id="container1" class="list">
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
                                    <th width="5%"><input type="checkbox" name="check_all" id="checkedAll2" value=""></th>
                                    <th width="10%">序号</th>
                                    <th width="50%">账号</th>
                                    <th width="15%">SEA</th>
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
            <div class="modal-content" style="margin-top:50%;">
                <div class="modal-body" style="text-align: center;">
                    <div class="progress">
                        <div style="width: 0%" aria-valuemax="100" aria-valuemin="0" aria-valuenow="35" role="progressbar" class="progress-bar progress-bar-success" id="status_width">
                        </div>
                    </div>
                    <span>当前进度<div id="status">0/0</div> </span>
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
	<script src="js/transfer.js"></script>

    <script>
        $(function () {
            $('.full-height-scroll').slimScroll({
                height: '100%'
            });
            $("#checkedAll1").click(function(){
                var is = $(this).is(":checked");
                $("#editable1").find(":checkbox").prop("checked",is);
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

