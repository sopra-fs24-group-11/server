package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository("tripRepository")
public interface TripRepository extends JpaRepository<Trip, Long> {
  List<Trip> findByCompletedFalseAndMeetUpTimeBefore(LocalDateTime time);
}