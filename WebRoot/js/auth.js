var authAddress=[]

function authStep1(){//获取随机数
	console.log(authAddress)
	if(authAddress.length>0){
		authStep2(0);
	}else{
		swal({
			title: "没有要解锁的账号"
		})
	}
}
var goIndex=0;
function authStep2(index){
	goIndex=index
	console.log("需要授权数量:");
	console.log(authAddress.length)
	if(index<authAddress.length){
		var address=authAddress[index];
		console.log(address)
		var url="https://starsharks.com/go/api/login/random-string";
		$.getJSON(url,{},function(data){
			console.log("账号:"+address);
			var message=data.data.message
			var before=web3.utils.utf8ToHex(message);
			console.log("签名前字符串:"+before);
			$.ajax({
	    		type: 'POST',
    			url: "/rest/auth",
    			data: {
    				address:address,
    				message:message,
    				messageSign:before
    			},
    			dataType: "json",
    			success: function(result){
    				if(result.code==0){
    					axios.post('https://starsharks.com/go/api/login/verify-sign', {
    						account:address,
    						hexsign:result.hexsign,
    						message:message,
    						referer:""
    					},{
    				        headers: {
    				            'content-type': "application/json",
    				            'content-encoding': "br",
    				            'accept-language':"zh-CN,zh;q=0.9",
    				            'x-frame-options': "SAMEORIGIN"
    				        }
    				    }).then(function (response) {
    						console.log("----------verify-sign------------");
    						console.log(response);
    				    	if(response.data.code==0){
    					    	var data=response.data.data;
    					    	var authorization=data.authorization;//获取授权信息
								var qrauth=data.qr_code;//登录二维码信息
    					    	$.ajax({
    					    		type: 'POST',
    				    			url: "/rest/saveAuth",
    				    			data: {
    				    				address:address,
    				    				authorization:authorization,
										qrauth:qrauth
    				    			},
    				    			dataType: "json",
    				    			success: function(result){
    				    				authStep3(address)
    				    			}
    					    	});
    				    	}else{
    	    					swal({
    	    						title: response.data.message
    	    					},function(){
    	    						return;
    	    					})
    				    	}
    				    })
    				}else{
    					swal({
    						title: result.error
    					},function(){
    						return;
    					})
    				}
    			}
	    	});
		});
	}else{
		console.log("执行结束")
		swal({
			title: "执行结束"
		}, function(res){
			window.location.reload();
		})
	}
}

function authStep3(address){//批准交易
	$.ajax({
		type: 'POST',
		url: "/rest/approval",
		data: {
			from:address
		},
		dataType: "json",
		success: function(result){
			if (result.code==0){
				authHash.push({
					"hash":result.hash,
					"address":address
				})
			}else{
				swal({
					title: res.message
				},function(){
					return;
				})
			}
		}
	})
}

function authStep4(hash){//查询结果
	web3.eth.getTransactionReceipt(hash.hash ,function(error,result){
		if(!error){
			if(result&&result.status){
				authHash=arrRemoveJson(authHash,"hash",hash.hash);
				goIndex+=1
				toastrAlert(hash.address+":成功",1)//提示当前账号成功
				log(hash.address+"授权成功",1)
				authStep2(goIndex);
			}else{
				authStep2(goIndex);
			}
		}else{
			console.log("-------error---------");
			console.log(error);
		}
	})
}

var authHash=[];

/**
 * 轮询hash数组
 */
function refresAuthHash(){
	for(var i=0;i<authHash.length;i++){
		authStep4(authHash[i]);
	}
}

/**
 * 1秒钟去更新seahash数组
 */
window.setInterval(refresAuthHash,1000);