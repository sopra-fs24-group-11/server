package ch.uzh.ifi.hase.soprafs24.repository;


import ch.uzh.ifi.hase.soprafs24.entity.ParticipantConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("participantConnectionRepository")
public interface ParticipantConnectionRepository extends JpaRepository<ParticipantConnection, Long> {
}
