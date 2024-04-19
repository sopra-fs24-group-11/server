package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.time.LocalDate;

public class UserGetDTO {
  private Long id;
  private String password;
  private String username;
  private UserStatus status;
  private LocalDate birthday;
  private LocalDate creationDate;
  private Double level;
  private String email;



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday (LocalDate birthday) {
    this.birthday = birthday;
  }
  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate (LocalDate creationDate) {
    this.creationDate = creationDate;
  }
  public Double getLevel () {
    return level;
  }

  public void setLevel (Double level) {
    this.level = level;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}
