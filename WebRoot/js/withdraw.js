var web3=new Web3('https://bsc-dataseed.binance.org/');
var contract_sea=new web3.eth.Contract(contracts.sea.abi,contracts.sea.address);
var contract_sss=new web3.eth.Contract(contracts.sss.abi,contracts.sss.address);

/**
 * 加载余额 SEA,SSS,BNB
 * @param id 账号地址
 */
function loadBalance(id){
	contract_sea.methods.balanceOf(id).call({from:id},function(error,result){
		$("#"+id+"_sea").html(web3.utils.fromWei(result));
	});
	web3.eth.getBalance(id,function(error,result){
		$("#"+id+"_bnb").html(web3.utils.fromWei(result));
	});
	axios.get('https://starsharks.com/go/auth-api/account/base', {
		headers: {
			'content-type': "application/json",
			'content-encoding': "br",
			'accept-language':"zh-CN,zh;q=0.9",
			'x-frame-options': "SAMEORIGIN",
			'authorization':$("#"+id+"_auth").html()
		}
	}).then(function (response) {
		var data=response.data.data;
		console.log(data)
		$("#"+id+"_value").html(data.amount);
	}).catch(function (error) {
		console.log(error)
	});
}
function show_shopm(t){
	loadBalance($(t).attr("id"));
}
/**
 * 检查余额
 * @param id 账号地址
 * @param callback 检查完后回调
 */
function checkBalance(id,callback){
	contract_sea.methods.balanceOf(id).call({from:id},function(error,result){
		var nowSea=web3.utils.fromWei(result);
		if(nowSea<$("#sea").val()){
			callback(false)
		}
		web3.eth.getBalance(id,function(error,result){
			var nowBnb=web3.utils.fromWei(result);
			if(nowBnb<$("#bnbCount").val()){
				callback(false)
			}else{				
				callback(true)
			}
		});
	});
}

/**
 * 点击表格后更新当前行余额
 * @param t 表格对象
 */
function show_shopm(t) {
    var row = $(t).attr("id");
    loadItem($(t).attr("id"));
}

/**
 * 加载当前账号的余额
 * @param address 账号地址
 */
function loadItem(address) {
    loadBalance(address);
}

/**
 * 加载所有余额，地址为表格第3列
 */
function loadAll(){
	var list=$("#idsVal>tr");
	for (var i=0;i<list.length;i++) {
        var tdArr = list.eq(i).find("td");
        var address = tdArr.eq(3).html();
        loadBalance(address);
    }
}

/**
 * 如果字符串超过63位则返回，不超过则前面填充0
 * @param str 初始字符串
 * @returns {string|*} 填充后字符串
 */
function fillZero(str){
	str=str.replace("0x","");
	var result="";
	if(str.length>63){
		return str;
	}
	for(var i=str.length;i<64;i++){
		result+="0";
	}
	result+=str;
	return result;
}

/**
 * 根据属性删除数组中的值
 * @param arr 原始数组
 * @param attr 属性
 * @param value 值
 * @returns {string|*}删除后返回的新数组，调用者需要重新给原始数组赋值
 * 比如原始数组为[{name:"张三",age:18},{name:"李四",age:15}]
 * attr为"name"
 * value为"张三"
 * 则返回[{name:"李四",age:15}]
 */
var arrRemoveJson = function (arr, attr, value) {
  if (!arr || arr.length == 0) {
    return ""
  }
  var newArr = arr.filter(function (item, index) {
    return item[attr] != value
  })
  return newArr
}
Array.prototype.remove = function(from, to) {
	  var rest = this.slice((to || from) + 1 || this.length);
	  this.length = from < 0 ? this.length + from : from;
	  return this.push.apply(this, rest);
};
loadAll();
var listAddress=[];
var address;
var index=0;
/**
 * 提现第一步
 */
function step1(){
	console.log("--------------step1----------------")
	$("#modal-form").show();
	if(index<listAddress.length){
		address=listAddress[index];
		var now=index+1;
		var all=listAddress.length;
		setLoading(parseFloat(now/all)*100+"%",""+now+"/"+all+"");
	}else{
		$("#modal-form").hide();
		swal({
			title: "执行结束",
			type: "info",
			confirmButtonText:"好的"
		});
		return;
	}
	axios.post('https://starsharks.com/go/auth-api/account/withdraw-sea-v2',{},{
		headers: {
			'content-type': "application/json",
			'content-encoding': "br",
			'accept-language':"zh-CN,zh;q=0.9",
			'authorization':$("#"+address+"_auth").html(),
			'x-frame-options': "SAMEORIGIN"
		}
	}).then(function (response) {
		//console.log(response.data.code);
		var data=response.data.data;
		if(data){
			console.log(data);
			var n0="0x07bfe9e6";
			var n1=fillZero(data.token);
			var n2=fillZero(web3.utils.toHex(data.amount));
			var n3=fillZero(web3.utils.toHex(data.order_id));
			var n4=fillZero(web3.utils.toHex(data.deadline));
			var n5="00000000000000000000000000000000000000000000000000000000000000a0";
			var n6="0000000000000000000000000000000000000000000000000000000000000041";
			var n7=data.sign;
			var n8="00000000000000000000000000000000000000000000000000000000000000";
			var result=n0+n1+n2+n3+n4+n5+n6+n7+n8;
			step2(result);
		}else{
			toastrAlert("余额不足提现跳过",3)
			index+=1;
			step1();
		}
	}).catch(function (error) {
		toastrAlert("未授权跳过",3)
		index+=1;
		step1();
	});
}
function step2(order){
	console.log("--------------step2----------------")
	$.ajax({
		type: 'POST',
		url: "/withdraw/order",
		data: {
			order: order,
			address: address
		},
		dataType: "json",
		success: function (result) {
			if(result.code=="0"){
				if(result.hash!=null){
					hashAddress.push({
						"hash":result.hash,
						"address":result.address
					});
				}
			}else{
				toastrAlert("跳过",3)
				index+=1;
				step1();
			}
		}
	})
}
/**
 * 根据hash值获取区块连上的结果，判断是成功或失败
 * @param hash
 * @param address 和hash对应的账号，用于更新界面
 */
function step3(hash){
	console.log("--------------step3----------------")
	web3.eth.getTransactionReceipt(hash.hash ,function(error,result){
		if(!error){
			console.log(result);
			if(result){
				hashAddress=arrRemoveJson(hashAddress,"hash",hash.hash);
				if(result.status){
					toastrAlert(hash.address+"成功",1)
				}else{
					toastrAlert("失败跳过",3);
				}
				index+=1;
				step1();
			}
		}else{
			console.log("-------error---------");
			console.log(error);
		}
	})
}
var hashAddress=[];

/**
 * 轮询hash数组
 */
function refresHash(){
	for(var i=0;i<hashAddress.length;i++){
		step3(hashAddress[i]);
	}
}

/**
 * 1秒钟去更新hash数组
 */
window.setInterval(refresHash,1000);
/**
 * toastrAlert
 * @param message 需要提示的消息
 * @param type 类型(1 成功 2.警告 3.失败)
 */
function toastrAlert(message,type){
	if(message!=null&&message!="") {
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
		if(type==1){
			Command: toastr["success"](message)
		}else if(type==2){
			Command: toastr["info"](message)
		}else{
			Command: toastr["error"](message)
		}
	}
}

/**
 * 设置进度条
 * @param width 进度条宽度
 * @param content 进度条内容
 */
function setLoading(width,content){
	$("#status").html(content);
	$("#status_width").css({"width": ""+width+"","aria-valuemax":"100","aria-valuemin":"0","aria-valuenow":"35","role":"progressbar"});
}