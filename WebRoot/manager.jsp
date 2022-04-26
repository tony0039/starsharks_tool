<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
	<title>管理端</title>

    <link rel="shortcut icon" href="favicon.ico"> <link href="css/bootstrap.min.css?v=3.3.7" rel="stylesheet">
    <link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">
    <link href="css/animate.css" rel="stylesheet">
    <link href="css/style.css?v=4.1.0" rel="stylesheet">

</head>

<body class="gray-bg top-navigation">

    <div id="wrapper">
        <div id="page-wrapper" class="gray-bg">
            <div class="wrapper wrapper-content">
                <div class="container">
                    <div class="row">
                        <div class="col-sm-12">
							<div class="ibox float-e-margins">
								<div class="ibox-title">
			                        <h5>基础设置</h5>
			                    </div>
								<div class="ibox-content">
									<div class="form-horizontal">
										<div class="form-group">
											<label class="col-sm-2 control-label">解锁密码</label>
											<div class="col-sm-6">
												<div class="input-group">
													<select class="input-sm form-control m-b" id="userSelect">
														<option value="">选择账号</option>
														<c:forEach items="${users}" var="item">
															<option value="${item.id}">${item.userName}</option>
														</c:forEach>
													</select>
													<input type="password" placeholder="输入密码" class="form-control" id="password">
													<span class="input-group-btn"> 
			                                     		<button type="button" class="btn btn-primary" id="btn1">解锁</button> 
			                                     		<button type="button" class="btn btn-primary" id="btn2" style="margin-left:5px;">同步keystore</button> 
			                                     	</span>
		                                     	</div>
											</div>
										</div>
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
    <link href="css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    <script src="js/plugins/sweetalert/sweetalert.min.js"></script>
	<script>
		$(function(){
			$("#btn1").click(function(){
				var password=$("#password").val();
				var userId=$("#userSelect").val();
				if(userId){
					if(password){
						$.ajax({
							type: 'POST',
							url: "/rest/password",
							data: {
								password:password,
								userId:userId
							},
							dataType: "json",
							success: function(result){
								swal({
									title: result.message
								})
							}
						})
					}else{
						swal({
							title: "请输入密码"
						})
					}
				}else{
					swal({
						title: "请选择解锁账户"
					})
				}
			});
			$("#btn2").click(function(){
				$.ajax({
					type: 'POST',
					url: "/admin/keystore",
					dataType: "json",
					success: function(result){
						swal({
    						title: result.message
    					})
					}
				})
			});
		})
	</script>

</body>

</html>




