package com.dickson.libraryapp.web.rest;

import com.dickson.libraryapp.repository.BookCopyRepository;
import com.dickson.libraryapp.service.BookCopyQueryService;
import com.dickson.libraryapp.service.BookCopyService;
import com.dickson.libraryapp.service.criteria.BookCopyCriteria;
import com.dickson.libraryapp.service.dto.BookCopyDTO;
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
 * REST controller for managing {@link com.dickson.libraryapp.domain.BookCopy}.
 */
@RestController
@RequestMapping("/api/book-copies")
public class BookCopyResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookCopyResource.class);

    private static final String ENTITY_NAME = "bookCopy";

    @Value("${jhipster.clientApp.name:librarymanagement}")
    private String applicationName;

    private final BookCopyService bookCopyService;

    private final BookCopyRepository bookCopyRepository;

    private final BookCopyQueryService bookCopyQueryService;

    public BookCopyResource(
        BookCopyService bookCopyService,
        BookCopyRepository bookCopyRepository,
        BookCopyQueryService bookCopyQueryService
    ) {
        this.bookCopyService = bookCopyService;
        this.bookCopyRepository = bookCopyRepository;
        this.bookCopyQueryService = bookCopyQueryService;
    }

    /**
     * {@code POST  /book-copies} : Create a new bookCopy.
     *
     * @param bookCopyDTO the bookCopyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookCopyDTO, or with status {@code 400 (Bad Request)} if the bookCopy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookCopyDTO> createBookCopy(@Valid @RequestBody BookCopyDTO bookCopyDTO) throws URISyntaxException {
        LOG.debug("REST request to save BookCopy : {}", bookCopyDTO);
        if (bookCopyDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookCopy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookCopyDTO = bookCopyService.save(bookCopyDTO);
        return ResponseEntity.created(new URI("/api/book-copies/" + bookCopyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookCopyDTO.getId().toString()))
            .body(bookCopyDTO);
    }

    /**
     * {@code PUT  /book-copies/:id} : Updates an existing bookCopy.
     *
     * @param id the id of the bookCopyDTO to save.
     * @param bookCopyDTO the bookCopyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCopyDTO,
     * or with status {@code 400 (Bad Request)} if the bookCopyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookCopyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookCopyDTO> updateBookCopy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookCopyDTO bookCopyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookCopy : {}, {}", id, bookCopyDTO);
        if (bookCopyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookCopyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCopyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookCopyDTO = bookCopyService.update(bookCopyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookCopyDTO.getId().toString()))
            .body(bookCopyDTO);
    }

    /**
     * {@code PATCH  /book-copies/:id} : Partial updates given fields of an existing bookCopy, field will ignore if it is null
     *
     * @param id the id of the bookCopyDTO to save.
     * @param bookCopyDTO the bookCopyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCopyDTO,
     * or with status {@code 400 (Bad Request)} if the bookCopyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookCopyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookCopyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookCopyDTO> partialUpdateBookCopy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookCopyDTO bookCopyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookCopy partially : {}, {}", id, bookCopyDTO);
        if (bookCopyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookCopyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCopyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookCopyDTO> result = bookCopyService.partialUpdate(bookCopyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookCopyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /book-copies} : get all the Book Copies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Book Copies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookCopyDTO>> getAllBookCopies(
        BookCopyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BookCopies by criteria: {}", criteria);

        Page<BookCopyDTO> page = bookCopyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-copies/count} : count all the bookCopies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookCopies(BookCopyCriteria criteria) {
        LOG.debug("REST request to count BookCopies by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookCopyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /book-copies/:id} : get the "id" bookCopy.
     *
     * @param id the id of the bookCopyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookCopyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookCopyDTO> getBookCopy(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookCopy : {}", id);
        Optional<BookCopyDTO> bookCopyDTO = bookCopyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookCopyDTO);
    }

    /**
     * {@code DELETE  /book-copies/:id} : delete the "id" bookCopy.
     *
     * @param id the id of the bookCopyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookCopy : {}", id);
        bookCopyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
