package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.IndividualPackingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("individualPackingRepository")
public interface IndividualPackingRepository extends JpaRepository<IndividualPackingItem, Long> {

}
