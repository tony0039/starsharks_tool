<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div style="text-align: center;">
    <div style="height: 28px;line-height: 22px;text-align: center;font-size: 12px;margin-top: 20px;">
        共 ${total } 条 页次:<font color="#1a91ff"> ${pageIndex }</font>/${totalPage }页
        每页
        <select name="pageSize"
                style="text-align: center;width: 47px; padding-right: 3px;border: 1px solid #e5e6e7; border-radius: 1px;"
                onchange="javascript:document.getElementById('paging').submit();">
            <c:if test="${pageSize!=null}">
                <option value="${pageSize }">${pageSize }</option>
            </c:if>
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
            <option value="100">100</option>
        </select>
        条
        <c:choose>
            <c:when test="${pageIndex==1}">
                首页
            </c:when>
            <c:otherwise>
                <a style="" href='javascript:gotoPage2(1)'>首页</a>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${pageIndex-1>0}">
                <a style="" href='javascript:gotoPage2(${pageIndex-1 })'>上一页</a>
            </c:when>
            <c:otherwise>
                上一页
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${pageIndex+1<=totalPage}">
                <a style="" href='javascript:gotoPage2(${pageIndex+1 })'>下一页</a>
            </c:when>
            <c:otherwise>
                下一页
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${pageIndex==totalPage}">
                尾页
            </c:when>
            <c:otherwise>
                <a style="" href='javascript:gotoPage2(${totalPage})'>尾页</a>
            </c:otherwise>
        </c:choose>
        转到：<input id="vpageIndex" type='text' style="text-align: center;width:30px;border: 1px solid #e5e6e7; border-radius: 1px;"/>
        <input type="hidden" value=${totalPage } id="vtotalPage">
        <input id="pageIndex" type="hidden" name="pageIndex" value='1'/>
        <a href="javascript:void(0)" onclick="gotoPage()" id="submitPage">确定</a>
    </div>
</div>
<script type="text/javascript">
    var gotoPage = function () {
        var pageIndex = parseInt(document.getElementById('vpageIndex').value);
        if (isNaN(pageIndex)) {
            alert("请输入数字");
            return;
        } else if (pageIndex > parseInt(document.getElementById('vtotalPage').value) || pageIndex < 1) {
            alert("无此页");
            return;
        }
        document.getElementById("pageIndex").value = pageIndex;
        document.getElementById("paging").submit();
    }

    function gotoPage2(num) {
        var pageIndex = num;
        document.getElementById("pageIndex").value = pageIndex;
        document.getElementById("paging").submit();
    }
</script>