package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.entity.Item;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("itemRepository")
public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findAllByTripAndItemType(Trip trip, ItemType itemType);
  List<Item> findAllByTripAndItemTypeAndParticipant(Trip trip, ItemType itemType, TripParticipant tripParticipant);
  List<Item> findByIdAndItemType(Long id, ItemType itemType);
  List<Item> findAllByUserId(Long userId);
  List<Item> findAllByParticipant(TripParticipant participant);
  void deleteAllByParticipantAndItemType(TripParticipant participant, ItemType itemType);
  void deleteAllByUserIdAndItemType(Long userId, ItemType itemType);
  void deleteAllByTrip(Trip trip);
}
