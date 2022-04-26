function setCookie(name, value, iDay)
{
  var oDate=new Date();
  oDate.setDate(oDate.getDate()+iDay);
  document.cookie=name+'='+value+';expires='+oDate;
};
/*获取cookie*/
function getCookie(name){
	var arr=document.cookie.split('; ');//多个cookie值是以; 分隔的，用split把cookie分割开并赋值给数组
	for(var i=0;i<arr.length;i++){//历遍数组
		var ars=arr[i].split('=');//原来割好的数组是：user=simon，再用split('=')分割成：user simon 这样可以通过arr2[0] arr2[1]来分别获取user和simon
		if(ars[0]==name){//如果数组的属性名等于传进来的name
		return ars[1];//就返回属性名对应的值
		}
	}
	return ''; //没找到就返回空
}
function removeCookie(name)
{
  setCookie(name, 1, -1); 
};
function getCookieValue(id){
	var content=getCookie(id);
	if(content){
		$("#"+id).val(content);
	}
}
function setCookieValue(id){
	var content=$("#"+id).val();
	if(content){
		setCookie(id,content,1000*3600*24*15);
	}
}
function initCookie(){
	getCookieValue("sea");
	getCookieValue("gas");
	getCookieValue("gasLimit");
	getCookieValue("timeInterval");
	getCookieValue("cishu");
	getCookieValue("buyCount");
	getCookieValue("bnbCount");
}
initCookie()
function saveCookie(){
	setCookieValue("sea");
	setCookieValue("gas");
	setCookieValue("gasLimit");
	setCookieValue("timeInterval");
	setCookieValue("cishu");
	setCookieValue("buyCount");
	setCookieValue("bnbCount");
}