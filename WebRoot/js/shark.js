/**
 * 日志，会显示在面板上
 * @param content 内容
 * @param type类型（1.成功 2.警告 3.失败）
 * @param count 同时显示日志在面板中的数量，默认20，超出一条会删除最后一条
 */
function log(content,type,count){
	var icon='';
	if(type==1){
		icon='<i class="fa fa-check text-navy"></i>';
	}else if(type==2){
		icon='<i class="fa fa-exclamation text-warning"></i>';
	}else if(type==3){
		icon='<i class="fa fa-close text-danger"></i>';
	}
	var result='<div class="media-body">'+icon+
		'<small>'+content+'</small><br/>'+
		'<small class="text-muted">'+curentTime()+'</small>'+
		'<div class="hr-line-dashed" style="margin:5px 0"></div>'+
	'</div>';
	$('#result_img').prepend(result);
	if(!count){
		count=20;
	}
	if($("#result_img>.media-body").length>count){
		$("#result_img>.media-body")[count-1].remove();
	}
}

/**
 * 获取当前时间,格式为0000-00-00 00:00:00
 * @returns {string}
 */
function curentTime(){ 
       var now = new Date();
       var year = now.getFullYear();
       var month = now.getMonth() + 1;
       var day = now.getDate();
       var hh = now.getHours(); 
       var mm = now.getMinutes();
       var ss = now.getSeconds();
       var clock = year + "-";
       if(month < 10)
           clock += "0";
       clock += month + "-";
       if(day < 10)
           clock += "0";
       clock += day + " ";
       if(hh < 10)
           clock += "0";
       clock += hh + ":";
       if (mm < 10) clock += '0'; 
       clock += mm+ ":"; 
       if (ss < 10) clock += '0'; 
       clock += ss; 
       return(clock); 
 }

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
	contract_sss.methods.balanceOf(id).call({from:id},function(error,result){
		$("#"+id+"_sss").html(web3.utils.fromWei(result));
	});
	web3.eth.getBalance(id,function(error,result){
		$("#"+id+"_bnb").html(web3.utils.fromWei(result));
	});
}

/**
 * 检查余额
 * @param id 账号地址
 * @param callback 检查完后回调
 */
function checkBalance(id,callback){
	contract_sea.methods.balanceOf(id).call({from:id},function(error,result){
		var nowSea=web3.utils.fromWei(result);
		if(parseInt(nowSea)<parseInt($("#sea").val())){
			callback(false)
		}
		web3.eth.getBalance(id,function(error,result){
			var nowBnb=web3.utils.fromWei(result);
			if(parseFloat(nowBnb)<parseFloat($("#bnbCount").val())){
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
 * 加载所有余额，地址为表格第二列
 */
function loadAll(){
	var list=$("#idsVal>tr");
	log("加载账户["+list.length+"]条",1);
	for (var i=0;i<list.length;i++) {
        var tdArr = list.eq(i).find("td");
        var address = tdArr.eq(2).html();
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
var isOpen=false;
var listAccount=[];
var progressAddress={};
var nowShark;
var nowTimeout;
var isprogress=false;

function start(){
	pause(function(){
		isprogress=true
		//将准备租鱼的数组状态全改成0/2
		for(var i=0;i<listAccount.length;i++){
			setLoading(listAccount[i],"0");
		}
		step1();
	})
}
//获取智能模式的次数，如果开启智能模式则返回测试，否则返回0
function getMind(){
	if($("#setting").is(":checked")){
		return parseInt($("#cishu").val());
	}else{
		return 0;
	}
}
function loadMarket(price,callback){
	axios.post('https://starsharks.com/go/api/market/sharks', {
		'class': [],
		'star': 0,
		'pureness': 0,
		'hp': [0,200],
		'speed': [0,200],
		'skill': [0,200],
		'morale': [0,200],
		'body': [],
		'parts': [],
		'rent_cyc': 0,
		'rent_except_gain': [price,price],
		'skill_id': [0,0,0,0],
		'full_energy': false,
		'page': 1,
		'filter': 'rent',
		'sort': 'PriceAsc',
		'page_size': 36
	},{
		headers: {
			'content-type': "application/json",
			'content-encoding': "br",
			'accept-language':"zh-CN,zh;q=0.9",
			'authorization':"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDc4Mzc5MzcsImp0aSI6IjB4ZWFkNzJmNmJiMzhhMjgwY2E1NmViYjkwYjQ4N2EzZTM0NWQ3NThlNyIsInN1YiI6IndlYiJ9.C49ZkLG7ZhEJECBZM41d9ySnqyboR7KtLAZlN4PbpsM",
			'x-frame-options': "SAMEORIGIN"
		}
	}).then(function (response) {
		if(response.data.data.total_count>0){
			var sharks=response.data.data.sharks;
			var count=sharks.length;
			callback(count)
		}else{
			callback(0)
		}
	}).catch(function (error) {
		callback(0)
	});
}
/**
 * 租鱼第一步，判断次数后调用step2
 */
function step1(){
	//从集合中取出第一个元素，进行操作
	pause(function() {
		console.log("--------------step1----------------")
		if (listAccount.length > 0) {
			progressAddress.address = listAccount[0];
			progressAddress.count = 0;
			beforeStep2()
		}else{
			$("#stop").click();
			isprogress=false
		}
	})
}

function beforeStep2(){
	pause(function() {
		var cishu = getMind();
		if (cishu > 0) {
			loadMarket(parseInt($("#sea").val()), function (count) {//开启智能模式则先请求市场
				if (count > cishu) {//如果市场的数量大于智能模式设置的数量，则往下运行
					step2();
				} else {//如果市场的数量不满足智能模式设置的数量，则暂停1秒后重复调用
					log("市场数量不足以达到智能模式条件,重复拉取", 2);
					delay(1, function () {
						beforeStep2()
					})
				}
			})
		} else {
			step2();
		}
	})
}
/**
 * 拉取爬虫信息
 * 判断当前执行的账号需要租鱼数量
 * 检查余额
 */
function step2(){
	//拉取倒计时列表,获取第一个
	pause(function() {
		console.log("--------------step2----------------")
		console.log("队列账号："+listAccount.length);
		if(progressAddress.count<parseInt($("#buyCount").val())){
			console.log("当前账号："+progressAddress.count);
			log("切入账号,检查余额:"+progressAddress.address.substr(0,20)+"...",1);
			checkBalance(progressAddress.address,function(res){
				if(res){
					setLoading(progressAddress.address,progressAddress.count);
					getShark();
				}else{
					log(progressAddress.address.substr(0,20)+"...余额不足，跳过！",3);
					listAccount=listAccount.splice(1,listAccount.length);
					progressAddress={};
					step1();
				}
			})
		}else{
			if(listAccount.length>0){
				listAccount=listAccount.splice(1,listAccount.length);
				progressAddress={};
				step1();
			}else{
				$("#stop").click();
				isprogress=false
			}
		}
	})
}

/**
 * 获取爬虫的鲨鱼
 */
function getShark(){
	var seaPrice=parseInt($("#sea").val());
	$.ajax({
		type:"get",
		url:"shark/list?price="+seaPrice+"&start=5&end=30",
		dataType: "json",
		success: function(result){
			var data=result.data
			if(data){
				nowShark=data[0].id;
				nowTimeout=data[0].expireTime;
				log("获取鲨鱼:"+nowShark,1);
				console.log(data[0]);
				step3();
			}else{
				if(!isOpen){
					return;
				}else{
					delay(1,function(){
						getShark();
					});
				}
			}
		}
	})
}
/**
 * 拉取爬虫信息
 * 判断当前执行的账号需要租鱼数量
 * 检查余额
 */
function step2_old(){
	//拉取倒计时列表,获取第一个
	pause(function() {
		console.log("--------------step2----------------")
		console.log("队列账号：" + listAccount.length);
		var seaPrice = parseInt($("#sea").val());
		var url = "http://192.168.0.125:8080/getSharkInfoList";
		var param = {
			type: 0,
			price: seaPrice
		}
		if (progressAddress.count < parseInt($("#buyCount").val())) {
			console.log("当前账号：" + progressAddress.count);
			log("切入账号,检查余额:" + progressAddress.address.substr(0, 20) + "...", 1);
			checkBalance(progressAddress.address, function (res) {
				if (res) {
					setLoading(progressAddress.address, progressAddress.count);
					$.getJSON(url, param, function (data) {
						if (data.length > 0) {
							nowShark = data[0].id;
							nowTimeout = data[0].expireTime * 1000;
							log("获取鲨鱼:" + nowShark, 1);
							console.log(data[0]);
							step3();
						}
					});
				} else {
					log(progressAddress.address.substr(0, 20) + "...余额不足，跳过！", 3);
					listAccount = listAccount.splice(1, listAccount.length);
					progressAddress = {};
					step1();
				}
			})
		} else {
			if (listAccount.length > 0) {
				listAccount = listAccount.splice(1, listAccount.length);
				progressAddress = {};
				step1();
			} else {
				$("#stop").click();
			}
		}
	})
}

/**
 * 重复拉取鲨鱼详情，一直到鱼的租用状态为0，并且价格符合设定值
 */
function step3(){
	//拉取鲨鱼详情
	pause(function() {
		console.log("--------------step3----------------")
		try {
			axios.get('https://starsharks.com/go/api/market/shark-detail?shark_id=' + nowShark, {}, {
				headers: {
					'content-type': "application/json",
					'content-encoding': "br",
					'accept-language': "zh-CN,zh;q=0.9",
					'x-frame-options': "SAMEORIGIN"
				}
			}).then(function (response) {
				var data = response.data.data;
				console.log("----获取鲨鱼结果-----");
				console.log(data.sheet.status);
				if (data.sheet.status == 0) {
					console.log("----鲨鱼价格-----");
					console.log(data.sheet.rent_except_gain);
					if (data.sheet.rent_except_gain > parseInt($("#sea").val())) {
						log("租用费用已改", 3);
						beforeStep2();
					} else {
						log("准备提交订单:" + nowShark, 1);
						step4();
					}
				} else {
					var now = Date.parse(new Date());
					if (nowTimeout + 20000 > now) {
						step3();
					} else {
						beforeStep2();
					}
				}
			}).catch(function (error) {
				if (error.response) {
					console.log(error.response.data.message);
					log(error.response.data.message, 3)
				} else {
					console.log('Error', error.message);
				}
				beforeStep2();
			});
		} catch (error) {
			beforeStep2();
		}
	})
}

/**
 * 拉取当前账号租鱼的订单
 */
function step4(){
	pause(function() {
		console.log("--------------step4----------------")
		try {
			axios.post('https://starsharks.com/go/auth-api/market/rent-in', {
				shark_id: parseInt(nowShark)
			}, {
				headers: {
					'content-type': "application/json",
					'content-encoding': "br",
					'accept-language': "zh-CN,zh;q=0.9",
					'authorization': $("#" + progressAddress.address + "_auth").html(),
					'x-frame-options': "SAMEORIGIN"
				}
			}).then(function (response) {
				console.log("----获取的订单code-----");
				console.log(response.data.code);
				if (response.data.code == 0) {
					var data = response.data.data;
					console.log(data);
					var n0 = "0x0bb180e1";
					var n1 = fillZero(web3.utils.toHex(data.order_id));
					var n2 = "000000000000000000000000416f1d70c1c22608814d9f36c492efb3ba8cad4c";
					var n3 = fillZero(web3.utils.toHex(data.token_owner));
					var n4 = "0000000000000000000000000000000000000000000000000000000000000100";
					var n5 = "0000000000000000000000000000000000000000000000000000000000000180";
					var n6 = "00000000000000000000000000000000000000000000000000000000000001a0";
					var n7 = fillZero(web3.utils.toHex(data.deadline));
					var n8 = "00000000000000000000000000000000000000000000000000000000000001c0";
					var n9 = "0000000000000000000000000000000000000000000000000000000000000003";
					var n10 = fillZero(web3.utils.toHex(data.token_id));
					var n11 = fillZero(web3.utils.toHex(data.expire_at));
					var n12 = fillZero(web3.utils.toHex(data.price));
					var n13 = "0000000000000000000000000000000000000000000000000000000000000000";
					var n14 = "0000000000000000000000000000000000000000000000000000000000000000";
					var n15 = "0000000000000000000000000000000000000000000000000000000000000041";
					var n16 = data.signature;
					var n17 = "00000000000000000000000000000000000000000000000000000000000000";
					var result = n0 + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17;
					log("成功载入订单:" + nowShark, 1);
					step5(result);
				} else {
					beforeStep2()
				}
			}).catch(function (error) {
				beforeStep2()
			});
		} catch (error) {
			beforeStep2()
		}
	})
}
/**
 * 将订单提交到后台去发送交易，并将结果放入hash数组
 * @param order 订单信息
 */
function step5(order){
	pause(function() {
		console.log("--------------step5----------------")
		//请求hash，成功则记录数量，数量满足则移除元素，然后进入step1，否则继续进入step2
		$.ajax({
			type: 'POST',
			url: "/rest/order",
			data: {
				order: order,
				address: progressAddress.address,
				gas: $("#gas").val(),
				gasLimit: $("#gasLimit").val()
			},
			dataType: "json",
			success: function (result) {
				if (result.code == "0") {
					if (result.hash != null) {
						hashAddress.push({
							"hash": result.hash,
							"address": result.address
						});
					}
				} else {
					log(result.message, 3)
					toastrAlert("跳过", 3)
					listAccount = listAccount.splice(1, listAccount.length);
					progressAddress = {};
					step1();
				}
			}
		});
	})
}

/**
 * 根据hash值获取区块连上的结果，判断是成功或失败
 * @param hash
 * @param address 和hash对应的账号，用于更新界面
 */
function step6(hash,address){
	console.log("--------------step6----------------")
	web3.eth.getTransactionReceipt(hash ,function(error,result){
		if(!error){
			console.log(result);
			if(result){
				hashAddress=arrRemoveJson(hashAddress,"hash",hash);
				if(result.status){
					log("成功",1);
					toastrAlert("成功",1)
					progressAddress.count+=1;
					setLoading(progressAddress.address,progressAddress.count);
					loadItem(progressAddress.address);
					beforeStep2();
				}else{
					log("失败",3);
					toastrAlert("租鱼失败",3)
					loadItem(address);
					beforeStep2();
				}
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
		if(isOpen) {
			step6(hashAddress[i].hash, hashAddress[i].address);
		}
	}
}

/**
 * 1秒钟去更新hash数组
 */
window.setInterval(refresHash,1000);

/**
 * 设置账号租鱼状态，当前执行的数量
 * @param id 账号
 * @param content 当前进展
 */
function setLoading(id,content){
	$("#"+id+"_status").html(content+"/"+$("#buyCount").val());
}

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
function delay(n,callback){
	setTimeout(callback,n*1000);
}
//暂停方法
function pause(callback){
	if(!isOpen){
		console.log("----------暂停----------------")
		delay(1,function(){
			pause(function(){
				callback()
			})
		})
	}else{
		callback()
	}
}
//toastrAlert("租鱼失败",3)