package cn.lsj.controller;

import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.vo.HttpResponseBean;
import cn.lsj.vo.UserVo;
import io.netty.channel.Channel;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user",produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    @ResponseBody
    @GetMapping(value = "/isOnline")
    public HttpResponseBean isOnline(@RequestParam("receiverAccount") String userAccount){
        Channel channel = WebSocketConstant.concurrentMap.get(userAccount);
        UserVo userVo;
        if(channel != null)
            userVo = new UserVo(userAccount,"on");
        else
            userVo = new UserVo(userAccount,"off");
        return new HttpResponseBean<>(userVo,200);
    }
}
