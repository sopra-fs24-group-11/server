package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TripService {
  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final TripRepository tripRepository;

  @Autowired
  public TripService(@Qualifier("tripRepository") TripRepository tripRepository) {
    this.tripRepository = tripRepository;
  }
}
