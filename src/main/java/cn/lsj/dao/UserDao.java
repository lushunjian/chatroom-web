package cn.lsj.dao;

import cn.lsj.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    User getUserByUserNameAndPassWord(@Param("user_name") String userName, @Param("password") String passWord);
}
