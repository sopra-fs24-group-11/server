package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.entity.*;
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

  @Autowired
  public ListService(@Qualifier("itemRepository") ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<ItemGetDTO> getItems (Trip trip, ItemType itemType, TripParticipant participant) {
    List<Item> items;
    if (itemType.equals(ItemType.INDIVIDUALPACKING)) {
      items = itemRepository.findAllByTripAndItemTypeAndParticipant(trip, itemType, participant);
    } else {
      items = itemRepository.findAllByTripAndItemType(trip, itemType);
    }
      List<ItemGetDTO> itemGetDTOS = new ArrayList<>();
      for (Item item : items) {
        ItemGetDTO itemGetDTO = DTOMapper.INSTANCE.convertEntityToToDoGetDTO(item);
        itemGetDTOS.add(itemGetDTO);
      }
      return itemGetDTOS;

  }


  public void updateItem(Long itemId, Item updatedItem) {
    Item existingItem = getItemById(itemId);
    if (existingItem.getParticipant() != null) {
      existingItem.setCompleted(updatedItem.isCompleted()); // first select --> then complete
    }
    existingItem.setItem(updatedItem.getItem());
    existingItem = itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public void updateResponsible(Long itemId, TripParticipant participant) {
    Item existingItem = getItemById(itemId);
    if (existingItem.getParticipant() != null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to select this item");
    }
    existingItem.setParticipant(participant);
    existingItem.setUserId(participant.getUser().getId());
    existingItem = itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public void deleteResponsible(Long itemId) {
    Item existingItem = getItemById(itemId);
    existingItem.setParticipant(null);
    existingItem.setUserId(null);
    existingItem.setCompleted(false);
    existingItem = itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public String addItem(Trip trip, Item newItem, ItemType itemType, TripParticipant participant) {
    newItem.setTrip(trip);
    newItem.setCompleted(false);
    newItem.setItemType(itemType);
    if (itemType.equals(ItemType.INDIVIDUALPACKING)) {
      newItem.setParticipant(participant);
      newItem.setUserId(participant.getUser().getId());
    }
    newItem = itemRepository.save(newItem);
    itemRepository.flush();

    return newItem.getItem();
  }

  public void deleteItem(Long itemId) {
    itemRepository.deleteById(itemId);
    itemRepository.flush();
  }

  private Item getItemById(Long itemId) {
    return itemRepository.findById(itemId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Item not found"));
  }

  public void checkIfItemIdHasType(Long itemId, ItemType itemType) {
    Item item = getItemById(itemId);
    if (item.getItemType() != itemType) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
    }
  }

  public void checkIfItemIdHasParticipant(Long itemId, TripParticipant participant) {
    Item item = getItemById(itemId);
    if (item.getParticipant() != participant) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to change this item");
    }
  }

  public void checkIfItemIdHasTrip(Long itemId, Trip trip) {
    Item item = getItemById(itemId);
    if (item.getTrip() != trip) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to change this item");
    }
  }

  public void checkIfItemIdHasParticipantOrNone(Long itemId, TripParticipant participant) {
    Item item = getItemById(itemId);
    if (item.getParticipant() != null && item.getParticipant() != participant) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to change this item");
    }
  }

  public void checkIfItemIdHasNoParticipant(Long itemId) {
    Item item = getItemById(itemId);
    if (item.getParticipant() != null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to change this item");
    }
  }

  public void revertAllForAParticipant(TripParticipant participant) {
    List<Item> items = itemRepository.findAllByParticipant(participant);
    for (Item item : items) {
      item.setCompleted(false);
      item.setUserId(null);
      item.setParticipant(null);
    }
    itemRepository.saveAll(items);
    itemRepository.flush();
  }
  public void deleteAllForAParticipant(TripParticipant participant) {
    itemRepository.deleteAllByParticipantAndItemType(participant, ItemType.INDIVIDUALPACKING);
    itemRepository.flush();
  }

  public void revertAllForAUser(Long userId) {
    List<Item> items = itemRepository.findAllByUserId(userId);
    for (Item item : items) {
      item.setCompleted(false);
      item.setUserId(null);
      item.setParticipant(null);
    }
    itemRepository.saveAll(items);
    itemRepository.flush();
  }

  public void deleteAllForAUser(Long userId) {
    itemRepository.deleteAllByUserIdAndItemType(userId, ItemType.INDIVIDUALPACKING);
    itemRepository.flush();
  }

  public void deleteAllForATrip(Trip trip) {
    itemRepository.deleteAllByTrip(trip);
    itemRepository.flush();
  }

  public void transferList(Trip trip, User user, TripParticipant participant, List<TemplatePackingItem> items) {
    for (TemplatePackingItem item : items) {
      Item newItem = new Item();
      newItem.setItem(item.getItem());
      addItem(trip, newItem, ItemType.INDIVIDUALPACKING, participant);
    }
  }

}

