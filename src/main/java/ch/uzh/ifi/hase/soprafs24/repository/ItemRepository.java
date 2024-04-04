package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Item;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("toDoRepository")
public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findAllByTrip(Trip trip);
  Item findByid(Long id);
}
