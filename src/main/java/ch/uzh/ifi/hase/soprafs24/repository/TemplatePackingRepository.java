package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.TemplatePackingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("templatePackingRepository")
public interface TemplatePackingRepository extends JpaRepository<TemplatePackingItem, Long> {

}
