 $('.menu .item').tab();

 $('.ui.accordion').accordion();

 // 遮罩层
 //$('.dimmer').dimmer('show');

 //悬浮样式
 $('.special.cards .image').dimmer({
     on: 'hover'
 });

 // 进度条测试
 $('#test').progress({
   percent: 22
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
                        // 文件传输消息
                        var time = new Date(Number(result.sendTime)).Format("yyyy-MM-dd HH:mm:ss");
                        console.log(result);
                        if(result.messageType == "file"){
                            // 处理一下文件名，防止文件名过长，样式出现问题
                            var fileNames = result.fileName.split(".");
                            var htmlFileName = fileNames[0].substring(0,12)+"...  （"+fileNames[1]+"）";
                            // 文件大小保留两位小数，单位 M
                            var fileSize = (result.fileSize/(1024*1024)).toFixed(2);
                            var html ='<div class="comment"><a class="avatar"><img src="/static/semantic/themes/default/assets/images/matt.jpg"></a>'+
                                     '<div class="content"><a class="author">'+result.senderName+' </a><div class="metadata"><span class="date">'+time+'</span></div><div class="text">'+
                                     '<div class="ui segment" style="width:270px;height:80px;cursor:pointer" onclick="downLoadFile(\''+result.downloadPath+'\')" >'+
                                     '<a class="ui orange right ribbon label"><i class="block layout icon"></i></a>'+
                                     '<div class="ui form" style="margin-top: -25px"><div class="inline field" style="margin-bottom: 5px"><label>名称:</label>'+
                                      '<label>'+htmlFileName+'</label></div><div class="inline field"><label>大小:</label><label>'+fileSize+'M</label></div></div>'+
                                      '</div></div></div></div>';
                            $("#chatContent").append(html);
                        }else if(result.messageType == "text"){  // 文本消息
                            var html = '<div class="comment"><a class="avatar"><img src="/static/semantic/themes/default/assets/images/elliot.jpg">'+
                                       '</a><div class="content"><a class="author">'+result.senderName+' </a><div class="metadata"><span class="date">' + time +
                                       '</span></div><div class="text">' + result.messageContent + ' </div></div></div>';
                            $("#chatContent").append(html);
                        }else if(result.messageType == "video"){  // 视频信令消息
                            console.log('onmessage: ', resultData);
                            //如果是一个ICE的候选，则将其加入到PeerConnection中，否则设定对方的session描述为传递过来的描述
                            if( resultData.event === "_ice_candidate" ){
                                pc.addIceCandidate(new RTCIceCandidate(json.data.candidate));
                            } else {
                                pc.setRemoteDescription(new RTCSessionDescription(json.data.sdp));
                                // 如果是一个offer，那么需要回复一个answer
                                if(json.event === "_offer") {
                                    pc.createAnswer(sendAnswerFn, function (error) {
                                        console.log('Failure callback: ' + error);
                                    });
                                }
                            }
                        }
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

    //文件下载请求
    function downLoadFile(filePath){
        window.location.href="/socket/file/download?filePath="+filePath;
    }

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
        var messageType = "text";

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
    var block = 1024*1024*10;   //每次传 10M
    // 文件总大小 字节
    var totalSize = 0;
    // 文件上传进度条
    var percent=0;
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
            // 文件名 不可为空
            var fileName = file.name;
            // 文件唯一标识
            var fileUuid = uuid();
            // 文件上传样式
            var time = new Date().Format("yyyy-MM-dd HH:mm:ss");
            // 文件大小保留两位小数，单位 M
            var fileSize = (totalSize/(1024*1024)).toFixed(2);
            // 处理一下文件名，防止文件名过长，样式出现问题
            var fileNames = fileName.split(".");
            var htmlFileName = fileNames[0].substring(0,12)+"...  （"+fileNames[1]+"）";

            $.ajax({
                   type: "POST",
                   url: "/socket/file/message",
                   data: {"fileLength":totalSize,
                          "fileBlockSize":blockSize,
                          "fileName":fileName,
                          "fileUuid":fileUuid,
                          "senderAccount":$("#userAccount").val(),
                          "senderName":$("#userName").val(),
                          "receiverAccount":$("#receiverAccount").val(),
                          "sendTime":new Date().getTime()

                   },
                   dataType:"json",
                   async: false,
                   success: function(ret){
                        if(ret.code===200){
                            var fileHtml = '<div class="comment"><a class="avatar"><img src="/static/semantic/themes/default/assets/images/matt.jpg"></a>'+
                                   '<div class="content"><a class="author">我 </a><div class="metadata"><span class="date">'+time+'</span></div><div class="text">'+
                                   '<div class="ui segment" style="width:270px;height:80px"><a class="ui orange right ribbon label"><i class="block layout icon"></i></a>'+
                                   '<div class="ui form" style="margin-top: -25px"><div class="inline field" style="margin-bottom: 5px"><label>名称:</label>'+
                                    '<label>'+htmlFileName+'</label></div><div class="inline field"><label>大小:</label><label>'+fileSize+'M</label></div></div>'+
                                    '<div class="ui bottom attached progress" id="'+fileUuid+'"><div class="bar"></div></div></div></div></div></div>';
                            $("#chatContent").append(fileHtml);
                            // 上传文件
                             sendBlock(startSize,endSize,file,fileUuid);

                        }else{
                            alert(22);
                            return false;
                        }
                   }
                });

         }
         // 完成后，清空input文件框中的内容
         document.getElementById('file').value="" ;
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
     function sendBlock(startSize,endSize,file,md5Name){
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
            // 都是异步方法
             // 处理load事件。该事件在读取操作完成时触发。
             reader.onload = function loaded(e) {
                 var ArrayBuffer = e.target.result;
                 console.log("发送文件第" + (j++) + "部分,起始:"+startSize+"---结束:"+endSize+"块大小--"+ArrayBuffer.byteLength);
                 socket.send(ArrayBuffer);
                 startSize = endSize;
                 endSize = startSize+block;

                 if(startSize<totalSize){
                    percent = Math.floor(startSize*100/totalSize);
                    // 进度展示
                    $("#"+md5Name).progress({percent: Number(percent)});
                 }else{
                    // 进度条 100
                    $("#"+md5Name).progress({percent: 100});
                    // 文件上传完成，进度条重置
                    percent = 0;
                    // 上传完成后，需把聊天信息保存到缓存中
                    // ...

                 }

                 // 递归调用，相当于同步阻塞，当文件较大时，会导致卡顿
                 sendBlock(startSize,endSize,file,md5Name);
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

    //生成uid方法
    function uuid() {
        var s = [];
        var hexDigits = "0123456789abcdef";
        for (var i = 0; i < 36; i++) {
            s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
        }
        s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
        s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
        s[8] = s[13] = s[18] = s[23] = "-";

        var uuid = s.join("");
        return uuid;
    }



    /**************************************************************
    *
    * WebRTC 部分，用于端到端的视频
    * webSocket做信令服务器
    * 目前支持谷歌，火狐高版本
    *
    ***************************************************************/

     // stun和turn服务器
    var iceServer = {
        "iceServers": [{
            "url": "stun:stun.l.google.com:19302"
        }, {
            "url": "turn:numb.viagenie.ca",
            "username": "webrtc@live.com",
            "credential": "muazkh"
        }]
     };

    // 创建PeerConnection实例 (参数为null则没有iceserver，即使没有stunserver和turnserver，仍可在局域网下通讯)
    // 兼容不同浏览器 PeerConnection
    var pc;
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        //判断是否Firefox浏览器
        if (userAgent.indexOf("Firefox") > -1) {
            pc = new mozRTCPeerConnection(iceServer);
        }
        //判断是否chorme浏览器
        else if (userAgent.indexOf("Chrome") > -1){
            pc = new webkitRTCPeerConnection(iceServer);
        }
        // 其他浏览器
        else{
            pc = new RTCPeerConnection(iceServer);
        }
    // 兼容不同浏览器 PeerConnection
    //var pc = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;

    // 发送offer和answer的函数，发送本地session描述
    var sendOfferFn = function(desc){
        pc.setLocalDescription(desc);
        socket.send(JSON.stringify({
            "event": "_offer",
            "data": {
                "sdp": desc
            }
        }));
    };

    var sendAnswerFn = function(desc){
        pc.setLocalDescription(desc);
        socket.send(JSON.stringify({
            "event": "_answer",
            "data": {
                "sdp": desc
            }
        }));
    };

     // 发送ICE候选到其他客户端
    pc.onicecandidate = function(event){
        if (event.candidate !== null) {
            socket.send(JSON.stringify({
                "event": "_ice_candidate",
                "data": {
                    "candidate": event.candidate
                }
            }));
        }
    };

    // 如果检测到媒体流连接到本地，将其绑定到一个video标签上输出
    pc.onaddstream = function(event){
        document.getElementById('remoteVideo').src = URL.createObjectURL(event.stream);
    };

    // 呼叫方初始化
    // Get a list of friends from a server
    // User selects a friend to start a peer connection with
    navigator.getUserMedia = navigator.getUserMedia ||
                             navigator.webkitGetUserMedia ||
                             navigator.mozGetUserMedia;

    var localStream;
    var offer=0;
    var videoObj = {"video": true,"audio": true};
    var error = function(error){
             //处理媒体流创建失败错误
             console.log('getUserMedia error: ' + error);
         };
    var success = function(stream) {
          localStream = stream;
          // 获得vido标签对象
          var video = document.getElementById('localVideo');
         //绑定本地媒体流到video标签用于输出
          video.src = window.URL.createObjectURL(stream);
          // play带有播放和暂停按钮的一段视频
          video.onloadedmetadata = function(e) {
             video.play();
          };

/*          pc.onaddstream({stream: stream});
          // Adding a local stream won't trigger the onaddstream callback
          pc.addStream(stream);*/
          //向PeerConnection中加入需要发送的流
          pc.addStream(stream);
          // 视频发起方，调用此函数。通过点击事件执行此方法
          if(offer){
              pc.createOffer(sendOfferFn,function(){
                  console.log('Failure callback: ' + error);
              });
          }
    }

    $("#video").on("click",function(){
    // 判断对方是否在线，如果在线则开启摄像机
        $.ajax({
           type: "GET",
           url: "/user/isOnline",
           data: {"receiverAccount":$("#receiverAccount").val()},
           dataType:"json",
           async: false,
           success: function(ret){
               if(ret.data.isOnline == "on"){
                   // 如果本地摄像头没有开启，则允许开启摄像机
                   if(!localStream){
                       // 本地摄像机开启
                       if (navigator.getUserMedia) {
                           navigator.getUserMedia(videoObj, success, error);
                       }else {
                           alert("getUserMedia not supported");
                           console.log("getUserMedia not supported");
                       }
                   }
               }else{
                    alert("对方不在线!");
               }
           }
        });
    });

    // 视频挂断
    $("#hangup").on("click",function(){
                   // 如果本地已经开启了摄像机
       if(localStream){
           localStream.getTracks().forEach(function (track) {
               track.stop();
           });
           localStream = null;
       }
    });