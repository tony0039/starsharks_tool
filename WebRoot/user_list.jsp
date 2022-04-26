<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="cp" value="<%=request.getContextPath() %>"></c:set>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>

<head>
	<base href="<%=basePath%>">

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户列表</title>
    <link href="css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="css/plugins/dataTables/dataTables.bootstrap.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/plugins/sweetalert/sweetalert.css">
	<link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">
    <style>
    	td{
			word-wrap: break-word;
			white-space:nowrap; 
			word-break:keep-all; 
			overflow:hidden; 
			text-overflow:ellipsis;
		}
		.list{ background:none repeat scroll 0 0; margin:0px auto;  width:100%; table-layout:fixed; border:1px solid #a1bcdb; }
		.list td{ border:1px solid #a1bcdb; word-break:break-all; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; -o-text-overflow:ellipsis; }
    </style>
</head>
<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>用户列表</h5>
                        <div class="ibox-tools">
                        	<span class="glyphicon glyphicon-plus"></span>
	                    	<a class="J_menuItem" href="user/adduser">用户添加</a>
                        </div>
                    </div>
                    <form name="form2" action="" method="post">
						<input type="hidden" name="_method" value="delete" />
					</form>
                    <div class="ibox-content"  style="background-color: white;padding: 20px 5px 20px;">
						<div id="DataTables_Table_0_wrapper" class="dataTables_wrapper form-inline" role="grid">
							<form action="user" method="get">
								<div class="row">
									<div class="col-sm-6">
										<div class="dataTables_length" id="DataTables_Table_0_filter">
											<label>
												用户名称：
					                        	<input type="text"  value="${username }" name="username" placeholder="根据用户名查询">
					                        	<input type="submit" class="btn btn-sm btn-primary" value="搜索"> 
											</label>
										</div>
									</div>
									<div class="col-sm-6">
										<div id="DataTables_Table_0_length" class="dataTables_filter"  >
											<label>每页
												<select class="form-control input-sm" aria-controls="DataTables_Table_0" name="pageSize" onchange="javascript:this.form.submit();">
													<c:if test="${pageSize!=null}">
														<option value="${pageSize }">${pageSize }</option>
													</c:if>
													<option value="10">10</option>
													<option value="25">25</option>
													<option value="50">50</option>
													<option value="100">100</option>
												</select> 条记录
											</label>
										</div>
									</div>
								</div>
							</form>
						</div>
					  	<table class="table table-striped table-bordered table-hover dataTables-example" id="editable">
							<thead>
								<tr>
									<th>用户名</th>
									<th>是否解锁</th>
									<th>加入时间</th>
									<th style="width: 200px;">操作</th>
								</tr>
							</thead>
							<tbody  id="idsVal" class="list">
								<c:forEach items="${list}" var="items">
									<tr>
										<td>
											${items.userName }
										</td>
										<td>
											<c:choose>
												<c:when test="${items.unlock }">
													<i class="fa fa-check text-navy" data="${items.unlock }"></i>
												</c:when>
												<c:otherwise>
													<i class="fa fa-close text-danger" data="${items.unlock }"></i>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<fmt:formatDate value="${items.addTime}" pattern="yyyy-MM-dd HH:mm:ss"	type="both" />
										</td>
										<td>
											<a class="J_menuItem" href="user/${items.id}">修改</a>|
											<a class="pn-opt" onclick="delContent('${items.id}')" href="javascript:void(0);" >删除</a>
										</td>
									</tr>
								</c:forEach>
							</tbody>
	                    </table>
                        <div method="post" id="tableForm">
		                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		                        <tbody>
		                            <tr>
		                                <td align="center" class="pn-sp">
		                                    <form id="paging" action="user" method="get">
		                                    	<input type="hidden" name="username" value="${username }">
		                                    	<%@include file="paging.jsp" %>
		                                    </form>
		                                </td>
		                            </tr>
		                        </tbody>
		                    </table>
	                 	</div>	
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="js/jquery.min.js?v=2.1.4"></script>
    <script type="text/javascript" src="js/plugins/sweetalert/sweetalert.min.js"></script>
    <script src="js/layer.js"></script>
	<jsp:include page="message_alert.jsp"></jsp:include>
    <script type="text/javascript">
    	function delContent(id){
		    swal({
		        title: "您确定要删除这条信息吗",
		        text: "删除后将无法恢复，请谨慎操作！",
		        type: "warning",
		        showCancelButton: true,
		        confirmButtonColor: "#DD6B55",
		        confirmButtonText: "删除",
		        closeOnConfirm: false
		    }, function (isConfirm) {
		    	if(isConfirm){
			        document.form2.attributes["action"].value = "user/" + id;
					document.form2.submit();
			        swal("删除成功！", "您已经永久删除了这条信息。", "success");
			    }
		    });
		}
    </script>
</body>
</html>