package com.dickson.libraryapp.web.rest;

import static com.dickson.libraryapp.domain.BookCopyAsserts.*;
import static com.dickson.libraryapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dickson.libraryapp.IntegrationTest;
import com.dickson.libraryapp.domain.Book;
import com.dickson.libraryapp.domain.BookCopy;
import com.dickson.libraryapp.domain.enumeration.BookCopyStatus;
import com.dickson.libraryapp.repository.BookCopyRepository;
import com.dickson.libraryapp.service.BookCopyService;
import com.dickson.libraryapp.service.dto.BookCopyDTO;
import com.dickson.libraryapp.service.mapper.BookCopyMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookCopyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookCopyResourceIT {

    private static final String DEFAULT_BARCODE = "AAAAAAAAAA";
    private static final String UPDATED_BARCODE = "BBBBBBBBBB";

    private static final String DEFAULT_SHELF_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_SHELF_LOCATION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ACQUISITION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ACQUISITION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ACQUISITION_DATE = LocalDate.ofEpochDay(-1L);

    private static final BookCopyStatus DEFAULT_STATUS = BookCopyStatus.AVAILABLE;
    private static final BookCopyStatus UPDATED_STATUS = BookCopyStatus.LOANED;

    private static final String ENTITY_API_URL = "/api/book-copies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookCopyRepository bookCopyRepositoryMock;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Mock
    private BookCopyService bookCopyServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookCopyMockMvc;

    private BookCopy bookCopy;

    private BookCopy insertedBookCopy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createEntity() {
        return new BookCopy()
            .barcode(DEFAULT_BARCODE)
            .shelfLocation(DEFAULT_SHELF_LOCATION)
            .acquisitionDate(DEFAULT_ACQUISITION_DATE)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createUpdatedEntity() {
        return new BookCopy()
            .barcode(UPDATED_BARCODE)
            .shelfLocation(UPDATED_SHELF_LOCATION)
            .acquisitionDate(UPDATED_ACQUISITION_DATE)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        bookCopy = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBookCopy != null) {
            bookCopyRepository.delete(insertedBookCopy);
            insertedBookCopy = null;
        }
    }

    @Test
    @Transactional
    void createBookCopy() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);
        var returnedBookCopyDTO = om.readValue(
            restBookCopyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookCopyDTO.class
        );

        // Validate the BookCopy in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBookCopy = bookCopyMapper.toEntity(returnedBookCopyDTO);
        assertBookCopyUpdatableFieldsEquals(returnedBookCopy, getPersistedBookCopy(returnedBookCopy));

        insertedBookCopy = returnedBookCopy;
    }

    @Test
    @Transactional
    void createBookCopyWithExistingId() throws Exception {
        // Create the BookCopy with an existing ID
        bookCopy.setId(1L);
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBarcodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookCopy.setBarcode(null);

        // Create the BookCopy, which fails.
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkShelfLocationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookCopy.setShelfLocation(null);

        // Create the BookCopy, which fails.
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookCopy.setStatus(null);

        // Create the BookCopy, which fails.
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBookCopies() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].shelfLocation").value(hasItem(DEFAULT_SHELF_LOCATION)))
            .andExpect(jsonPath("$.[*].acquisitionDate").value(hasItem(DEFAULT_ACQUISITION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookCopiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookCopyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookCopyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookCopyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookCopiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookCopyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookCopyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookCopyRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBookCopy() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get the bookCopy
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL_ID, bookCopy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookCopy.getId().intValue()))
            .andExpect(jsonPath("$.barcode").value(DEFAULT_BARCODE))
            .andExpect(jsonPath("$.shelfLocation").value(DEFAULT_SHELF_LOCATION))
            .andExpect(jsonPath("$.acquisitionDate").value(DEFAULT_ACQUISITION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getBookCopiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        Long id = bookCopy.getId();

        defaultBookCopyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookCopyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookCopyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookCopiesByBarcodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where barcode equals to
        defaultBookCopyFiltering("barcode.equals=" + DEFAULT_BARCODE, "barcode.equals=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllBookCopiesByBarcodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where barcode in
        defaultBookCopyFiltering("barcode.in=" + DEFAULT_BARCODE + "," + UPDATED_BARCODE, "barcode.in=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllBookCopiesByBarcodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where barcode is not null
        defaultBookCopyFiltering("barcode.specified=true", "barcode.specified=false");
    }

    @Test
    @Transactional
    void getAllBookCopiesByBarcodeContainsSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where barcode contains
        defaultBookCopyFiltering("barcode.contains=" + DEFAULT_BARCODE, "barcode.contains=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllBookCopiesByBarcodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where barcode does not contain
        defaultBookCopyFiltering("barcode.doesNotContain=" + UPDATED_BARCODE, "barcode.doesNotContain=" + DEFAULT_BARCODE);
    }

    @Test
    @Transactional
    void getAllBookCopiesByShelfLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where shelfLocation equals to
        defaultBookCopyFiltering("shelfLocation.equals=" + DEFAULT_SHELF_LOCATION, "shelfLocation.equals=" + UPDATED_SHELF_LOCATION);
    }

    @Test
    @Transactional
    void getAllBookCopiesByShelfLocationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where shelfLocation in
        defaultBookCopyFiltering(
            "shelfLocation.in=" + DEFAULT_SHELF_LOCATION + "," + UPDATED_SHELF_LOCATION,
            "shelfLocation.in=" + UPDATED_SHELF_LOCATION
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByShelfLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where shelfLocation is not null
        defaultBookCopyFiltering("shelfLocation.specified=true", "shelfLocation.specified=false");
    }

    @Test
    @Transactional
    void getAllBookCopiesByShelfLocationContainsSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where shelfLocation contains
        defaultBookCopyFiltering("shelfLocation.contains=" + DEFAULT_SHELF_LOCATION, "shelfLocation.contains=" + UPDATED_SHELF_LOCATION);
    }

    @Test
    @Transactional
    void getAllBookCopiesByShelfLocationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where shelfLocation does not contain
        defaultBookCopyFiltering(
            "shelfLocation.doesNotContain=" + UPDATED_SHELF_LOCATION,
            "shelfLocation.doesNotContain=" + DEFAULT_SHELF_LOCATION
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByAcquisitionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where acquisitionDate equals to
        defaultBookCopyFiltering(
            "acquisitionDate.equals=" + DEFAULT_ACQUISITION_DATE,
            "acquisitionDate.equals=" + UPDATED_ACQUISITION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByAcquisitionDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where acquisitionDate in
        defaultBookCopyFiltering(
            "acquisitionDate.in=" + DEFAULT_ACQUISITION_DATE + "," + UPDATED_ACQUISITION_DATE,
            "acquisitionDate.in=" + UPDATED_ACQUISITION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByAcquisitionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where acquisitionDate is not null
        defaultBookCopyFiltering("acquisitionDate.specified=true", "acquisitionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBookCopiesByAcquisitionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where acquisitionDate is greater than or equal to
        defaultBookCopyFiltering(
            "acquisitionDate.greaterThanOrEqual=" + DEFAULT_ACQUISITION_DATE,
            "acquisitionDate.greaterThanOrEqual=" + UPDATED_ACQUISITION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByAcquisitionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where acquisitionDate is less than or equal to
        defaultBookCopyFiltering(
            "acquisitionDate.lessThanOrEqual=" + DEFAULT_ACQUISITION_DATE,
            "acquisitionDate.lessThanOrEqual=" + SMALLER_ACQUISITION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByAcquisitionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where acquisitionDate is less than
        defaultBookCopyFiltering(
            "acquisitionDate.lessThan=" + UPDATED_ACQUISITION_DATE,
            "acquisitionDate.lessThan=" + DEFAULT_ACQUISITION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByAcquisitionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where acquisitionDate is greater than
        defaultBookCopyFiltering(
            "acquisitionDate.greaterThan=" + SMALLER_ACQUISITION_DATE,
            "acquisitionDate.greaterThan=" + DEFAULT_ACQUISITION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where status equals to
        defaultBookCopyFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllBookCopiesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where status in
        defaultBookCopyFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllBookCopiesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where status is not null
        defaultBookCopyFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllBookCopiesByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            bookCopyRepository.saveAndFlush(bookCopy);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        bookCopy.setBook(book);
        bookCopyRepository.saveAndFlush(bookCopy);
        Long bookId = book.getId();
        // Get all the bookCopyList where book equals to bookId
        defaultBookCopyShouldBeFound("bookId.equals=" + bookId);

        // Get all the bookCopyList where book equals to (bookId + 1)
        defaultBookCopyShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultBookCopyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookCopyShouldBeFound(shouldBeFound);
        defaultBookCopyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookCopyShouldBeFound(String filter) throws Exception {
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].shelfLocation").value(hasItem(DEFAULT_SHELF_LOCATION)))
            .andExpect(jsonPath("$.[*].acquisitionDate").value(hasItem(DEFAULT_ACQUISITION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookCopyShouldNotBeFound(String filter) throws Exception {
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookCopy() throws Exception {
        // Get the bookCopy
        restBookCopyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookCopy() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCopy
        BookCopy updatedBookCopy = bookCopyRepository.findById(bookCopy.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookCopy are not directly saved in db
        em.detach(updatedBookCopy);
        updatedBookCopy
            .barcode(UPDATED_BARCODE)
            .shelfLocation(UPDATED_SHELF_LOCATION)
            .acquisitionDate(UPDATED_ACQUISITION_DATE)
            .status(UPDATED_STATUS);
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(updatedBookCopy);

        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookCopyToMatchAllProperties(updatedBookCopy);
    }

    @Test
    @Transactional
    void putNonExistingBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        partialUpdatedBookCopy.acquisitionDate(UPDATED_ACQUISITION_DATE).status(UPDATED_STATUS);

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookCopyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBookCopy, bookCopy), getPersistedBookCopy(bookCopy));
    }

    @Test
    @Transactional
    void fullUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        partialUpdatedBookCopy
            .barcode(UPDATED_BARCODE)
            .shelfLocation(UPDATED_SHELF_LOCATION)
            .acquisitionDate(UPDATED_ACQUISITION_DATE)
            .status(UPDATED_STATUS);

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookCopyUpdatableFieldsEquals(partialUpdatedBookCopy, getPersistedBookCopy(partialUpdatedBookCopy));
    }

    @Test
    @Transactional
    void patchNonExistingBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookCopy() throws Exception {
        // Initialize the database
        insertedBookCopy = bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookCopy
        restBookCopyMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookCopy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookCopyRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BookCopy getPersistedBookCopy(BookCopy bookCopy) {
        return bookCopyRepository.findById(bookCopy.getId()).orElseThrow();
    }

    protected void assertPersistedBookCopyToMatchAllProperties(BookCopy expectedBookCopy) {
        assertBookCopyAllPropertiesEquals(expectedBookCopy, getPersistedBookCopy(expectedBookCopy));
    }

    protected void assertPersistedBookCopyToMatchUpdatableProperties(BookCopy expectedBookCopy) {
        assertBookCopyAllUpdatablePropertiesEquals(expectedBookCopy, getPersistedBookCopy(expectedBookCopy));
    }
}
