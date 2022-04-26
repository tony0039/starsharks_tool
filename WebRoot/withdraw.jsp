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
    <title>提现</title>
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

		.qrcodeBox {
			background-color: #fff;
			border: 1px solid #cfcfcf;
			border-radius: 4px;
			height: 300px;
			left: 20%;
			top:30%;
			padding: 5px;
			position: absolute;
			width: 300px;
			z-index: 1000;
		}
		.qrcodeIcon {
			background-image: url("img/ermIcon.png?v=201507271756");
			background-position: 11px 10px;
			background-repeat: no-repeat;
			display: inline-block;
			height: 25px;
			width: 33px;
		}
    </style>

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content  animated fadeInRight">
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-content">
                        <div class="row">
							<div class="col-sm-3 m-b-xs">
								<div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-default">数额</button>
	                                  </span>
									<input type="text" value="999" class="input-sm form-control"  id="seaValue">
								</div>
							</div>
                            <div class="col-sm-3 m-b-xs">
                                <div class="input-group">
	                                  <span class="input-group-btn">
	                                      <button type="button" class="btn btn-sm btn-primary" id="withdraw">提现</button>
	                                      <button type="button" class="btn btn-sm btn-primary" id="export" style="margin-left: 20px;">导出二维码</button>
	                                  </span>
	                          	</div>
                          	</div>
							<div class="col-sm-3 m-b-xs">
								<div class="input-group">
	                                  <span class="input-group-btn">
	                                  </span>
								</div>
							</div>
                        </div>
						<div  style="overflow-y:scroll;">
							<table class="table table-striped table-bordered table-hover dataTables-example " style="table-layout:fixed;margin-bottom: 0px;">
								   <tbody>
									   <tr>
											<th width="5%"><input type="checkbox" name="check_all" id="checkedAll" value=""></th>
											<th width="5%">二维码</th>
											<th width="5%">序号</th>
											<th width="45%">账号</th>
											<th width="10%">SEA可用</th>
											<th width="10%">SEA余额</th>
											<th width="10%">BNB</th>
											<th width="10%">授权</th>
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
											</td>
											<td width="5%">
												<div class="qrcodeIcon" _style="16" _id="5"
													 onMouseOut="hideImg('${item.address }')"
													 onmouseover="showImg('${item.address }')"></div>
												<div class="qrcodeBox" style="display: none;"
													 id="wxImg${item.address }">
													<div class="inIcon"></div>
													<img alt="未授权"
														 src="withdraw/url?address=${item.address }"
														 style="height: 100%; width: 100%;">
												</div>
											</td>
											<td width="5%">${item.sort}</td>
											<td width="45%" onclick='show_shopm(this)' id='${item.address}'>${item.address}</td>
											<td width="10%" id='${item.address}_value'>-</td>
											<td width="10%" id='${item.address}_sea'>-</td>
											<td width="10%" id='${item.address}_bnb'>-</td>
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
										</tr>
									</c:forEach>
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
    <!-- 自定义js -->
    <script src="js/withdraw.js"></script>

    <script>
		function showImg(index){
			document.getElementById("wxImg"+index).style.display='block';
		}
		function hideImg(index){
			document.getElementById("wxImg"+index).style.display='none';
		}
        $(function () {
            $('.full-height-scroll').slimScroll({
                height: '100%'
            });
			//全选
			$("#checkedAll").click(function(){
				var is = $(this).is(":checked");
				$("#editable").find(":checkbox").prop("checked",is);
			});
			$("#export").click(function(){
				swal({
					title:"是否导出二维码图片压缩文件？",
					type: "warning",
					showCancelButton: true,
					confirmButtonColor: "#DD6B55",
					confirmButtonText: "导出",
					closeOnConfirm: false
				},function (isConfirm) {
					if(isConfirm){
						window.location.href="withdraw/download";
					}
				});
			})
			$("#withdraw").click(function(){
				listAddress=[]
				$("#editable").find(":checkbox").each(function() {
					if ($(this).is(":checked")) {
						var id=$(this).val();
						if(id){
							listAddress.push(id);
						}
					}
				});
				if(listAddress.length==0){
					swal({
						title: "请选择提现账户"
					})
					return;
				}
				step1();
			});
        });
    </script>
</body>

</html>

