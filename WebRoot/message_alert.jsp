<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>    
<link href="css/plugins/toastr/toastr.min.css" rel="stylesheet">
<script src="js/plugins/toastr/toastr.min.js"></script>
<div class="alert alert-danger" id="message" style="display: none;">${message }</div>
<script type="text/javascript">
	$(function(){
		var message=$("#message").html();
	  	if(message!=null&&message!=""){
			toastr.options = {
			  closeButton: false,
			  debug: false,
			  progressBar: true,
			  positionClass: "toast-top-center",
			  onclick: null,
			  showDuration: "400",
			  hideDuration: "1000",
			  timeOut: "3000",
			  extendedTimeOut: "1000",
			  showEasing: "swing",
			  hideEasing: "linear",
			  showMethod: "fadeIn",
			  hideMethod: "fadeOut"
			}
	       	Command: toastr["error"]($("#message").html())
		}
	});
</script>