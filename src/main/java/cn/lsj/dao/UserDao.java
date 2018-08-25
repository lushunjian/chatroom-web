package cn.lsj.dao;

import cn.lsj.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    int userExist(@Param("userAccount") String userAccount, @Param("userPassword") String userPassword);

    User getUserInfo(@Param("userAccount") String userAccount, @Param("userPassword") String userPassword);

    int addUser(@Param("user")User user);
}
