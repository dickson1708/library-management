package com.dickson.libraryapp.service;

import com.dickson.libraryapp.service.dto.ReservationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.dickson.libraryapp.domain.Reservation}.
 */
public interface ReservationService {
    /**
     * Save a reservation.
     *
     * @param reservationDTO the entity to save.
     * @return the persisted entity.
     */
    ReservationDTO save(ReservationDTO reservationDTO);

    /**
     * Updates a reservation.
     *
     * @param reservationDTO the entity to update.
     * @return the persisted entity.
     */
    ReservationDTO update(ReservationDTO reservationDTO);

    /**
     * Partially updates a reservation.
     *
     * @param reservationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReservationDTO> partialUpdate(ReservationDTO reservationDTO);

    /**
     * Get all the reservations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReservationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" reservation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReservationDTO> findOne(Long id);

    /**
     * Delete the "id" reservation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
