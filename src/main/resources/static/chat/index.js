 $('.menu .item').tab();

 $('.ui.accordion').accordion();

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

    // 用户下线方法，另一客户端登录时，已经将当期客户保存在redis中的信息替换。因此跳转到登录界面即可
    function forceOffline(){
        location.href="/logout";
    }

    // 从cookie中获取用户的账号
    var userAccount = $.cookie("userAccount");

    // 打开WebSocket, 传递的参数url没有同源策略的限制。
    var socket = IWebSocket({
        uri:'ws://127.0.0.1:8889/webSocket?userAccount='+userAccount,
        // 可以自定义四大事件
        onOpen: function(event) {
            console.log('连接');
        },
        onClose: function(event){
           console.log('断开');
        },
        onMessage: function(event) {
            //获取后台数据
            var result=$.parseJSON(event.data);
            console.log(result);
            var isOffline=result.isOffline;
            //如果值为1，则提示用户在另一客户端登录，强制用户下线
            if(isOffline){
                $('#userOffline')
                  .modal('setting', 'closable', false)
                  .modal('show')
                ;
            }else{
                //正常消息，业务处理
            }
            console.log(event)
        },
        onError: function(event) {
            console.log('异常')
         }
    });


    var status=["正在连接","连接成功","正在关闭连接","连接已关闭"];

    //发送消息按钮，点击事件
    $("#sendMessageButton").on("click",function(){
        var messageArea = $("#messageArea");
        //获取用户输入的消息
        var messageContent = messageArea.val();
        // 接收者账号
        var receiver = "222";
        // 消息类型
        var messageType = "whisper";

        // 如果用户没有输入内容，不允许发送消息
        if(messageContent) {
            //添加状态判断，当为OPEN时，发送消息
            if (socket.readyState===1) {
                // 构建消息对象
                var message = new Message(userAccount,receiver,messageContent,messageType);
                // 发送消息
                socket.send(JSON.stringify(message));
            }else{
                alert(socket.readyState);
                //do something
            }
            //系统当前时间，格式化日期
            var time = new Date().Format("yyyy-MM-dd HH:mm:ss");
            var html = '<div class="comment"><a class="avatar"><img src="/static/semantic/themes/default/assets/images/elliot.jpg">'+
                       '</a><div class="content"><a class="author">Elliot Fu </a><div class="metadata"><span class="date">' + time +
                       '</span></div><div class="text">' + message + ' </div></div></div>'
            $("#chatContent").append(html);
            //清空输入框的内容
            messageArea.val("");
        }else{
            $("#messageNotice").show();
        }
    });

    // 消息对象
    function Message(sender,receiver,messageContent,messageType){
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
        this.messageType = messageType;
    }