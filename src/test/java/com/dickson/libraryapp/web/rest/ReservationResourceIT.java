package com.dickson.libraryapp.web.rest;

import static com.dickson.libraryapp.domain.ReservationAsserts.*;
import static com.dickson.libraryapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dickson.libraryapp.IntegrationTest;
import com.dickson.libraryapp.domain.Book;
import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.domain.Reservation;
import com.dickson.libraryapp.domain.enumeration.ReservationStatus;
import com.dickson.libraryapp.repository.ReservationRepository;
import com.dickson.libraryapp.service.ReservationService;
import com.dickson.libraryapp.service.dto.ReservationDTO;
import com.dickson.libraryapp.service.mapper.ReservationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ReservationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReservationResourceIT {

    private static final Instant DEFAULT_RESERVATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESERVATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_QUEUE_POSITION = 1;
    private static final Integer UPDATED_QUEUE_POSITION = 2;
    private static final Integer SMALLER_QUEUE_POSITION = 1 - 1;

    private static final ReservationStatus DEFAULT_STATUS = ReservationStatus.PENDING;
    private static final ReservationStatus UPDATED_STATUS = ReservationStatus.READY;

    private static final String ENTITY_API_URL = "/api/reservations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationRepository reservationRepositoryMock;

    @Autowired
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationService reservationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReservationMockMvc;

    private Reservation reservation;

    private Reservation insertedReservation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createEntity() {
        return new Reservation()
            .reservationDate(DEFAULT_RESERVATION_DATE)
            .expirationDate(DEFAULT_EXPIRATION_DATE)
            .queuePosition(DEFAULT_QUEUE_POSITION)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createUpdatedEntity() {
        return new Reservation()
            .reservationDate(UPDATED_RESERVATION_DATE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .queuePosition(UPDATED_QUEUE_POSITION)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        reservation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReservation != null) {
            reservationRepository.delete(insertedReservation);
            insertedReservation = null;
        }
    }

    @Test
    @Transactional
    void createReservation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        var returnedReservationDTO = om.readValue(
            restReservationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReservationDTO.class
        );

        // Validate the Reservation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReservation = reservationMapper.toEntity(returnedReservationDTO);
        assertReservationUpdatableFieldsEquals(returnedReservation, getPersistedReservation(returnedReservation));

        insertedReservation = returnedReservation;
    }

    @Test
    @Transactional
    void createReservationWithExistingId() throws Exception {
        // Create the Reservation with an existing ID
        reservation.setId(1L);
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReservationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setReservationDate(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reservation.setStatus(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReservations() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].reservationDate").value(hasItem(DEFAULT_RESERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].queuePosition").value(hasItem(DEFAULT_QUEUE_POSITION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReservationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(reservationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReservationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reservationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReservationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reservationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReservationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reservationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReservation() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get the reservation
        restReservationMockMvc
            .perform(get(ENTITY_API_URL_ID, reservation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reservation.getId().intValue()))
            .andExpect(jsonPath("$.reservationDate").value(DEFAULT_RESERVATION_DATE.toString()))
            .andExpect(jsonPath("$.expirationDate").value(DEFAULT_EXPIRATION_DATE.toString()))
            .andExpect(jsonPath("$.queuePosition").value(DEFAULT_QUEUE_POSITION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getReservationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        Long id = reservation.getId();

        defaultReservationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReservationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReservationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate equals to
        defaultReservationFiltering(
            "reservationDate.equals=" + DEFAULT_RESERVATION_DATE,
            "reservationDate.equals=" + UPDATED_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate in
        defaultReservationFiltering(
            "reservationDate.in=" + DEFAULT_RESERVATION_DATE + "," + UPDATED_RESERVATION_DATE,
            "reservationDate.in=" + UPDATED_RESERVATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByReservationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where reservationDate is not null
        defaultReservationFiltering("reservationDate.specified=true", "reservationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByExpirationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where expirationDate equals to
        defaultReservationFiltering("expirationDate.equals=" + DEFAULT_EXPIRATION_DATE, "expirationDate.equals=" + UPDATED_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void getAllReservationsByExpirationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where expirationDate in
        defaultReservationFiltering(
            "expirationDate.in=" + DEFAULT_EXPIRATION_DATE + "," + UPDATED_EXPIRATION_DATE,
            "expirationDate.in=" + UPDATED_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReservationsByExpirationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where expirationDate is not null
        defaultReservationFiltering("expirationDate.specified=true", "expirationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByQueuePositionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where queuePosition equals to
        defaultReservationFiltering("queuePosition.equals=" + DEFAULT_QUEUE_POSITION, "queuePosition.equals=" + UPDATED_QUEUE_POSITION);
    }

    @Test
    @Transactional
    void getAllReservationsByQueuePositionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where queuePosition in
        defaultReservationFiltering(
            "queuePosition.in=" + DEFAULT_QUEUE_POSITION + "," + UPDATED_QUEUE_POSITION,
            "queuePosition.in=" + UPDATED_QUEUE_POSITION
        );
    }

    @Test
    @Transactional
    void getAllReservationsByQueuePositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where queuePosition is not null
        defaultReservationFiltering("queuePosition.specified=true", "queuePosition.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByQueuePositionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where queuePosition is greater than or equal to
        defaultReservationFiltering(
            "queuePosition.greaterThanOrEqual=" + DEFAULT_QUEUE_POSITION,
            "queuePosition.greaterThanOrEqual=" + UPDATED_QUEUE_POSITION
        );
    }

    @Test
    @Transactional
    void getAllReservationsByQueuePositionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where queuePosition is less than or equal to
        defaultReservationFiltering(
            "queuePosition.lessThanOrEqual=" + DEFAULT_QUEUE_POSITION,
            "queuePosition.lessThanOrEqual=" + SMALLER_QUEUE_POSITION
        );
    }

    @Test
    @Transactional
    void getAllReservationsByQueuePositionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where queuePosition is less than
        defaultReservationFiltering("queuePosition.lessThan=" + UPDATED_QUEUE_POSITION, "queuePosition.lessThan=" + DEFAULT_QUEUE_POSITION);
    }

    @Test
    @Transactional
    void getAllReservationsByQueuePositionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where queuePosition is greater than
        defaultReservationFiltering(
            "queuePosition.greaterThan=" + SMALLER_QUEUE_POSITION,
            "queuePosition.greaterThan=" + DEFAULT_QUEUE_POSITION
        );
    }

    @Test
    @Transactional
    void getAllReservationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status equals to
        defaultReservationFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReservationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status in
        defaultReservationFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReservationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status is not null
        defaultReservationFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllReservationsByMemberIsEqualToSomething() throws Exception {
        LibraryMember member;
        if (TestUtil.findAll(em, LibraryMember.class).isEmpty()) {
            reservationRepository.saveAndFlush(reservation);
            member = LibraryMemberResourceIT.createEntity(em);
        } else {
            member = TestUtil.findAll(em, LibraryMember.class).get(0);
        }
        em.persist(member);
        em.flush();
        reservation.setMember(member);
        reservationRepository.saveAndFlush(reservation);
        Long memberId = member.getId();
        // Get all the reservationList where member equals to memberId
        defaultReservationShouldBeFound("memberId.equals=" + memberId);

        // Get all the reservationList where member equals to (memberId + 1)
        defaultReservationShouldNotBeFound("memberId.equals=" + (memberId + 1));
    }

    @Test
    @Transactional
    void getAllReservationsByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            reservationRepository.saveAndFlush(reservation);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        reservation.setBook(book);
        reservationRepository.saveAndFlush(reservation);
        Long bookId = book.getId();
        // Get all the reservationList where book equals to bookId
        defaultReservationShouldBeFound("bookId.equals=" + bookId);

        // Get all the reservationList where book equals to (bookId + 1)
        defaultReservationShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultReservationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReservationShouldBeFound(shouldBeFound);
        defaultReservationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReservationShouldBeFound(String filter) throws Exception {
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].reservationDate").value(hasItem(DEFAULT_RESERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].queuePosition").value(hasItem(DEFAULT_QUEUE_POSITION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReservationShouldNotBeFound(String filter) throws Exception {
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReservation() throws Exception {
        // Get the reservation
        restReservationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReservation() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservation
        Reservation updatedReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReservation are not directly saved in db
        em.detach(updatedReservation);
        updatedReservation
            .reservationDate(UPDATED_RESERVATION_DATE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .queuePosition(UPDATED_QUEUE_POSITION)
            .status(UPDATED_STATUS);
        ReservationDTO reservationDTO = reservationMapper.toDto(updatedReservation);

        restReservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reservationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReservationToMatchAllProperties(updatedReservation);
    }

    @Test
    @Transactional
    void putNonExistingReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reservationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReservationWithPatch() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservation using partial update
        Reservation partialUpdatedReservation = new Reservation();
        partialUpdatedReservation.setId(reservation.getId());

        partialUpdatedReservation.queuePosition(UPDATED_QUEUE_POSITION);

        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReservation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReservation))
            )
            .andExpect(status().isOk());

        // Validate the Reservation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReservationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReservation, reservation),
            getPersistedReservation(reservation)
        );
    }

    @Test
    @Transactional
    void fullUpdateReservationWithPatch() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reservation using partial update
        Reservation partialUpdatedReservation = new Reservation();
        partialUpdatedReservation.setId(reservation.getId());

        partialUpdatedReservation
            .reservationDate(UPDATED_RESERVATION_DATE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .queuePosition(UPDATED_QUEUE_POSITION)
            .status(UPDATED_STATUS);

        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReservation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReservation))
            )
            .andExpect(status().isOk());

        // Validate the Reservation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReservationUpdatableFieldsEquals(partialUpdatedReservation, getPersistedReservation(partialUpdatedReservation));
    }

    @Test
    @Transactional
    void patchNonExistingReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reservationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReservation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reservation.setId(longCount.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReservationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reservation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReservation() throws Exception {
        // Initialize the database
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the reservation
        restReservationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reservation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reservationRepository.count();
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

    protected Reservation getPersistedReservation(Reservation reservation) {
        return reservationRepository.findById(reservation.getId()).orElseThrow();
    }

    protected void assertPersistedReservationToMatchAllProperties(Reservation expectedReservation) {
        assertReservationAllPropertiesEquals(expectedReservation, getPersistedReservation(expectedReservation));
    }

    protected void assertPersistedReservationToMatchUpdatableProperties(Reservation expectedReservation) {
        assertReservationAllUpdatablePropertiesEquals(expectedReservation, getPersistedReservation(expectedReservation));
    }
}
