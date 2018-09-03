var IWebSocket = function(json) {
    let options = {
        uri:"#", // Socket绑定的URI
        sockJsUri:"#",
        projectName: window.location.pathname.split("/")[1], // 项目名称。默认以“/”为分隔符切割URI后取第2个字符串
        host: window.location.host, // 项目IP和端口。默认取当前项目的主机IP和Port
        onOpen:function(event) {
            // 自定义WSC连接事件：服务端与前端连接成功后触发
            console.log(event)
        },
        onMessage:function(event) {
            // 自定义WSC消息接收事件：服务端向前端发送消息时触发
            console.log(event)
        },
        onError:function(event) {
            // 自定义WSC异常事件：WSC报错后触发
            console.log(event)
        },
        onClose:function(event) {
            // 自定义WSC关闭事件：WSC关闭后触发
            console.log(event)
        }
    };
    $.extend(true, options, json);

    let websocket;
    if ('WebSocket' in window) {
        websocket = new WebSocket(options.uri);
    } else if ('MozWebSocket' in window) {
        websocket = new MozWebSocket(options.uri);
    } else {
        // SockJS插件，在浏览器不支持websocket情况下，SockJS可用http请求模拟socket请求
        websocket = new SockJS("http://" + options.host + "/" +options.projectName + "/" + options.sockJsUri);
    }

    websocket.onopen = function(evnt) {

        options.onOpen(evnt);
    };
    websocket.onmessage = function(evnt) {

        options.onMessage(evnt);
    };
    websocket.onerror = function(evnt) {

        options.onError(evnt);
    };
    websocket.onclose = function(evnt) {

        options.onClose(evnt);
    };

    return websocket;
}