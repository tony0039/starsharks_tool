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
        url: "/transferShark/list",
        data: data,
        dataType: "json",
        success: function(result){
            console.log(result);
            if(result.code==0){
                var body="";
                for(var i=0;i<result.data.length;i++) {
                    var data=result.data[i];
                    var load="loadContainerBalance('"+data.address+"','"+container+"')";
                    var auth=data.address+"_auth";
                    var checkBox="";
                    if(container=="container2"){
                        checkBox='<input type="radio" name="check_all" value="'+data.address+'">'
                    }
                    var sort='<td width="5%">'+data.sort+'</td>';
                    var tr = '<tr>' +
                        '<td width="5%">' +checkBox +
                        '<div style="display: none;" id="'+data.address+'_auth_'+container+'">'+data.auth+'</div>'+
                        '</td>' +sort +
                        '<td width="45%" onClick="'+load+'" id='+data.address+'_'+container+'>'+data.address+'</td>' +
                        '<td width="15%" id="'+data.address+'_attr_'+container+'">-</td>' +
                        '<td width="15%" id="'+data.address+'_sheet_'+container+'">-</td>' +
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
    web3.eth.getBalance(id,function(error,result){
        $("#"+id+"_bnb_"+container).html(web3.utils.fromWei(result));
    });
    loadShark(id,container);
}

function loadShark(id,container){
    try {
        axios.post('https://starsharks.com/go/auth-api/account/sharks', {
            page: 1,
            page_size: 60,
            star: 0,
            tag_type: 0
        }, {
            headers: {
                'content-type': "application/json",
                'content-encoding': "br",
                'accept-language': "zh-CN,zh;q=0.9",
                'x-frame-options': "SAMEORIGIN",
                'authorization':$("#"+id+"_auth_"+container).html()
            }
        }).then(function (response) {
            console.log("----获取鲨鱼结果-----");
            var sharks=response.data.data.sharks
            var result="";
            var attr={};
            var sheet={};
            var sharkObjs=[];
            for(var i=0;i<sharks.length;i++){
                if(sharks[i].sheet){
                    var star="星级"+sharks[i].attr.star;
                    if(sheet[star]){
                        sheet[star]+=1;
                    }else{
                        sheet[star]=1;
                    }
                    var obj={
                        img:sharks[i].attr.genes,
                        id:sharks[i].attr.shark_id,
                        star:sharks[i].attr.star,
                        power:sharks[i].attr.power,
                        owner:sharks[i].attr.owner,
                        rentOut:sharks[i].sheet.rent_expire_at,
                        type:2
                    };
                    sharkObjs.push(obj);
                }else{
                    var star="星级"+sharks[i].attr.star;
                    if(attr[star]){
                        attr[star]+=1;
                    }else{
                        attr[star]=1;
                    }
                    var obj={
                        img:sharks[i].attr.genes,
                        id:sharks[i].attr.shark_id,
                        star:sharks[i].attr.star,
                        power:sharks[i].attr.power,
                        owner:sharks[i].attr.owner,
                        type:1
                    };
                    sharkObjs.push(obj);
                }
            }
            if(JSON.stringify(attr) != '{}'){
                $("#"+id+"_attr_"+container).html(JSON.stringify(attr));
            }else{
                $("#"+id+"_attr_"+container).html("-");
            }
            if(JSON.stringify(sheet) != '{}') {
                $("#" + id + "_sheet_" + container).html(JSON.stringify(sheet));
            }else{
                $("#" + id + "_sheet_" + container).html("-");
            }
            if(sharkObjs.length>0){
                var td=$("#" + id + "_info_" + container);
                var divs="";
                for(var i=0;i<sharkObjs.length;i++){
                    divs+=getSharkItem(sharkObjs[i],container);
                    if((i+1)%3==0){
                        divs+="<br/>";
                    }
                }
                if(td.html()){
                    td.html(divs);
                }else{
                    var tr='<tr><td width="100%" colspan="8" id='+id+'_info_'+container+'>';
                    tr+=divs;
                    tr+='</td></tr>';
                    $("#" + id + "_sheet_" + container).parent().after(tr);
                }
            }else{
                var td=$("#" + id + "_info_" + container);
                if(td.html()){
                    td.remove();
                }
            }
        }).catch(function (error) {
            if (error.response) {
                console.log(error.response.data.message);
            } else {
                console.log('Error', error.message);
            }
        });
    } catch (error) {
        console.log(error);
    }
}

function getSharkItem(obj,container){
    var img="https://starsharks.com/nft/img/0x"+obj.img+".png?w=100"; //鱼的图片
    var id=obj.id;       //鱼的id
    var star=obj.star;  //鱼的星级
    var starhtml=""      //根据星级得到html几颗星
    for(var i=0;i<star;i++){
        starhtml+='<div style="display: inline-block;">'+
            '<img alt="shark level" src="img/star.png" style="width:20px;">'+
        '</div>';
    }
    var powerhtml=obj.power+"/"+star*10;
    var event="transferShark('"+obj.owner+"','"+obj.id+"')";   //转出的事件
    var type=obj.type;  //类型（1.自己的 2.租的）
    var buttonhtml="";
    var renthtml="";
    if(type==1){
        if(container=="container1"){
            buttonhtml='<span class="input-group-btn">'+
                '<button type="button" class="btn btn-sm btn-primary" onclick='+event+'>转出</button>'+
            '</span>';
        }else{
            buttonhtml='<span class="input-group-btn">'+
                '<button type="button" class="btn btn-sm btn-primary">拥有</button>'+
            '</span>';
        }
    }else{
        buttonhtml='<span class="input-group-btn">'+
            '<button type="button" class="btn btn-sm btn-default">租用</button>'+
        '</span>';
        renthtml='<div class="css-1jxgzd7 timeout">'+
            '<div class="chakra-stack css-1paq6c8">'+
                '<input type="hidden" value="'+obj.rentOut+'">'+
                '<p class="chakra-text css-1wu7cx7">到期时间:<span>00</span></p>'+
            '</div>'+
        '</div>'
    }
    var div='<div style="width:240px;border:1px #C3D2D1FF solid;border-radius: 3px;display: inline-block;">'+
        '<div class="pull-left">'+
            '<a href="https://starsharks.com/zh-Hant/market/sharks/'+id+'" target="_blank">'+
                '<img src="'+img+'">'+
            '</a>'+
        '</div>'+
        '<div class="input-group" style="width:120px;">'+
            '<div>#'+id+'</div>'+
            '<div>'+starhtml+'</div>'+
            '<div>'+
                '<div style="display: inline-block;">'+
                    '<img alt="shark level" src="img/power.png">'+
                '</div>'+
                '<div style="display: inline-block;">'+
                    '<p>'+powerhtml+'</p>'+
                '</div>'+
            '</div>'+buttonhtml+
        '</div>'+renthtml+
    '</div>'
    return div;
}
var toAddress;
function transferShark(owner,sharkId){
    checkBNB(owner, function (result) {
        if (result) {
            getToAddress();
            if (toAddress) {
                swal({
                    title: "转账确认",
                    text: "确认将["+sharkId+"],转给账户:"+toAddress+"?",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "确认",
                    closeOnConfirm: false
                }, function (isConfirm) {
                    if (isConfirm) {
                        swal.close();
                        $("#modal-form").show();
                        $.ajax({
                            type: 'POST',
                            url: "/transferShark",
                            data: {
                                from: owner,
                                sharkId: sharkId,
                                to: toAddress
                            },
                            dataType: "json",
                            success: function (data) {
                                console.log("-------------结果---------------")
                                console.log(data)
                                if (data.code==0) {
                                    sharkHash.push({
                                        hash: data.data,
                                        address: owner,
                                        sharkId: sharkId
                                    });
                                } else {
                                    toastrAlert(data.message, 3)
                                    $("#modal-form").hide();
                                }
                            }
                        });
                    }
                })
            }
        } else {
            swal({
                title: "账户没有BNB,无法支付所需gas"
            })
        }
    })
}

function getToAddress(){
    var to=$("#editable2").find("input:radio:checked").val();
    if(to){
        toAddress=to;
    }else{
        swal({
            title: "请选择转入的账户"
        })
        return;
    }
}

/**
 * 根据hash值获取区块连上的结果，判断是成功或失败
 * @param hash
 */
function getResult(hash){
    web3.eth.getTransactionReceipt(hash.hash ,function(error,result){
        if(!error){
            console.log(result);
            if(result){
                sharkHash=arrRemoveJson(sharkHash,"hash",hash.hash);
                toastrAlert(hash.sharkId+":成功",1)//提示当前账号成功
                delay(2,function(){
                    loadContainerBalance(hash.address,"container1")
                    loadContainerBalance(hash.address,"container2")
                    loadContainerBalance(toAddress,"container2")
                    toAddress="";
                })
                $("#modal-form").hide();
            }
        }else{
            console.log("-------error---------");
            console.log(error);
        }
    })
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

var sharkHash=[];

/**
 * 轮询hash数组
 */
function refresSharkHash(){
    for(var i=0;i<sharkHash.length;i++){
        getResult(sharkHash[i]);
    }
}

/**
 * 1秒钟去更新seahash数组
 */
window.setInterval(refresSharkHash,1000);

function countDown(endTimeStamp) {
    var nowTimeStamp = new Date().getTime()
    var time = {}
    if (endTimeStamp > nowTimeStamp) {
        var mss = endTimeStamp - nowTimeStamp;
        var days = parseInt(mss / (1000 * 60 * 60 * 24))
        var hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
        var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60))
        var seconds = parseInt((mss % (1000 * 60)) / 1000)
        time = {
            day: days < 10 ? "0" + days : days,
            hour: hours < 10 ? "0" + hours : hours,
            minute: minutes < 10 ? "0" + minutes : minutes,
            second: seconds < 10 ? "0" + seconds : seconds,
            mss: mss,
        }
    } else {
        time = {
            day: '00',
            hour: '00',
            minute: '00',
            second: '00',
            mss: '00',
        }
    }
    return time
}
function getTimeDown(){
    $(".timeout input[type=hidden]").each(function(){
        var timeDown=countDown(this.value*1000);
        var str=timeDown.hour+":"+timeDown.minute+":"+timeDown.second;
        if(parseInt(timeDown.hour)<3){
            console.log("less 3 hour");
            $(this).next().children("span").css("color","red");
        }
        $(this).next().children("span").html(str);
    });
}
window.setInterval(getTimeDown,1000);