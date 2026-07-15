package com.dickson.libraryapp.service;

import com.dickson.libraryapp.service.dto.LoanDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.dickson.libraryapp.domain.Loan}.
 */
public interface LoanService {
    /**
     * Save a loan.
     *
     * @param loanDTO the entity to save.
     * @return the persisted entity.
     */
    LoanDTO save(LoanDTO loanDTO);

    /**
     * Updates a loan.
     *
     * @param loanDTO the entity to update.
     * @return the persisted entity.
     */
    LoanDTO update(LoanDTO loanDTO);

    /**
     * Partially updates a loan.
     *
     * @param loanDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LoanDTO> partialUpdate(LoanDTO loanDTO);

    /**
     * Get all the loans with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LoanDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" loan.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LoanDTO> findOne(Long id);

    /**
     * Delete the "id" loan.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
