package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
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
  @Mapping(target = "status", ignore = true)
  FriendGetDTO convertEntityToFriendGetDTO(Friend friend);

  @Mapping(source = "tripName", target = "tripName")
  @Mapping(source = "tripDescription", target = "tripDescription")
  @Mapping(source = "meetUpTime", target = "meetUpTime")
  @Mapping(source = "meetUpPlace", target = "meetUpPlace")
  Trip convertTripPostDTOtoEntity(TripPostDTO tripPostDTO);

  @Mapping(source = "tripName", target = "tripName")
  @Mapping(source = "tripDescription", target = "tripDescription")
  @Mapping(source = "meetUpTime", target = "meetUpTime")
  @Mapping(source = "meetUpPlace", target = "meetUpPlace")
  @Mapping(source = "rating", target = "rating")
  Trip convertTripPutDTOtoEntity(TripPutDTO tripPutDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  ParticipantGetDTO convertEntityToParticipantGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "tripName", target = "tripName")
  @Mapping(source = "tripDescription", target = "tripDescription")
  @Mapping(source = "numberOfParticipants", target = "numberOfParticipants")
  @Mapping(source = "meetUpPlace", target = "meetUpPlace")
  @Mapping(source = "meetUpTime", target = "meetUpTime")
  @Mapping(source = "completed", target = "completed")
  @Mapping(source = "rating", target = "rating")
  BasicTripInfoGetDTO convertEntityToBasicTripInfoGetDTO(Trip trip);

  @Mapping(source = "connectionType", target = "connectionType")
  @Mapping(source = "connectionName", target = "connectionName")
  @Mapping(source = "departureTime", target = "departureTime")
  @Mapping(source = "departurePoint", target = "departurePoint")
  @Mapping(source = "arrivalTime", target = "arrivalTime")
  @Mapping(source = "arrivalPoint", target = "arrivalPoint")
  ParticipantConnection convertConnectionDTOToEntity(ConnectionDTO connectionDTO);

  @Mapping(source = "connectionType", target = "connectionType")
  @Mapping(source = "connectionName", target = "connectionName")
  @Mapping(source = "departureTime", target = "departureTime")
  @Mapping(source = "departurePoint", target = "departurePoint")
  @Mapping(source = "arrivalTime", target = "arrivalTime")
  @Mapping(source = "arrivalPoint", target = "arrivalPoint")
  ConnectionDTO convertEntityToConnectionDTO(ParticipantConnection participantConnection);

  @Mapping(source = "item", target = "item")
  Item convertToDoPostDTOToEntity(ItemPostDTO itemPostDTO);

  @Mapping(source = "item", target = "item")
  ItemPostDTO convertEntityToToDoPostDTO(Item item);

  @Mapping(source = "item", target = "item")
  @Mapping(source = "completed", target = "completed")
  Item convertToDoPutDTOToEntity(ItemPutDTO itemPutDTO);

  @Mapping(source = "item", target = "item")
  @Mapping(source = "completed", target = "completed")
  ItemPutDTO convertEntityToToDoPutDTO(Item item);

  @Mapping(source = "item", target = "item")
  @Mapping(source = "completed", target = "completed")
  @Mapping(source = "id", target = "id")
  @Mapping(source = "userId", target = "userId")
  ItemGetDTO convertEntityToToDoGetDTO(Item item);

  @Mapping(source = "message", target = "message")
  @Mapping(source = "timeStamp", target = "timeStamp")
  NotificationGetDTO convertUserNotificationToNotificationGetDTO(UserNotification userNotification);

  @Mapping(source = "message", target = "message")
  @Mapping(source = "timeStamp", target = "timeStamp")
  NotificationGetDTO convertTripNotificationToNotificationGetDTO(TripNotification tripNotification);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "profileImage.profilePicture", target = "profilePicture")
  MemberGetDTO convertEntityToMemberGetDTO(User user);

  @Mapping(source = "item", target = "item")
  TemplatePackingItem convertTemplateDTOToEntity(TemplateDTO templateDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "item", target = "item")
  TemplateGetDTO convertEntityToTemplateGetDTO(TemplatePackingItem templatePackingItem);
}
