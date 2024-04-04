package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.entity.Item;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.repository.IndividualPackingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ItemRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ItemGetDTO;
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

  public List<ItemGetDTO> getItems (Trip trip, ItemType itemType) {
    List<Item> items = itemRepository.findAllByTripAndItemType(trip, itemType);
    List<ItemGetDTO> itemGetDTOS = new ArrayList<>();
    for (Item item : items) {
      ItemGetDTO itemGetDTO = DTOMapper.INSTANCE.convertEntityToToDoGetDTO(item);
      itemGetDTOS.add(itemGetDTO);
    }
    return itemGetDTOS;
  }

  public void updateItem(Trip trip, Long itemId, Item updatedItem) {
    Item existingItem = getItemById(itemId);
    existingItem.setCompleted(updatedItem.isCompleted());
    existingItem.setItem(updatedItem.getItem());
    existingItem = itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public void updateResponsible(Long itemId, TripParticipant participant) {
    //TODO: add possibility to remove a responsibility
    Item existingItem = getItemById(itemId);
    existingItem.setParticipantId(participant.getId());
    existingItem = itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public void deleteResponsible(Long itemId, TripParticipant participant) {
    Item existingItem = getItemById(itemId);
    existingItem.setParticipantId(null);
    existingItem = itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public String addItem(Trip trip, Item newItem, ItemType itemType) {
    newItem.setTrip(trip);
    newItem.setCompleted(false);
    newItem.setItemType(itemType);
    newItem = itemRepository.save(newItem);
    itemRepository.flush();

    return newItem.getItem();
  }

  public void deleteItem(Trip trip, Long itemId) {
    //TODO check if id exists
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

