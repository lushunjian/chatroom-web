package cn.lsj.service;

import cn.lsj.dao.UserDao;
import cn.lsj.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/25 08:35
 * @Description:
 */
@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public boolean userExist(String userAccount,String userPassword){
       int count = userDao.userExist(userAccount,userPassword);
       return count != 0;
    }

    public User getUserInfo(String userAccount,String userPassword){
        return userDao.getUserInfo(userAccount,userPassword);
    }

    public boolean addUser(User user) throws Exception{
        int count = userDao.addUser(user);
        return count != 0;
    }

}
