package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ListService {
  private final ItemRepository itemRepository;

  @Autowired
  public ListService(@Qualifier("itemRepository") ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<Item> getItems (Trip trip, ItemType itemType, TripParticipant participant) {
    if (itemType.equals(ItemType.INDIVIDUALPACKING)) {
      return itemRepository.findAllByTripAndItemTypeAndParticipant(trip, itemType, participant);
    } else {
      return itemRepository.findAllByTripAndItemType(trip, itemType);
    }
  }


  public void updateItem(Long itemId, Item updatedItem) {
    Item existingItem = getItemById(itemId);
    if (existingItem.getParticipant() != null) {
      existingItem.setCompleted(updatedItem.isCompleted()); // first select --> then complete
    }
    existingItem.setItem(updatedItem.getItem());
    itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public void updateResponsible(Long itemId, TripParticipant participant) {
    Item existingItem = getItemById(itemId);
    if (existingItem.getParticipant() != null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Dieses item wurde bereits von %s ausgewählt.", existingItem.getParticipant().getUser().getUsername()));
    }
    existingItem.setParticipant(participant);
    existingItem.setUserId(participant.getUser().getId());
    itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public void deleteResponsible(Long itemId) {
    Item existingItem = getItemById(itemId);
    existingItem.setParticipant(null);
    existingItem.setUserId(null);
    existingItem.setCompleted(false);
    itemRepository.save(existingItem);
    itemRepository.flush();
  }

  public Item addItem(Trip trip, Item newItem, ItemType itemType, TripParticipant participant) {
    newItem.setTrip(trip);
    newItem.setCompleted(false);
    newItem.setItemType(itemType);
    if (itemType.equals(ItemType.INDIVIDUALPACKING)) {
      newItem.setParticipant(participant);
      newItem.setUserId(participant.getUser().getId());
    }
    newItem = itemRepository.save(newItem);
    itemRepository.flush();

    return newItem;
  }

  public void deleteItem(Long itemId) {
    itemRepository.deleteById(itemId);
    itemRepository.flush();
  }

  private Item getItemById(Long itemId) {
    return itemRepository.findById(itemId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Item nicht gefunden."));
  }

  public void checkIfItemIdHasType(Long itemId, ItemType itemType) {
    Item item = getItemById(itemId);
    if (item.getItemType() != itemType) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item nicht gefunden.");
    }
  }

  public void checkIfItemIdHasParticipant(Long itemId, TripParticipant participant) {
    Item item = getItemById(itemId);
    if (item.getParticipant() != participant) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Dieses item wurde bereits von %s ausgewählt.", item.getParticipant().getUser().getUsername()));
    }
  }

  public void checkIfItemIdHasTrip(Long itemId, Trip trip) {
    Item item = getItemById(itemId);
    if (item.getTrip() != trip) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Du darfst dieses Item nicht anpassen.");
    }
  }

  public void checkIfItemIdHasParticipantOrNone(Long itemId, TripParticipant participant) {
    Item item = getItemById(itemId);
    if (item.getParticipant() != null && item.getParticipant() != participant) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Dieses item wurde bereits von %s ausgewählt.", item.getParticipant().getUser().getUsername()));
    }
  }

  public void checkIfItemIdHasNoParticipant(Long itemId) {
    Item item = getItemById(itemId);
    if (item.getParticipant() != null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Dieses item wurde bereits von %s ausgewählt.", item.getParticipant().getUser().getUsername()));
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

  public void transferList(Trip trip, TripParticipant participant, List<TemplatePackingItem> items) {
    for (TemplatePackingItem item : items) {
      Item newItem = new Item();
      newItem.setItem(item.getItem());
      addItem(trip, newItem, ItemType.INDIVIDUALPACKING, participant);
    }
  }

}

