package com.dickson.libraryapp.web.rest;

import com.dickson.libraryapp.repository.LibraryMemberRepository;
import com.dickson.libraryapp.service.LibraryMemberQueryService;
import com.dickson.libraryapp.service.LibraryMemberService;
import com.dickson.libraryapp.service.criteria.LibraryMemberCriteria;
import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import com.dickson.libraryapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dickson.libraryapp.domain.LibraryMember}.
 */
@RestController
@RequestMapping("/api/library-members")
public class LibraryMemberResource {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryMemberResource.class);

    private static final String ENTITY_NAME = "libraryMember";

    @Value("${jhipster.clientApp.name:librarymanagement}")
    private String applicationName;

    private final LibraryMemberService libraryMemberService;

    private final LibraryMemberRepository libraryMemberRepository;

    private final LibraryMemberQueryService libraryMemberQueryService;

    public LibraryMemberResource(
        LibraryMemberService libraryMemberService,
        LibraryMemberRepository libraryMemberRepository,
        LibraryMemberQueryService libraryMemberQueryService
    ) {
        this.libraryMemberService = libraryMemberService;
        this.libraryMemberRepository = libraryMemberRepository;
        this.libraryMemberQueryService = libraryMemberQueryService;
    }

    /**
     * {@code POST  /library-members} : Create a new libraryMember.
     *
     * @param libraryMemberDTO the libraryMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new libraryMemberDTO, or with status {@code 400 (Bad Request)} if the libraryMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LibraryMemberDTO> createLibraryMember(@Valid @RequestBody LibraryMemberDTO libraryMemberDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LibraryMember : {}", libraryMemberDTO);
        if (libraryMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new libraryMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(libraryMemberDTO.getInternalUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        libraryMemberDTO = libraryMemberService.save(libraryMemberDTO);
        return ResponseEntity.created(new URI("/api/library-members/" + libraryMemberDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, libraryMemberDTO.getId().toString()))
            .body(libraryMemberDTO);
    }

    /**
     * {@code PUT  /library-members/:id} : Updates an existing libraryMember.
     *
     * @param id the id of the libraryMemberDTO to save.
     * @param libraryMemberDTO the libraryMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryMemberDTO,
     * or with status {@code 400 (Bad Request)} if the libraryMemberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the libraryMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LibraryMemberDTO> updateLibraryMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LibraryMemberDTO libraryMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LibraryMember : {}, {}", id, libraryMemberDTO);
        if (libraryMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        libraryMemberDTO = libraryMemberService.update(libraryMemberDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, libraryMemberDTO.getId().toString()))
            .body(libraryMemberDTO);
    }

    /**
     * {@code PATCH  /library-members/:id} : Partial updates given fields of an existing libraryMember, field will ignore if it is null
     *
     * @param id the id of the libraryMemberDTO to save.
     * @param libraryMemberDTO the libraryMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryMemberDTO,
     * or with status {@code 400 (Bad Request)} if the libraryMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the libraryMemberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the libraryMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LibraryMemberDTO> partialUpdateLibraryMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LibraryMemberDTO libraryMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LibraryMember partially : {}, {}", id, libraryMemberDTO);
        if (libraryMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LibraryMemberDTO> result = libraryMemberService.partialUpdate(libraryMemberDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, libraryMemberDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /library-members} : get all the Library Members.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Library Members in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LibraryMemberDTO>> getAllLibraryMembers(
        LibraryMemberCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get LibraryMembers by criteria: {}", criteria);

        Page<LibraryMemberDTO> page = libraryMemberQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /library-members/count} : count all the libraryMembers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLibraryMembers(LibraryMemberCriteria criteria) {
        LOG.debug("REST request to count LibraryMembers by criteria: {}", criteria);
        return ResponseEntity.ok().body(libraryMemberQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /library-members/:id} : get the "id" libraryMember.
     *
     * @param id the id of the libraryMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the libraryMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LibraryMemberDTO> getLibraryMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LibraryMember : {}", id);
        Optional<LibraryMemberDTO> libraryMemberDTO = libraryMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(libraryMemberDTO);
    }

    /**
     * {@code DELETE  /library-members/:id} : delete the "id" libraryMember.
     *
     * @param id the id of the libraryMemberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibraryMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LibraryMember : {}", id);
        libraryMemberService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
