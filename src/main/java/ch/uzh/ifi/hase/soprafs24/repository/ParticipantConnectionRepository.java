package ch.uzh.ifi.hase.soprafs24.repository;


import ch.uzh.ifi.hase.soprafs24.entity.ParticipantConnection;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("participantConnectionRepository")
public interface ParticipantConnectionRepository extends JpaRepository<ParticipantConnection, Long> {
  List<ParticipantConnection> findAllByParticipant(TripParticipant participant);
  void deleteAllByParticipant(TripParticipant participant);
}
