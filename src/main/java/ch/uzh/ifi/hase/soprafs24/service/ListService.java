package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.repository.GroupPackingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.IndividualPackingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ToDoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ListService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final ToDoRepository toDoRepository;
  private final GroupPackingRepository groupPackingRepository;
  private final IndividualPackingRepository individualPackingRepository;

  @Autowired
  public ListService(@Qualifier("toDoRepository") ToDoRepository toDoRepository,@Qualifier("groupPackingRepository") GroupPackingRepository groupPackingRepository,@Qualifier("individualPackingRepository") IndividualPackingRepository individualPackingRepository) {
    this.toDoRepository = toDoRepository;
    this.groupPackingRepository = groupPackingRepository;
    this.individualPackingRepository = individualPackingRepository;
  }




}
