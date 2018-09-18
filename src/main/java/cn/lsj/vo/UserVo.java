package cn.lsj.vo;

public class UserVo {

    private String userAccount;
    private String isOnline;

    public UserVo(){}

    public UserVo(String userAccount, String isOnline) {
        this.userAccount = userAccount;
        this.isOnline = isOnline;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }
}
