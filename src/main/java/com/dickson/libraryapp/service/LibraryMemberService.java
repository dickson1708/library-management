package com.dickson.libraryapp.service;

import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.dickson.libraryapp.domain.LibraryMember}.
 */
public interface LibraryMemberService {
    /**
     * Save a libraryMember.
     *
     * @param libraryMemberDTO the entity to save.
     * @return the persisted entity.
     */
    LibraryMemberDTO save(LibraryMemberDTO libraryMemberDTO);

    /**
     * Updates a libraryMember.
     *
     * @param libraryMemberDTO the entity to update.
     * @return the persisted entity.
     */
    LibraryMemberDTO update(LibraryMemberDTO libraryMemberDTO);

    /**
     * Partially updates a libraryMember.
     *
     * @param libraryMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LibraryMemberDTO> partialUpdate(LibraryMemberDTO libraryMemberDTO);

    /**
     * Get all the libraryMembers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LibraryMemberDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" libraryMember.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LibraryMemberDTO> findOne(Long id);

    /**
     * Delete the "id" libraryMember.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
