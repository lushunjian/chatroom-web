package cn.lsj.domain;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/25 16:05
 * @Description:
 */
public class Friend {

    private Integer id ;
    private String userAccount ;
    private String friendName ;
    private String friendAccount ;
    private String friendDescribe ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(String friendAccount) {
        this.friendAccount = friendAccount;
    }

    public String getFriendDescribe() {
        return friendDescribe;
    }

    public void setFriendDescribe(String friendDescribe) {
        this.friendDescribe = friendDescribe;
    }
}
