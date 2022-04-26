/**
 * 查询账号列表
 * @param address 根据账号关键字模糊查询
 * @param container 查询结果显示的容器
 */
function list(address,container,userId){
    var data={
        address:address
    }
    if(userId){
        data.userId=userId;
    }
    $.ajax({
        type: 'get',
        url: "/transfer/list",
        data: data,
        dataType: "json",
        success: function(result){
            console.log(result);
            if(result.code==0){
                var body="";
                for(var i=0;i<result.data.length;i++) {
                    var data=result.data[i];
                    var load="loadContainerBalance('"+data.address+"','"+container+"')";
                    var tr = '<tr>' +
                        '<td width="5%">' +
                        '<input type="checkbox" name="check_all" value="'+data.address+'">' +
                        '</td>' +
                        '<td width="10%">'+data.sort+'</td>' +
                        '<td width="50%" onClick="'+load+'" id='+data.address+'>'+data.address+'</td>' +
                        '<td width="15%" id="'+data.address+'_sea_'+container+'">-</td>' +
                        '<td width="15%" id="'+data.address+'_bnb_'+container+'">-</td>' +
                    '</tr>';
                    body+=tr;
                }
                $("#"+container).html(body);
                loadContainer(result.data,container);
            }else{
                swal({
                    title: result.message
                })
            }
        }
    });
}

/**
 * 循环查询余额
 * @param list 需要查询余额的账户数组
 * @param container 将余额更新的容器
 */
function loadContainer(list,container){
    for (var i=0;i<list.length;i++) {
        var address = list[i].address;
        loadContainerBalance(address,container);
    }
}

/**
 * 查询账户余额
 * @param id 需要查询余额的账户
 * @param container 将余额更新的容器
 */
function loadContainerBalance(id,container){
    contract_sea.methods.balanceOf(id).call({from:id},function(error,result){
        $("#"+id+"_sea_"+container).html(web3.utils.fromWei(result));
    });
    web3.eth.getBalance(id,function(error,result){
        $("#"+id+"_bnb_"+container).html(web3.utils.fromWei(result));
    });
}

/**
 * 转账前判断用户输入
 * @param from 发起转账账户
 * @param to 接收转账账户
 * @param value 转账金额
 * @param type 转账类型（1.SEA 2.BNB）
 */
function step1(from,to,value,type){
    var bz="";
    if(type==1){
        bz="sea";
    }else if(type==2){
        bz="bnb";
    }
    if(from.length>1&&to.length>1){
        swal({
            title: "转账账户和目标账户不能同时多选"
        });
        return;
    }
    var str="确认从["+from.length+"]个账号\n转账到["+to.length+"]个账号\n["+value+"]个"+bz
    swal({
        title: "转账确认",
        text: str,
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "确认",
        closeOnConfirm: false
    }, function (isConfirm) {
        if(isConfirm){
            nonce=0;
            console.log("提交转账")
            if(from.length==1){
                fangshi=1
                readyFrom=from[0];
                readyTo=to;
            }else{
                fangshi=2
                readyFrom=from;
                readyTo=to[0];
            }
            swal({
                title: "发送请求中,请等待",
                type: "info",
                confirmButtonText:"好的"
            },function(){
                $("#modal-form").show();
            });
            step2(value,type,0)
        }else{
            console.log("取消转账")
        }
    });
}
var bnbRate=0.002;//bnb转账手续费阈值
var fangshi=1;//1.从1个账户转到多个账户 2.从多个账户转到1个账户
var readyFrom;
var readyTo;
var nonce=0;
var goIndex=0;
/**
 * 转账前判断用户输入
 * @param value 转账金额
 * @param type 转账类型（1.SEA 2.BNB）
 * @param index 当前执行次数
 */
function step2(value,type,index){//转账前判断账户余额
    var from;
    var to;
    goIndex=index;
    if(fangshi==1){
        console.log("OneToMany")
        if(index>=readyTo.length){
            $("#modal-form").hide();
            swal({
                title: "执行结束",
                type: "info",
                confirmButtonText:"好的"
            });
            nonce=0;
            return;
        }else{
            from=readyFrom;
            to=readyTo[index];
            var now=index+1;
            var all=readyTo.length;
            setLoading(parseFloat(now/all)*100+"%",""+now+"/"+all+"");
        }
    }
    if(fangshi==2){
        console.log("ManyToOne")
        if(index>=readyFrom.length){
            $("#modal-form").hide();
            swal({
                title: "执行结束",
                type: "info",
                confirmButtonText:"好的"
            });
            return;
        }else{
            from=readyFrom[index];
            to=readyTo;
            var now=index+1;
            var all=readyFrom.length;
            setLoading(parseFloat(now/all)*100+"%",""+now+"/"+all+"");
        }
    }
    if(type==1){
        console.log("转SEA")
        checkSEA(from,function(result){
            if(result){
                checkBNB(from,function(res){
                    if(res) {
                        step3(from, to, value, type, function () {
                            index += 1;
                            step2(value, type, index);
                        })
                    }else{
                        toastrAlert(from+":BNB不足,跳过",3)
                        index+=1;
                        step2(value,type,index);
                    }
                })
            }else{
                toastrAlert(from+":SEA不足,跳过",3)
                index+=1;
                step2(value,type,index);
            }
        })
    }
    if(type==2){
        console.log("转BNB")
        checkBNB(from,function(result){//如果是转bnb的话判断转账数额加上最低0.002的手续费够不够
            if(result){
                step3(from,to,value,type,function(){
                    index+=1;
                    step2(value,type,index);
                })
            }else{
                toastrAlert(from+":BNB不足,跳过",3)
                index+=1;
                step2(value,type,index);
            }
        })
    }
}

/**
 *
 * 转账前判断用户输入
 * @param from 发起转账账户
 * @param to 接收转账账户
 * @param value 转账金额
 * @param type 转账类型（1.SEA 2.BNB）
 * @param callback 完成后的回调函数
 */
function step3(from,to,value,type,callback){//发起转账
    var data={
        from:from,
        to:to,
        type:type,
        value:value
    }
    if(nonce>0){
        data.nonce=nonce+1;
    }
    $.ajax({
        type: 'POST',
        url: "/transfer",
        data: data,
        dataType: "json",
        success: function(result){
            if(result.code==0){
                if(fangshi==1){
                    //nonce=result.nonce;
                    if(type==1){//从一个账号转多个账号SEA
                        seaHash.push({
                            "hash":result.data,
                            "value":value,
                            "type":type
                        })
                    }else{
                        toastrAlert(from+":成功",1)
                        callback()
                    }
                }else{
                    toastrAlert(from+":成功",1)
                    callback()
                }
            }else{
                toastrAlert(result.message,3)
                callback()
            }
        }
    });
}

/**
 * 根据hash值获取区块连上的结果，判断是成功或失败
 * @param hash
 */
function step4(hash){
    web3.eth.getTransactionReceipt(hash.hash ,function(error,result){
        if(!error){
            console.log(result);
            if(result){
                seaHash=arrRemoveJson(seaHash,"hash",hash.hash);
                goIndex+=1
                //toastrAlert(result.address+":成功",1)//提示当前账号成功
                step2(hash.value,hash.type,goIndex)
            }
        }else{
            console.log("-------error---------");
            console.log(error);
        }
    })
}
/**
 * 检查sea余额，只要大于0就可以转账
 * @param id 账户地址
 * @param callback 回调
 */
function checkSEA(id,callback){
    contract_sea.methods.balanceOf(id).call({from:id},function(error,result){
        var nowSea=web3.utils.fromWei(result);
        if(nowSea>0){
            callback(true)
        }else{
            callback(false)
        }
    });
}

/**
 * 检查BNB余额
 * @param id 账户地址，只要大于0就可以转账
 * @param callback 回调
 */
function checkBNB(id,callback){
    web3.eth.getBalance(id,function(error,result){
        var nowBnb=web3.utils.fromWei(result);
        if(nowBnb>0){
            callback(true)
        }else{
            callback(false)
        }
    });
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

var seaHash=[];

/**
 * 轮询hash数组
 */
function refresSeaHash(){
    for(var i=0;i<seaHash.length;i++){
        step4(seaHash[i]);
    }
}

/**
 * 1秒钟去更新seahash数组
 */
window.setInterval(refresSeaHash,1000);
//$("#modal-form").show();
//var now=parseFloat(1/3)*100+"%";
//setLoading(now,""+1+"/"+2+"");