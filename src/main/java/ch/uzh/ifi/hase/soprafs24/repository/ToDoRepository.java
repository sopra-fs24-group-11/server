package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.ToDoItem;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("toDoRepository")
public interface ToDoRepository extends JpaRepository<ToDoItem, Long> {

  List<ToDoItem> findAllByTrip(Trip trip);
}
