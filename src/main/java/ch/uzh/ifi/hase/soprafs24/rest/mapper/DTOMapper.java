package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "birthday", target = "birthday")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "level", target = "level")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "token", target = "token")
  UserLoginGetDTO convertEntityToUserLoginGetDTO(User user);

  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  User convertUserLoginPostDTOtoEntity(UserLoginPostDTO userLoginPostDTO);

  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "birthday", target = "birthday")
  User convertUserPutDTOToEntity(UserPutDTO userPutDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "level", target = "level")
  @Mapping(target = "status", ignore = true)
  MatchingUserGetDTO convertEntityToMatchingUserGetDTO(User user);

  @Mapping(source = "friendId", target = "friendId")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "level", target = "level")
  @Mapping(source = "points", target = "points")
  FriendGetDTO convertEntityToFriendGetDTO(Friend friend);

  @Mapping(source = "tripName", target = "tripName")
  @Mapping(source = "tripDescription", target = "tripDescription")
  @Mapping(source = "meetUpTime", target = "meetUpTime")
  Trip convertTripPostDTOtoEntity(TripPostDTO tripPostDTO);
}
