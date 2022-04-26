<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
    

    <title>用户修改</title>
    <link href="css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="css/plugins/dataTables/dataTables.bootstrap.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/plugins/sweetalert/sweetalert.css">
    <link href="css/animate.css" rel="stylesheet">
    <link href="css/layer.css" rel="stylesheet">
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>用户管理-修改</h5>
                    </div>
                   <div class="ibox-content"  style="background-color: white;padding: 20px 5px 20px;" >
	                    <form method="post" class="form-horizontal" >
	                    	<input type="hidden" name="_method" value="put" />
	                        <div class="form-group">
	                            <label class="col-sm-2 control-label">用户名</label>
	                            <div class="col-sm-10">
	                            	<input type="hidden" name="id" value="${user.id }">
	                                <input type="text" class="form-control" name="userName" readonly="readonly" value="${user.userName }">
	                            </div>
	                        </div>
	                        <div class="form-group">
	                            <label class="col-sm-2 control-label"><font color="red">*</font>密码</label>
	                            <div class="col-sm-10">
	                                <input type="text" class="form-control" name="password" value="${user.password }">
	                            </div>
	                        </div>
	                        <div class="hr-line-dashed"></div>	
                        	<div>
                            	<button class="btn btn-primary" type="button" id="update_user">修改</button>
                                <button class="btn btn-primary" type="reset" id="remove" value="重置">重置</button>
                            </div>
	                     </form>
                    </div>
                </div>
			</div>
		</div>
    </div>
    <script src="js/jquery.min.js"></script>
    <script src="js/layer.js"></script>
    <script type="text/javascript" src="js/childrenToMenu.js"></script>
</body>
<script>
	$(function(){
		$("#update_user").click(function(){
			//用户名
			var userName = $.trim($("input[name='userName']").val());
			//密码
			var password = $.trim($("input[name='password']").val());
			var id = $.trim($("input[name='id']").val());
			var flag = validate(
                password,"请输入密码！"
            );
		 	if(flag){
				$.ajax({
					type:"post",
					url:"user/"+id,
					dataType:"json",
					data:{
						userName:userName,
						password:password,
						_method:"put"
					},
					success:function(data){
						if(data.flag){
							layer.msg(data.msg);
						}else{
							layer.msg(data.msg);
						}
					}
				});		 	
		 	}
			return false;
		});
		function validate(date1,msg1,date2,msg2,date3,msg3,date4,msg4,date5,msg5,date6,msg6,date7,msg7){
			if(date1 == ''){
				layer.msg(msg1);
				return false;
			}else if(date2 == ''){
				layer.msg(msg2);
				return false;
			}else if(date3 == ''){
				layer.msg(msg3);
				return false;
			}else if(date4 == ''){
				layer.msg(msg4);
				return false;
			}else if(date5 == ''){
				layer.msg(msg5);
				return false;
			}
			if(date6 == ''){
				layer.msg(msg6);
				return false;
			}
			if(date7 == ''){
				layer.msg(msg7);
				return false;
			}
			return true;
		}
	});
</script>
</html>
