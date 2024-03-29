package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import java.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USERTABLE")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private LocalDate birthday;

  @Column(nullable = false)
  private LocalDate creationDate;

  @Column(nullable = false)
  private Double level;

  @Column(nullable = false)
  private String email;

  /*@ManyToMany
  @JoinTable(
          name = "user_friends",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "friend_id")
  )
  private Set<User> friends = new HashSet<>();

  public Set<User> getFriends() {
    return friends;
  }

  public void setFriends(Set<User> friends) {
    this.friends = friends;
  }*/
  @Embedded
  @Column(nullable = false)
  private Image profileImage;

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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public Image getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(Image profileImage) {
    this.profileImage = profileImage;
  }
}
