package bean;


public class UserProfile {

  private long id;
  private long userUid;
  private String nickname;
  private String headimg;
  private java.sql.Date birthday;
  private String emailAddress;
  private java.sql.Timestamp lastLoginTime;
  private long imgId;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getUserUid() {
    return userUid;
  }

  public void setUserUid(long userUid) {
    this.userUid = userUid;
  }


  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }


  public String getHeadimg() {
    return headimg;
  }

  public void setHeadimg(String headimg) {
    this.headimg = headimg;
  }


  public java.sql.Date getBirthday() {
    return birthday;
  }

  public void setBirthday(java.sql.Date birthday) {
    this.birthday = birthday;
  }


  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }


  public java.sql.Timestamp getLastLoginTime() {
    return lastLoginTime;
  }

  public void setLastLoginTime(java.sql.Timestamp lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }


  public long getImgId() {
    return imgId;
  }

  public void setImgId(long imgId) {
    this.imgId = imgId;
  }

}
