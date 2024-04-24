package com.travel.moldova.service;

import com.travel.moldova.domain.Events;
import com.travel.moldova.domain.User;
import com.travel.moldova.domain.enumeration.Type;
import com.travel.moldova.repository.EventsRepository;
import com.travel.moldova.repository.UserRepository;
import com.travel.moldova.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Service Implementation for managing {@link Events}.
 */
@Service
@Transactional
public class EventsService {

    private final Logger log = LoggerFactory.getLogger(EventsService.class);

    private final EventsRepository eventsRepository;

    private final UserRepository userRepository;


    public EventsService(EventsRepository eventsRepository, UserRepository userRepository) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a events.
     *
     * @param events the entity to save.
     * @return the persisted entity.
     */
    public Events save(Events events) {
        log.debug("Request to save Events : {}", events);
        return eventsRepository.save(events);
    }

    /**
     * Update a events.
     *
     * @param events the entity to save.
     * @return the persisted entity.
     */
    public Events update(Events events) {
        log.debug("Request to update Events : {}", events);
        return eventsRepository.save(events);
    }

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Events> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventsRepository.findAll(pageable);
    }

    /**
     * Get one events by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Events> findOne(Long id) {
        log.debug("Request to get Events : {}", id);
        return eventsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Events> findAllByUser(Pageable pageable, Type type) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findOneByLogin(login).orElseThrow();

        Page<Events> events;

        if (type.equals(Type.Toate)) {
            events = eventsRepository.findAllByUsersIn(Set.of(user), pageable);
        } else {
            events = eventsRepository.findAllByUsersInAndType(Set.of(user), type, pageable);
        }

        events.forEach(event -> {
            event.setFavorite(user);
        });
        return events;
    }

    @Transactional(readOnly = true)
    public void setFavorite(Long id) {
        Events event = eventsRepository.findById(id).orElseThrow();

        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findOneByLogin(login).orElseThrow();

        event.getUsers().add(user);
        eventsRepository.save(event);
    }

    @Transactional(readOnly = true)
    public void removeFavorite(Long id) {
        Events event = eventsRepository.findById(id).orElseThrow();

        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findOneByLogin(login).orElseThrow();

        event.getUsers().remove(user);
        eventsRepository.save(event);
    }


    /**
     * Delete the events by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Events : {}", id);
        eventsRepository.deleteById(id);
    }
}
