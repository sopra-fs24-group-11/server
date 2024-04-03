package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.GroupPackingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("groupPackingRepository")
public interface GroupPackingRepository extends JpaRepository<GroupPackingItem, Long> {

}
