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
    <title>租鱼</title>
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
            <div class="col-sm-9">
                <div class="ibox float-e-margins">
                    <div class="ibox-content">
                        <div class="row">
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">SEA购买价</button> 
	                                  </span>
	                                  <input type="text" value="14" class="input-sm form-control"  id="sea">
	                          	</div>
                          	</div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">GAS</button> 
	                                  </span>
	                                  <input type="text" value="10" class="input-sm form-control" id="gas"> 
                                 </div>
                            </div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">GASLimit</button> 
	                                  </span>
	                                  <input type="text" value="250000" class="input-sm form-control" id="gasLimit"> 
                                 </div>
                            </div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">间隔(毫秒)</button> 
	                                  </span>
	                                  <input type="text" value="500" class="input-sm form-control" id="timeInterval">
	                          	</div>
                          	</div>
                        </div>
                        <div class="row">
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">抢购数量</button> 
	                                  </span>
	                                  <input type="text" value="2" class="input-sm form-control" id="buyCount"> 
                                 </div>
                            </div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">BNB限制</button>
	                                  </span>
	                                  <input type="text" value="0.0045" class="input-sm form-control"  id="bnbCount">
	                          	</div>
                          	</div>
							<div class="col-sm-3 m-b-xs">
								<div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">智能设置</button>
	                                  </span>
									<input type="text" value="5" class="input-sm form-control" id="cishu">
								</div>
							</div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
									<span class="input-group-btn">
										<div class="onoffswitch">
											<input type="checkbox" class="onoffswitch-checkbox" id="setting">
											<label class="onoffswitch-label" for="setting">
												<span class="onoffswitch-inner"></span>
												<span class="onoffswitch-switch"></span>
											</label>
										</div>
									</span>
	                            	<span class="input-group-btn">
	                            		<button type="button" class="btn btn-sm btn-primary" id="start">开始</button>
	                            		<button type="button" class="btn btn-sm btn-danger" id="stop" style="display:none;">停止</button>
	                            	</span>
	                            	<span class="input-group-btn">
	                            		<button type="button" class="btn btn-sm btn-primary" id="auth" style="margin-left:5px;">授权</button>
	                            	</span>
	                          	</div>
                          	</div>
                        </div>
						<div  style="overflow-y:scroll;">
							<table class="table table-striped table-bordered table-hover dataTables-example " style="table-layout:fixed;margin-bottom: 0px;">
								   <tbody>
									   <tr>
										<th width="10%" colspan="2">
											<button type="button" class="btn btn-outline btn-default btn-xs" id="checkedAll">all</button>
											<button type="button" class="btn btn-outline btn-default btn-xs" onclick="checkLine(1)">1</button>
											<button type="button" class="btn btn-outline btn-default btn-xs" onclick="checkLine(2)">2</button>
											<button type="button" class="btn btn-outline btn-default btn-xs" onclick="checkLine(0)">3</button>
										<th width="40%">账号</th>
										<th width="10%">SEA</th>
										<th width="10%">SSS</th>
										<th width="15%">BNB</th>
										<th width="10%">授权</th>
										<th width="10%">状态</th>
										</tr>
								   </tbody>
							</table>
						</div>
						<div style="height:580px;overflow-y:scroll;">
							<table  class="table table-striped table-bordered table-hover dataTables-example " id="editable" style="table-layout:fixed;">
							   <tbody id="idsVal" class="list">
									<c:forEach items="${list}" var="item">
										<tr>
											<td width="5%">
												<input type="checkbox" name="check_all" value="${item.address }">
												<div id='${item.address }_auth' style='display:none;'>${item.auth }</div>
												<div id='${item.address }_keystore' style='display:none;'>${item.keystore }</div>
											</td>
											<td width="5%">${item.sort}</td>
											<td width="40%" onclick="show_shopm(this)" onload="loaditem(this)" id='${item.address}'>${item.address}</td>
											<td width="10%" id='${item.address}_sea'>-</td>
											<td width="10%" id='${item.address}_sss'>-</td>
											<td width="15%" id='${item.address}_bnb'>-</td>
											<td width="10%"  id='${item.address}_isAuth'>
												<c:choose>
													<c:when test="${item.isAuth }">
														<i class="fa fa-check text-navy" data="${item.isAuth }"></i>
													</c:when>
													<c:otherwise>
														<i class="fa fa-close text-danger" data="${item.isAuth }"></i>
													</c:otherwise>
												</c:choose>
											</td>
											<td  width="10%" align="center" id='${item.address}_status'>0/0</td>
										</tr>
									</c:forEach>
							   </tbody>
						   </table>
						</div>
                    </div>
                </div>
            </div>
            <div class="col-sm-3">
                <div class="ibox ">
                    <div class="ibox-content">
                        <div class="tab-content">
                            <div class="tab-pane active">
                                <div class="row">
                                    <div class="col-lg-8" id="result_img">
	                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
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
    <!-- 自定义js -->
    <script src="js/shark.js"></script>
    <script src="js/cookieUtil.js"></script>
    <script src="js/auth.js"></script>

    <script>
		function checkLine(index){
			var list=$("#editable").find(":checkbox");
			for(var i=0;i<list.length;i++){
				if((i+1)%3==index){
					$(list[i]).prop("checked",true);
				}else{
					$(list[i]).prop("checked",false);
				}
			}
		}
        $(function () {
            $('.full-height-scroll').slimScroll({
                height: '100%'
            });
            //全选
			var isChecked=false
			$("#checkedAll").click(function(){
				if(!isChecked){
					isChecked=true
				}else{
					isChecked=false
				}
				$("#editable").find(":checkbox").prop("checked",isChecked);
			});
			$("#start").click(function(){
				listAccount = [];
				$("#editable").find(":checkbox").each(function () {
					if ($(this).is(":checked")) {
						var id = $(this).val();
						if (id) {
							var sea = parseInt($("#" + id + "_sea").html());
							if (sea >= $("#sea").val()) {
								var address = $(this).val();
								var bool = $("#" + address + "_isAuth>i").attr("data");//判断是否授权
								if (bool == "true") {
									listAccount.push(address);
								} else {
									log(id.substr(0, 7) + "...未授权", 2);
								}
							} else {
								log(id.substr(0, 7) + "...账户余额不足", 2);
							}
						}
					}
				});
				cishu = parseInt($("#cishu").val());
				saveCookie();
				$("#start").hide();
				$("#stop").show();
				isOpen=true
				if(!isprogress){
					start();
				}
			});
			$("#stop").click(function(){
				isOpen=false;
				$("#start").show();
				$("#stop").hide();
			});
			$("#auth").click(function(){
				authAddress=[];
				$("#editable").find(":checkbox").each(function() { 
				    if ($(this).is(":checked")) { 
				    	var id=$(this).val();
				    	if(id){
				    		var bnb=parseFloat($("#"+id+"_bnb").html());
				    		if(bnb>0.0035){
				    			var address=$(this).val();
				    			//var bool=$("#"+address+"_isAuth>i").attr("data");//判断是否授权
				    			//if(bool=="false"){
								authAddress.push(address);
			    				//}
		    				}else{
        						log(id.substr(0,15)+"...BNB不足以扣除授权手续费",2);
        					}
        				}
				    } 
				});
				authStep1();
			});
        });
    </script>
</body>

</html>

