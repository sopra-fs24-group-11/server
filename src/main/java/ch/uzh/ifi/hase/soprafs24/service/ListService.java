package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.entity.Item;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.repository.IndividualPackingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ItemRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ToDoGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ListService {

  private final Logger log = LoggerFactory.getLogger(ListService.class);

  private final ItemRepository itemRepository;
  private final IndividualPackingRepository individualPackingRepository;

  @Autowired
  public ListService(@Qualifier("toDoRepository") ItemRepository itemRepository, @Qualifier("individualPackingRepository") IndividualPackingRepository individualPackingRepository) {
    this.itemRepository = itemRepository;
    this.individualPackingRepository = individualPackingRepository;
  }

  public List<ToDoGetDTO> getTodos (Trip trip) {
    List<Item> items = itemRepository.findAllByTrip(trip);
    List<ToDoGetDTO> toDoGetDTOS = new ArrayList<>();
    for (Item item : items) {
      ToDoGetDTO toDoGetDTO = DTOMapper.INSTANCE.convertEntityToToDoGetDTO(item);
      toDoGetDTOS.add(toDoGetDTO);
    }
    return toDoGetDTOS;
  }

  public void updateTodo(Trip trip, Long itemId, Item updatedItem) {
    Item existingToDo = getItemById(itemId);
    existingToDo.setCompleted(updatedItem.isCompleted());
    existingToDo.setItem(updatedItem.getItem());
    existingToDo = itemRepository.save(existingToDo);
    itemRepository.flush();
  }

  public void updateResponsible(Long itemId, TripParticipant participant) {
    //TODO: add possibility to remove a responsibility
    Item existingToDo = getItemById(itemId);
    existingToDo.setParticipantId(participant.getId());
    existingToDo = itemRepository.save(existingToDo);
    itemRepository.flush();
  }

  public void deleteResponsible(Long itemId, TripParticipant participant) {
    Item existingToDo = getItemById(itemId);
    existingToDo.setParticipantId(null);
    existingToDo = itemRepository.save(existingToDo);
    itemRepository.flush();
  }

  public String addTodo(Trip trip, Item newItem) {
    newItem.setTrip(trip);
    newItem.setCompleted(false);
    newItem.setItemType(ItemType.TODO);
    newItem = itemRepository.save(newItem);
    itemRepository.flush();

    return newItem.getItem();
  }

  public void deleteTodo(Trip trip, Long itemId) {
    itemRepository.deleteById(itemId);
    itemRepository.flush();
  }

  public Item getItemById(Long itemId) {
    Item item = itemRepository.findByid(itemId);
    if (item == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
    }
    return item;
  }


}

