 $('.menu .item').tab();

 $('.ui.accordion').accordion();

 // 遮罩层
 //$('.dimmer').dimmer('show');

 //悬浮样式
 $('.special.cards .image').dimmer({
     on: 'hover'
 });

    //好友搜索
    $('.ui.search')
        .search({
            apiSettings: {
                url: '//api.github.com/search/repositories?q={query}'
            },
            fields: {
                results : 'items',
                title   : 'name',
                url     : 'html_url'
            },
            minCharacters : 3
        });

    //
    $('.ui.dropdown')
        .dropdown({
            useLabels: false
        });

        //提示框关闭事件
        $('.message .close').on('click', function() {
            $(this)
                .closest('.message')
                .transition('fade') ;
            $("#messageNotice").css("visibility","inherit");
        });


    // 对Date的扩展，将 Date 转化为指定格式的String<>fmt [yyyy-MM-dd,yyyy-MM-dd HH:mm:ss]
    // 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
    // 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "H+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    };

    /* 用于存放，在线时，用户的聊天记录。数据格式如下：
     * friendAccount是变量，表示用户账号
     * {
     *      friendAccount:[
     *          {
     *              sender:"",
     *              receiver:"",
     *              senderName:"",
     *              receiverName:"",
     *              sendTime:"",
     *              messageContent:""
     *          }
     *      ]
     * }
     *
     * */
    var messageContent = {};

    /* 用于存放，用户的未读消息。数据格式如下：
     * friendAccount是变量，表示用户账号
     * {
     *      friendAccount:1
     * }
     *
     * */
    var unreadMessageCount = {};

    // 用户下线方法，另一客户端登录时，已经将当期客户保存在redis中的信息替换。因此跳转到登录界面即可
    function forceOffline(){
        location.href="/logout";
    }

    // 从cookie中获取用户的账号
    var userAccount = $.cookie("userAccount");

    // 打开WebSocket, 传递的参数url没有同源策略的限制。
    var socket = IWebSocket({
        uri:'ws://127.0.0.1:8889/webSocket?userAccount='+userAccount,
        // 二进制数据类型
        binaryType:'arraybuffer',
        // 可以自定义四大事件
        onOpen: function(event) {
            console.log('连接');
        },
        onClose: function(event){
           console.log('断开');
        },
        onMessage: function(event) {
            //获取后台数据
            var resultData = event.data;
            console.log(resultData);
            // 后台数据可能是文本，也可能是流
            //字符串处理
            if(typeof(resultData)==="string"){
                var result=$.parseJSON(resultData);
                console.log("Received data string");
                var isOffline=result.isOffline;
                //如果值为1，则提示用户在另一客户端登录，强制用户下线
                if(isOffline){
                    $('#userOffline')
                      .modal('setting', 'closable', false)
                      .modal('show')
                    ;
                }else{
                    //正常消息，业务处理
                    var senderAccount = result.sender;
                    var account = $("#receiverAccount").val();
                     /* 判断用户的聊天框是否正在和发送者聊天，如果是则将消息追加到聊天框中;
                        如果不是给用户提示有未读消息 */
                    // 判断是否相等，如果相等则表示用户正在和此好友聊天
                    if(senderAccount === account){
                        var time = new Date(Number(result.sendTime)).Format("yyyy-MM-dd HH:mm:ss");
                        var html = '<div class="comment"><a class="avatar"><img src="/static/semantic/themes/default/assets/images/elliot.jpg">'+
                                   '</a><div class="content"><a class="author">'+result.senderName+' </a><div class="metadata"><span class="date">' + time +
                                   '</span></div><div class="text">' + result.messageContent + ' </div></div></div>';
                        $("#chatContent").append(html);
                    }else{
                        // 在对应的好友处给消息提示
                        messageUnreadCount(senderAccount);
                        // 将聊天记录保存到缓存中
                         pushMessage(result,senderAccount);
                    }
                }
            }else if(resultData instanceof ArrayBuffer){
                 // 流处理
                var buffer = event.data;
                console.log("Received arrayBuffer");
            }
        },
        onError: function(event) {
            console.log('异常')
         }
    });

    //未读好友消息样式
    function messageUnreadCount(senderAccount){
        var friendItem = $("#item-"+senderAccount+"");
        //var html = '<i class="comments icon" style="float:right;padding-top:8px"></i>';
        // 从缓存中取出未读消息条数
        var num =0;
        var count = unreadMessageCount[senderAccount];
        // 如果没有则初始化为 1 ，如果有就累加 1
        if(count){
            count = count+1;
            num = count;
        }else{
           num = num+1;
        }
        var id = senderAccount+"-label";
        var label =  $("#"+id+"");
        if(label.length){
            label.text(num);
        }else{
            var html = '<div class="ui teal left pointing label" style="float:right;margin-top:10px" id="'+id+'">'+num+'</div>';
            friendItem.append(html);
        }
        unreadMessageCount[senderAccount] = num;
    }

    var status=["正在连接","连接成功","正在关闭连接","连接已关闭"];

    //发送消息按钮，点击事件
    $("#sendMessageButton").on("click",function(){
        var messageArea = $("#messageArea");
        //获取用户输入的消息
        var messageContent = messageArea.val();
        // 发送者姓名
        var senderName = $("#userName").val();
        // 发送时间，毫秒数
        var sendTime = new Date().getTime();
        // 接收者账号
        var receiver = $("#receiverAccount").val();
        // 接收者姓名
        var receiverName = $("#receiverName").val();
        // 消息类型
        var messageType = "whisper";

        // 如果用户没有输入内容，不允许发送消息
        if(messageContent) {
            //添加状态判断，当为OPEN时，发送消息
            if (socket.readyState===1) {
                // 构建消息对象
                var message = new Message(userAccount,senderName,sendTime,receiver,receiverName,messageContent,messageType);
                // 发送消息
                socket.send(JSON.stringify(message));
                //系统当前时间，格式化日期
                var time = new Date().Format("yyyy-MM-dd HH:mm:ss");
                var html = '<div class="comment"><a class="avatar"><img src="/static/semantic/themes/default/assets/images/matt.jpg">'+
                           '</a><div class="content"><a class="author">我 </a><div class="metadata"><span class="date">' + time +
                           '</span></div><div class="text">' + messageContent + ' </div></div></div>'
                $("#chatContent").append(html);
                // 将聊天记录保存到缓存中
                pushMessage(message,receiver);
                //清空输入框的内容
                messageArea.val("");
            }else{
                alert(socket.readyState);
                //do something
            }
        }else{
            $("#messageNotice").show();
        }
    });
    
    //好友点击事件，加载缓存中的聊天记录
    $("#friendList").on("click",".item",function () {
        var friendAccounts = $(this).prevAll(".friendAccount").val();
        var friendNames = $(this).prevAll(".friendName").val();
        /*移除消息提醒图标*/
        $(this).find(".label").remove();
        if(friendAccounts){
            // 隐藏遮罩层
            $('#dimmer').removeClass("active");
            //将接收者账号保存在聊天框的隐藏域中，表示正在和此人聊天
            $("#receiverAccount").val(friendAccounts);
            $("#receiverName").val(friendNames);
            $("#chatWith").text("正在与"+friendNames+"聊天");
            //从缓存中取出与此账号的聊天记录
            var messageArray = messageContent[friendAccounts];
            if(messageArray && messageArray.length>0){
                //清空聊天框的内容
                $("#chatContent").empty();
                // 暂时搞两个不同的头像区分，对方和我; (可做个头像上传)
                var me = "/static/semantic/themes/default/assets/images/matt.jpg";
                var you = "/static/semantic/themes/default/assets/images/elliot.jpg";
                for(var i = 0,len = messageArray.length; i < len; i++){
                    var messageItem = messageArray[i];
                    var time = new Date(Number(messageItem.sendTime)).Format("yyyy-MM-dd HH:mm:ss");
                    var html = "";
                    // 对方的消息
                    if(friendAccounts === messageItem.sender){
                        html = '<div class="comment"><a class="avatar"><img src="'+you+'">'+
                                  '</a><div class="content"><a class="author">'+messageItem.senderName+' </a><div class="metadata"><span class="date">' + time +
                                  '</span></div><div class="text">' + messageItem.messageContent + ' </div></div></div>';
                    }else{
                        html = '<div class="comment"><a class="avatar"><img src="'+me+'">'+
                                  '</a><div class="content"><a class="author">我 </a><div class="metadata"><span class="date">' + time +
                                  '</span></div><div class="text">' + messageItem.messageContent + ' </div></div></div>';
                    }
                    $("#chatContent").append(html);
                }
                // 将未读消息设置为 0，消息提示div移除
                var count = unreadMessageCount[friendAccounts];
                if(count){
                    unreadMessageCount[friendAccounts] = 0;
                }
                var friendItem = $("#"+friendAccounts+"");
                var label = friendItem.find(".label");
                label.remove();
            }else{
                $("#chatContent").empty();
            }
        }
    });


    // 服务端每次接受流有最大长度限制(65536)，所以大文件需分块发送 -- 1024*1024*5;
    var j=0;
    var block = 1024*1024*5;   //每次传 5M
    var totalSize = 0;
     //发送文件
    $("#file").on('change',function() {
        var inputElement = document.getElementById("file");
        var fileList = inputElement.files;

        for ( var i = 0; i < fileList.length; i++) {
            var file = fileList[i];
            // 文件总长度
            totalSize = file.size;
            console.log("文件大小:"+totalSize);
            // 计算的出分块次数
             var fileBlockSize = 0;
             if(totalSize%block===0) fileBlockSize = totalSize/block;
             else fileBlockSize = totalSize/block+1;
             var startSize=0;
             var endSize = block;
             //向下取整
            var blockSize = Math.floor(fileBlockSize);
            var fileParam = {};
            fileParam["fileLength"]=totalSize;
            fileParam["fileBlockSize"]=blockSize;
            // 文件名 不可为空
            fileParam["fileName"]=file.name;
            // 参数分隔符，可自定义
            fileParam["paramBoundary"]=$.md5(file.name);
            var extraParam = {};
            // senderAccount字段必填
            extraParam["senderAccount"]="111";
            extraParam["receiverAccount"]="222";
            extraParam["sendTime"]=new Date().getTime();

            // 上传文件
            fileBlockUpload(fileParam,extraParam,startSize,endSize,blockSize,file);
         }
        return false;
    });


    // 保存消息到缓存
    function pushMessage(message,account){
        var messageArray = messageContent[account];
        if(messageArray){
            messageArray.push(message);
        }else{
            messageArray = [];
            messageArray.push(message);
        }
        messageContent[account]=messageArray;
    }

     // 分块发送文件---保证顺序递归调用，当前一个文件块读取完成后，才读取下一个文件块
     function sendBlock(startSize,endSize,file){
         var blob;
         if (file.webkitSlice) {
             blob = file.webkitSlice(startSize, endSize);
         } else if (file.mozSlice) {
             blob = file.mozSlice(startSize, endSize);
         } else if(file.slice) {
             blob = file.slice(startSize, endSize);
         } else {
             alert('不支持分段读取！');
             return false;
         }
         var reader = new FileReader();
         reader.readAsArrayBuffer(blob);
         if(startSize<totalSize){
             // 处理load事件。该事件在读取操作完成时触发。
             reader.onload = function loaded(e) {
                 var ArrayBuffer = e.target.result;
                 console.log("发送文件第" + (j++) + "部分,起始:"+startSize+"---结束:"+endSize+"块大小--"+ArrayBuffer.byteLength);
                 socket.send(ArrayBuffer);
                 startSize = endSize;
                 endSize = startSize+block;
                 // 进度条
                 var percent=0;
                 if(startSize<totalSize)
                    percent = Math.floor(startSize*100/totalSize);
                 else
                    percent = 100;
                 console.log("当前进度----"+percent);
                 //进度条更新
                 $('#fileProgress').progress({
                     percent: percent
                 });
                 // 递归调用
                 sendBlock(startSize,endSize,file);
             };
             // 处理loadstart事件。该事件在读取操作开始之前触发。
             reader.onloadstart = function(e) {
                // console.log('onloadstart ---> ', e);
             };
             // 处理loadend事件。该事件在读取操作结束时（不管成功失败）触发。
             reader.onloadend = function(e) {
                // console.log('onloadend ---> ', e);
             };
             // 处理progress事件。该事件在读取Blob时触发。
             reader.onprogress = function(e) {
                // console.log('onprogress ---> ', e);
             }
             // 处理error事件。该事件在读取操作发生错误时触发。
             reader.onerror = function(e) {
                 // console.log('onerror ---> ', e);
             }
         }else{
            totalSize=0;
         }
     }

     function getBuffer(str) {
         var buf = new ArrayBuffer(str.length * 2); // 每个字符占用2个字节
         var bufView = new Uint16Array(buf);
         for (var i = 0, strLen = str.length; i < strLen; i++) {
             bufView[i] = str.charCodeAt(i);
         }
         return buf;
     }

 //文件分块上传方法，包含请求头，文件体，结束标识
 function fileBlockUpload(fileParam,extraParam,startSize,endSize,fileBlockSize,file) {
        console.log("分块数--"+fileBlockSize);
     /** 构造请求头，附带额外信息*/
     var fileMessages = fileMessage(fileParam,extraParam);
     //将字符串 转换成 二进制流
     var start = getBuffer(fileMessages);
     console.log("开始--发送请求头--长度:"+fileMessages.length);
     socket.send(start);

     /** 发送文件块部分，文件主体分块上传*/
     // 循环发送文件块
     sendBlock(startSize,endSize,file);
/*     for(var x=0;x<fileBlockSize;x++){

         startSize=endSize;
         endSize=startSize+block;
     }*/

 }

     //  生成文件上传请求报文
     /**
      *      Content-Type:multipart/file
      *      Accept-Encoding:utf-8
      *      File-Length:
      *      File-Block-Size:
      *      File-Name:
      *      Param-Boundary:--abc
      *      --abc
      *      name="senderAccount"
      *      111
      *
      *      --abc
      *      name="receiverAccount"
      *      222
      *
      *      --abc
      *      name="sendTime"
      *      131231313123
      *
      *      --abc
      *
      */
     function fileMessage(fileParam,extraParam) {
         var str = "Content-Type:multipart/file\nAccept-Encoding:utf-8";
         var fileLength = fileParam["fileLength"];
         var fileBlockSize = fileParam["fileBlockSize"];
         var fileName = fileParam["fileName"];
         var paramBoundary = fileParam["paramBoundary"];
         if(!fileLength) return null;
         if(!fileBlockSize) return null;
         if(!fileName) return null;
         str += "\nFile-Length:"+fileLength+"\nFile-Block-Size:"+fileBlockSize+"\nFile-Name:"+fileName;
         if (!paramBoundary) {
             return str;
         }
         str += "\nParam-Boundary:" + paramBoundary;
         for (var key in extraParam) {
             if(extraParam.hasOwnProperty(key)){
                 str += "\n" + paramBoundary;
                 str += "\nname=" + key;
                 str += "\n" + extraParam[key]+"\n";
             }
         }
         str += paramBoundary;
         return str;
     }

    // 消息对象
    function Message(sender,senderName,sendTime,receiver,receiverName,messageContent,messageType){
        this.sender = sender;
        this.senderName = senderName;
        this.sendTime = sendTime;
        this.receiver = receiver;
        this.receiverName=receiverName;
        this.messageContent = messageContent;
        this.messageType = messageType;
    }