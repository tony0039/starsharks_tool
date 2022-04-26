<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<meta name="renderer" content="webkit" />

		<title>星鲨脚本客户端</title>
		<link href="css/bootstrap.min.css?v=3.3.7" rel="stylesheet" />
		<link href="css/font-awesome.min.css?v=4.4.0" rel="stylesheet" />
		<link href="css/animate.css" rel="stylesheet" />
		<link href="css/style.css?v=4.1.0" rel="stylesheet" />
		<link href="css/jquery.contextMenu.min.css" rel="stylesheet"/>
   		<link rel="stylesheet" href="css/style.css">
	    <link href="css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	    <script src="js/plugins/sweetalert/sweetalert.min.js"></script>
		<style>
			.px-4 {
				padding-left: 1rem;
				padding-right: 1rem;
			}
			.py-2 {
				padding-top: 0.5rem;
				padding-bottom: 0.5rem;
			}
			.bg-opacity-50 {
				--tw-bg-opacity: 0.5;
			}
			.bg-gray-900 {
				--tw-bg-opacity: 1;
				background-color: rgba(17,24,39,var(--tw-bg-opacity));
			}
			.rounded-md {
				border-radius: 0.375rem;
			}
			.flex-col {
				flex-direction: column;
			}
			.flex {
				display: flex;
			}
			.mx-2 {
				margin-left: 0.5rem;
				margin-right: 0.5rem;
			}
			.text-white {
				--tw-text-opacity: 1;
				color: rgba(255,255,255,var(--tw-text-opacity));
			}
			.text-sm {
				font-size: .875rem;
				line-height: 1.25rem;
			}
			.text-indigo-500 {
				--tw-text-opacity: 1;
				color: rgba(99,102,241,var(--tw-text-opacity));
			}
			.font-bold {
				font-weight: 700;
			}
		</style>
	</head>

	<body class="fixed-sidebar full-height-layout gray-bg" style="overflow: hidden;">
		<div id="wrapper">
			<!--左侧导航开始-->
			<nav class="navbar-default navbar-static-side" role="navigation">
				<div class="nav-close"><i class="fa fa-times-circle"></i></div>
				<div class="sidebar-collapse">
					<ul class="nav" id="side-menu">
						<li class="nav-header">
							<div class="dropdown profile-element">
								<span>
									<img alt="image" src="img/logo.png" width="180px">
								</span>
							</div>
							<div class="logo-element">G</div>
						</li>
						<li class="">
							<a class="J_menuItem" href="/shark">
								<i class="fa fa-legal"></i>
								<span class="nav-label">租鱼</span>
							</a>
							<a class="J_menuItem" href="/withdraw">
								<i class="fa fa-money"></i>
								<span class="nav-label">提现</span>
							</a>
							<a class="J_menuItem" href="/transfer">
								<i class="fa fa-credit-card"></i>
								<span class="nav-label">转账</span>
							</a>
							<a class="J_menuItem" href="/transferShark">
								<i class="fa fa-anchor"></i>
								<span class="nav-label">转鱼</span>
							</a>
							<a class="J_menuItem" href="/statistics">
								<i class="fa fa-area-chart"></i>
								<span class="nav-label">统计</span>
							</a>
							<c:if test="${loginUser.userName=='admin'}">
								<a class="J_menuItem" href="admin">
									<i class="fa fa-unlock"></i>
									<span class="nav-label">解锁</span>
								</a>
							</c:if>
							<c:if test="${loginUser.userName=='admin'}">
								<a class="J_menuItem" href="user">
									<i class="fa fa-users"></i>
									<span class="nav-label">账号管理</span>
								</a>
							</c:if>
						</li>
					</ul>
				</div>
			</nav>
			<!--左侧导航结束-->
			<!--右侧部分开始-->
			<div id="page-wrapper" class="gray-bg dashbard-1">
				<div class="row border-bottom">
					<nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0;">
						<ul class="nav navbar-top-links navbar-left">
							<li class="dropdown">	
								<div style="font-size:18px;color:#ffffff;padding: 20px 10px;min-height: 50px;">
									账号：${loginUser.realName}
								</div>
							</li>
						</ul>
						<ul class="nav navbar-top-links navbar-right">
							<li class="dropdown hidden-xs">
								<a class="right-sidebar-toggle" aria-expanded="false" id="login_out"> <i class="fa fa-sign-out"></i>退出</a>
							</li>
						</ul>
					</nav>
				</div>
				
				<div class="row J_mainContent" id="content-main">
					<iframe class="J_iframe" name="iframe0" width="100%" height="100%" src="/shark" frameborder="0" data-id="/shark" seamless></iframe>
				</div>
			</div>
			<!--右侧边栏结束-->
		</div>
		<!-- 全局js -->
		<script src="js/jquery.min.js?v=2.1.4"></script>
		<script src="js/bootstrap.min.js?v=3.3.7"></script>
		<script src="js/plugins/metisMenu/jquery.metisMenu.js"></script>
		<script src="js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
		<script src="js/plugins/contextMenu/jquery.contextMenu.min.js"></script>

		<!-- 自定义js -->
		<script src="js/hplus.js?v=4.1.0"></script>
		<script type="text/javascript" src="js/contabs.js"></script>
		<script type="text/javascript">
			$("#login_out").click(function(){
				window.location.href="user/logOut";
			});
		</script>
	</body>
</html>

