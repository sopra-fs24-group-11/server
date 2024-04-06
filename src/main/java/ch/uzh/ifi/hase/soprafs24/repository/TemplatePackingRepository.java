package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.TemplatePackingItem;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("templatePackingRepository")
public interface TemplatePackingRepository extends JpaRepository<TemplatePackingItem, Long> {
  TemplatePackingItem findByIdAndUser(Long id, User user);
  void deleteAllByUser(User user);
  List<TemplatePackingItem> findAllByUser(User user);
}
