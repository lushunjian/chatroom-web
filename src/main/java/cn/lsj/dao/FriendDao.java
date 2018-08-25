package cn.lsj.dao;

import cn.lsj.domain.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/25 16:03
 * @Description:
 */
@Mapper
public interface FriendDao {

    List<Friend> getFriendByAccount(@Param("userAccount") String userAccount);

}
