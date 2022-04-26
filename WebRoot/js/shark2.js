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
		$("#result_img>.media-body").remove();
	}
}
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
//加载余额SEA,SSS,BNB
function loadBalance(id){
	contract_sea.methods.balanceOf(id).call({from:id},function(error,result){
		$("#"+id+"_sea").html(result/10**18);
	});
	contract_sss.methods.balanceOf(id).call({from:id},function(error,result){
		$("#"+id+"_sss").html(result/10**18);
	});
	web3.eth.getBalance(id,function(error,result){
		$("#"+id+"_bnb").html(result/10**18);
	});
}
function show_shopm(t) {
    var row = $(t).attr("id");
    loadItem($(t).attr("id"));
}
function loadItem(address) {
    loadBalance(address);
}
function loadAll(){
	var list=$("#idsVal>tr");
	log("加载账户["+list.length+"]条",1);
	for (var i=0;i<list.length;i++) {
        var tdArr = list.eq(i).find("td");
        var address = tdArr.eq(1).html();
        loadBalance(address,tdArr);
    }
}
var listAccount=[];
loadAll();
var countReady=-1;
function loadMarket(price){
	if(listAccount.length>0){
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
				if(listAccount.length<count){
					count=listAccount.length;
				}
				log("准备抢购["+count+"]条鲨鱼",1);
				var num=listAccount.length-1;
				for(var i=0;i<count;i++){
					var sharkid=sharks[num].attr.shark_id;
					loadSharkOrder(listAccount[i],sharkid);
					num--;
				}
			}else{
				log("未获取到符合条件的鲨鱼!",2);
		    	countReady=listAccount.length;
			}
		}).catch(function (error) {
			console.log(error);
	    	log(error,3);
	    	countReady=listAccount.length;
		});
	}else{
    	log("没有符合要求的账户，脚本无法启动",3);
	}
}
function loadPreMarket(price){
	var url="http://192.168.0.125:8080/getSharkInfoList"
		var param={
			type:0,
			price:price
		}
		$.getJSON(url,param,function(data){
			if(data.length>0){
				var count=data.length;
				if(listAccount.length<count){
					count=listAccount.length;
				}
				log("准备抢购["+count+"]条鲨鱼",1);
				for(var i=0;i<count;i++){
					var sharkid=data[i].id;
					var timeOut=data[i].expireTime*1000;
					loadPreSharkOrder(listAccount[i],sharkid,timeOut);
				}
			}
		});
}
function loadPreSharkOrder(address,sharkId,timeOut){
	try{
		axios.post('https://starsharks.com/go/auth-api/market/rent-in', {
			shark_id: sharkId
		},{
	        headers: {
	            'content-type': "application/json",
	            'content-encoding': "br",
	            'accept-language':"zh-CN,zh;q=0.9",
	            'authorization':"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDc4Mzc5MzcsImp0aSI6IjB4ZWFkNzJmNmJiMzhhMjgwY2E1NmViYjkwYjQ4N2EzZTM0NWQ3NThlNyIsInN1YiI6IndlYiJ9.C49ZkLG7ZhEJECBZM41d9ySnqyboR7KtLAZlN4PbpsM",
	            'x-frame-options': "SAMEORIGIN"
	        }
	    }).then(function (response) {
	    	if(response.data.code==0){
		    	var data=response.data.data;
		    	var n0="0x0bb180e1";
		    	var n1=fillZero(web3.utils.toHex(data.order_id));
		    	var n2="000000000000000000000000416f1d70c1c22608814d9f36c492efb3ba8cad4c";
		    	var n3=fillZero(web3.utils.toHex(data.token_owner));
		    	var n4="0000000000000000000000000000000000000000000000000000000000000100";
		    	var n5="0000000000000000000000000000000000000000000000000000000000000180";
		    	var n6="00000000000000000000000000000000000000000000000000000000000001a0";
		    	var n7=fillZero(web3.utils.toHex(data.deadline));
		    	var n8="00000000000000000000000000000000000000000000000000000000000001c0";
		    	var n9="0000000000000000000000000000000000000000000000000000000000000003";
		    	var n10=fillZero(web3.utils.toHex(data.token_id));
		    	var n11=fillZero(web3.utils.toHex(data.expire_at));
		    	var n12=fillZero(web3.utils.toHex(data.price));
		    	var n13="0000000000000000000000000000000000000000000000000000000000000000";
		    	var n14="0000000000000000000000000000000000000000000000000000000000000000";
		    	var n15="0000000000000000000000000000000000000000000000000000000000000041";
		    	var n16=data.signature;
		    	var n17="00000000000000000000000000000000000000000000000000000000000000";
		    	var result=n0+n1+n2+n3+n4+n5+n6+n7+n8+n9+n10+n11+n12+n13+n14+n15+n16+n17;
				//console.log(result);
				log("成功载入订单:"+sharkId,1);
				submitOrder(address,result);
	    	}else{
				log(response.data.message,3);
	    	}
		}).catch(function (error) {
			console.log("-------403,继续尝试---------"+sharkId);
			var now=Date.parse(new Date());
			//console.log("当前时间戳:"+now);
			//console.log("过期时间戳:"+timeOut);
			console.log("时间差:"+(timeOut-now));
			if(timeOut>now){
				loadPreSharkOrder(address,sharkId);
			}else{
				countReady++;
			}
		});
	}catch (error) {
		  console.error(error);
	}
}
function loadSharkOrder(address,sharkId){
	try{
		axios.post('https://starsharks.com/go/auth-api/market/rent-in', {
			shark_id: sharkId
		},{
	        headers: {
	            'content-type': "application/json",
	            'content-encoding': "br",
	            'accept-language':"zh-CN,zh;q=0.9",
	            'authorization':"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDc4Mzc5MzcsImp0aSI6IjB4ZWFkNzJmNmJiMzhhMjgwY2E1NmViYjkwYjQ4N2EzZTM0NWQ3NThlNyIsInN1YiI6IndlYiJ9.C49ZkLG7ZhEJECBZM41d9ySnqyboR7KtLAZlN4PbpsM",
	            'x-frame-options': "SAMEORIGIN"
	        }
	    }).then(function (response) {
	    	if(response.data.code==0){
		    	var data=response.data.data;
		    	var n0="0x0bb180e1";
		    	var n1=fillZero(web3.utils.toHex(data.order_id));
		    	var n2="000000000000000000000000416f1d70c1c22608814d9f36c492efb3ba8cad4c";
		    	var n3=fillZero(web3.utils.toHex(data.token_owner));
		    	var n4="0000000000000000000000000000000000000000000000000000000000000100";
		    	var n5="0000000000000000000000000000000000000000000000000000000000000180";
		    	var n6="00000000000000000000000000000000000000000000000000000000000001a0";
		    	var n7=fillZero(web3.utils.toHex(data.deadline));
		    	var n8="00000000000000000000000000000000000000000000000000000000000001c0";
		    	var n9="0000000000000000000000000000000000000000000000000000000000000003";
		    	var n10=fillZero(web3.utils.toHex(data.token_id));
		    	var n11=fillZero(web3.utils.toHex(data.expire_at));
		    	var n12=fillZero(web3.utils.toHex(data.price));
		    	var n13="0000000000000000000000000000000000000000000000000000000000000000";
		    	var n14="0000000000000000000000000000000000000000000000000000000000000000";
		    	var n15="0000000000000000000000000000000000000000000000000000000000000041";
		    	var n16=data.signature;
		    	var n17="00000000000000000000000000000000000000000000000000000000000000";
		    	var result=n0+n1+n2+n3+n4+n5+n6+n7+n8+n9+n10+n11+n12+n13+n14+n15+n16+n17;
				//console.log(result);
				log("成功载入订单:"+sharkId,1);
				submitOrder(address,result);
	    	}else{
				log(response.data.message,3);
	    	}
		}).catch(function (error) {
			console.log("-------拉取订单error---------");
			console.log(error);
			log("拉取订单失败，重新尝试！",3);
			countReady++;
		});
	}catch (error) {
		  console.error(error);
	}
}
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
function submitOrder(address,order){
	$.ajax({
		  type: 'POST',
		  url: "/rest/order",
		  data: {
				order:order,
				address:address,
				gas:$("#gas").val(),
				gasLimit:$("#gasLimit").val()
		  },
		  dataType: "json",
		  success: function(result){
				if(result.code=="0"){
					hashAddress.push({
						"hash":result.hash,
						"address":result.address
					});
					//resultByHash(result.hash,result.address);
				}else{
					log("未知错误",3)
				}
		  }
		});
}
var hashAddress=[];
function resultByHash(hash,address){
	web3.eth.getTransactionReceipt(hash ,function(error,result){
		if(!error){
			console.log("-------result---------");
			console.log(result);
			if(result){
				hashAddress=arrRemoveJson(hashAddress,"hash",hash);
				countReady++;
				console.log("完成账号数:"+countReady);
				if(result.status){
					log("成功",1);
					loadItem(address);
				}else{
					log("失败",3);
					loadItem(address);
				}
			}
		}else{
			console.log("-------error---------");
			console.log(error);
		}
	})
}
function refresHash(){
	for(var i=0;i<hashAddress.length;i++){
		resultByHash(hashAddress[i].hash,hashAddress[i].address);
	}
}

var arrRemoveJson = function (arr, attr, value) {
  if (!arr || arr.length == 0) {
    return ""
  }
  var newArr = arr.filter(function (item, index) {
    return item[attr] != value
  })
  return newArr
}
window.setInterval(refresHash,1000);
var cishu=parseInt($("#cishu").val());
function start(){
	cishu=parseInt($("#cishu").val());
	if(cishu>0){
		if(countReady<0){
			countReady++;
			loadPreMarket(parseInt($("#sea").val()));
			cishu=parseInt($("#cishu").val());
			$("#cishu").val(cishu-1);
		}
		if(countReady>=listAccount.length){
			countReady=0;
			loadPreMarket(parseInt($("#sea").val()));
			cishu=parseInt($("#cishu").val());
			$("#cishu").val(cishu-1);
		}
	}else{
		$("#stop").click();
	}
}