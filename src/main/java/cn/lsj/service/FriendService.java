package cn.lsj.service;

import cn.lsj.dao.FriendDao;
import cn.lsj.domain.Friend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/25 16:22
 * @Description:
 */
@Service
public class FriendService {

    @Autowired
    FriendDao friendDao;

    public List<Friend> getFriendByAccount(String userAccount){
        return friendDao.getFriendByAccount(userAccount);
    }
}
