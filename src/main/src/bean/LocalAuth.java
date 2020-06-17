package bean;


public class LocalAuth {

  private long id;
  private long userUid;
  private String username;
  private String password;


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


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
