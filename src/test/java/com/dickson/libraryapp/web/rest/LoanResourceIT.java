package com.dickson.libraryapp.web.rest;

import static com.dickson.libraryapp.domain.LoanAsserts.*;
import static com.dickson.libraryapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dickson.libraryapp.IntegrationTest;
import com.dickson.libraryapp.domain.BookCopy;
import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.domain.Loan;
import com.dickson.libraryapp.domain.enumeration.LoanStatus;
import com.dickson.libraryapp.repository.LoanRepository;
import com.dickson.libraryapp.service.LoanService;
import com.dickson.libraryapp.service.dto.LoanDTO;
import com.dickson.libraryapp.service.mapper.LoanMapper;
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
 * Integration tests for the {@link LoanResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LoanResourceIT {

    private static final Instant DEFAULT_LOAN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LOAN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RETURN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RETURN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_RENEWALS = 0;
    private static final Integer UPDATED_RENEWALS = 1;
    private static final Integer SMALLER_RENEWALS = 0 - 1;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final LoanStatus DEFAULT_STATUS = LoanStatus.ACTIVE;
    private static final LoanStatus UPDATED_STATUS = LoanStatus.RETURNED;

    private static final String ENTITY_API_URL = "/api/loans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LoanRepository loanRepository;

    @Mock
    private LoanRepository loanRepositoryMock;

    @Autowired
    private LoanMapper loanMapper;

    @Mock
    private LoanService loanServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLoanMockMvc;

    private Loan loan;

    private Loan insertedLoan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loan createEntity() {
        return new Loan()
            .loanDate(DEFAULT_LOAN_DATE)
            .dueDate(DEFAULT_DUE_DATE)
            .returnDate(DEFAULT_RETURN_DATE)
            .renewals(DEFAULT_RENEWALS)
            .notes(DEFAULT_NOTES)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loan createUpdatedEntity() {
        return new Loan()
            .loanDate(UPDATED_LOAN_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .renewals(UPDATED_RENEWALS)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        loan = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLoan != null) {
            loanRepository.delete(insertedLoan);
            insertedLoan = null;
        }
    }

    @Test
    @Transactional
    void createLoan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);
        var returnedLoanDTO = om.readValue(
            restLoanMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LoanDTO.class
        );

        // Validate the Loan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLoan = loanMapper.toEntity(returnedLoanDTO);
        assertLoanUpdatableFieldsEquals(returnedLoan, getPersistedLoan(returnedLoan));

        insertedLoan = returnedLoan;
    }

    @Test
    @Transactional
    void createLoanWithExistingId() throws Exception {
        // Create the Loan with an existing ID
        loan.setId(1L);
        LoanDTO loanDTO = loanMapper.toDto(loan);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLoanDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loan.setLoanDate(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);

        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loan.setDueDate(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);

        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loan.setStatus(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);

        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLoans() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList
        restLoanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loan.getId().intValue())))
            .andExpect(jsonPath("$.[*].loanDate").value(hasItem(DEFAULT_LOAN_DATE.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())))
            .andExpect(jsonPath("$.[*].renewals").value(hasItem(DEFAULT_RENEWALS)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLoansWithEagerRelationshipsIsEnabled() throws Exception {
        when(loanServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLoanMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(loanServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLoansWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(loanServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLoanMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(loanRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLoan() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get the loan
        restLoanMockMvc
            .perform(get(ENTITY_API_URL_ID, loan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(loan.getId().intValue()))
            .andExpect(jsonPath("$.loanDate").value(DEFAULT_LOAN_DATE.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()))
            .andExpect(jsonPath("$.renewals").value(DEFAULT_RENEWALS))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getLoansByIdFiltering() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        Long id = loan.getId();

        defaultLoanFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLoanFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLoanFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLoansByLoanDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where loanDate equals to
        defaultLoanFiltering("loanDate.equals=" + DEFAULT_LOAN_DATE, "loanDate.equals=" + UPDATED_LOAN_DATE);
    }

    @Test
    @Transactional
    void getAllLoansByLoanDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where loanDate in
        defaultLoanFiltering("loanDate.in=" + DEFAULT_LOAN_DATE + "," + UPDATED_LOAN_DATE, "loanDate.in=" + UPDATED_LOAN_DATE);
    }

    @Test
    @Transactional
    void getAllLoansByLoanDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where loanDate is not null
        defaultLoanFiltering("loanDate.specified=true", "loanDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLoansByDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where dueDate equals to
        defaultLoanFiltering("dueDate.equals=" + DEFAULT_DUE_DATE, "dueDate.equals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllLoansByDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where dueDate in
        defaultLoanFiltering("dueDate.in=" + DEFAULT_DUE_DATE + "," + UPDATED_DUE_DATE, "dueDate.in=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllLoansByDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where dueDate is not null
        defaultLoanFiltering("dueDate.specified=true", "dueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLoansByReturnDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where returnDate equals to
        defaultLoanFiltering("returnDate.equals=" + DEFAULT_RETURN_DATE, "returnDate.equals=" + UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    void getAllLoansByReturnDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where returnDate in
        defaultLoanFiltering("returnDate.in=" + DEFAULT_RETURN_DATE + "," + UPDATED_RETURN_DATE, "returnDate.in=" + UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    void getAllLoansByReturnDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where returnDate is not null
        defaultLoanFiltering("returnDate.specified=true", "returnDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLoansByRenewalsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where renewals equals to
        defaultLoanFiltering("renewals.equals=" + DEFAULT_RENEWALS, "renewals.equals=" + UPDATED_RENEWALS);
    }

    @Test
    @Transactional
    void getAllLoansByRenewalsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where renewals in
        defaultLoanFiltering("renewals.in=" + DEFAULT_RENEWALS + "," + UPDATED_RENEWALS, "renewals.in=" + UPDATED_RENEWALS);
    }

    @Test
    @Transactional
    void getAllLoansByRenewalsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where renewals is not null
        defaultLoanFiltering("renewals.specified=true", "renewals.specified=false");
    }

    @Test
    @Transactional
    void getAllLoansByRenewalsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where renewals is greater than or equal to
        defaultLoanFiltering("renewals.greaterThanOrEqual=" + DEFAULT_RENEWALS, "renewals.greaterThanOrEqual=" + UPDATED_RENEWALS);
    }

    @Test
    @Transactional
    void getAllLoansByRenewalsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where renewals is less than or equal to
        defaultLoanFiltering("renewals.lessThanOrEqual=" + DEFAULT_RENEWALS, "renewals.lessThanOrEqual=" + SMALLER_RENEWALS);
    }

    @Test
    @Transactional
    void getAllLoansByRenewalsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where renewals is less than
        defaultLoanFiltering("renewals.lessThan=" + UPDATED_RENEWALS, "renewals.lessThan=" + DEFAULT_RENEWALS);
    }

    @Test
    @Transactional
    void getAllLoansByRenewalsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where renewals is greater than
        defaultLoanFiltering("renewals.greaterThan=" + SMALLER_RENEWALS, "renewals.greaterThan=" + DEFAULT_RENEWALS);
    }

    @Test
    @Transactional
    void getAllLoansByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where notes equals to
        defaultLoanFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllLoansByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where notes in
        defaultLoanFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllLoansByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where notes is not null
        defaultLoanFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllLoansByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where notes contains
        defaultLoanFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllLoansByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where notes does not contain
        defaultLoanFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllLoansByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where status equals to
        defaultLoanFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLoansByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where status in
        defaultLoanFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLoansByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList where status is not null
        defaultLoanFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllLoansByMemberIsEqualToSomething() throws Exception {
        LibraryMember member;
        if (TestUtil.findAll(em, LibraryMember.class).isEmpty()) {
            loanRepository.saveAndFlush(loan);
            member = LibraryMemberResourceIT.createEntity(em);
        } else {
            member = TestUtil.findAll(em, LibraryMember.class).get(0);
        }
        em.persist(member);
        em.flush();
        loan.setMember(member);
        loanRepository.saveAndFlush(loan);
        Long memberId = member.getId();
        // Get all the loanList where member equals to memberId
        defaultLoanShouldBeFound("memberId.equals=" + memberId);

        // Get all the loanList where member equals to (memberId + 1)
        defaultLoanShouldNotBeFound("memberId.equals=" + (memberId + 1));
    }

    @Test
    @Transactional
    void getAllLoansByBookCopyIsEqualToSomething() throws Exception {
        BookCopy bookCopy;
        if (TestUtil.findAll(em, BookCopy.class).isEmpty()) {
            loanRepository.saveAndFlush(loan);
            bookCopy = BookCopyResourceIT.createEntity();
        } else {
            bookCopy = TestUtil.findAll(em, BookCopy.class).get(0);
        }
        em.persist(bookCopy);
        em.flush();
        loan.setBookCopy(bookCopy);
        loanRepository.saveAndFlush(loan);
        Long bookCopyId = bookCopy.getId();
        // Get all the loanList where bookCopy equals to bookCopyId
        defaultLoanShouldBeFound("bookCopyId.equals=" + bookCopyId);

        // Get all the loanList where bookCopy equals to (bookCopyId + 1)
        defaultLoanShouldNotBeFound("bookCopyId.equals=" + (bookCopyId + 1));
    }

    private void defaultLoanFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLoanShouldBeFound(shouldBeFound);
        defaultLoanShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLoanShouldBeFound(String filter) throws Exception {
        restLoanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loan.getId().intValue())))
            .andExpect(jsonPath("$.[*].loanDate").value(hasItem(DEFAULT_LOAN_DATE.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())))
            .andExpect(jsonPath("$.[*].renewals").value(hasItem(DEFAULT_RENEWALS)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restLoanMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLoanShouldNotBeFound(String filter) throws Exception {
        restLoanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLoanMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLoan() throws Exception {
        // Get the loan
        restLoanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLoan() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loan
        Loan updatedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLoan are not directly saved in db
        em.detach(updatedLoan);
        updatedLoan
            .loanDate(UPDATED_LOAN_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .renewals(UPDATED_RENEWALS)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS);
        LoanDTO loanDTO = loanMapper.toDto(updatedLoan);

        restLoanMockMvc
            .perform(put(ENTITY_API_URL_ID, loanDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isOk());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLoanToMatchAllProperties(updatedLoan);
    }

    @Test
    @Transactional
    void putNonExistingLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(put(ENTITY_API_URL_ID, loanDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(loanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLoanWithPatch() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loan using partial update
        Loan partialUpdatedLoan = new Loan();
        partialUpdatedLoan.setId(loan.getId());

        partialUpdatedLoan
            .dueDate(UPDATED_DUE_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .renewals(UPDATED_RENEWALS)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS);

        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLoan))
            )
            .andExpect(status().isOk());

        // Validate the Loan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoanUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLoan, loan), getPersistedLoan(loan));
    }

    @Test
    @Transactional
    void fullUpdateLoanWithPatch() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loan using partial update
        Loan partialUpdatedLoan = new Loan();
        partialUpdatedLoan.setId(loan.getId());

        partialUpdatedLoan
            .loanDate(UPDATED_LOAN_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .renewals(UPDATED_RENEWALS)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS);

        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLoan))
            )
            .andExpect(status().isOk());

        // Validate the Loan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoanUpdatableFieldsEquals(partialUpdatedLoan, getPersistedLoan(partialUpdatedLoan));
    }

    @Test
    @Transactional
    void patchNonExistingLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, loanDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(loanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(loanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLoan() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the loan
        restLoanMockMvc
            .perform(delete(ENTITY_API_URL_ID, loan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return loanRepository.count();
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

    protected Loan getPersistedLoan(Loan loan) {
        return loanRepository.findById(loan.getId()).orElseThrow();
    }

    protected void assertPersistedLoanToMatchAllProperties(Loan expectedLoan) {
        assertLoanAllPropertiesEquals(expectedLoan, getPersistedLoan(expectedLoan));
    }

    protected void assertPersistedLoanToMatchUpdatableProperties(Loan expectedLoan) {
        assertLoanAllUpdatablePropertiesEquals(expectedLoan, getPersistedLoan(expectedLoan));
    }
}
