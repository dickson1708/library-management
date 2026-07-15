package com.dickson.libraryapp.web.rest;

import static com.dickson.libraryapp.domain.LibraryMemberAsserts.*;
import static com.dickson.libraryapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dickson.libraryapp.IntegrationTest;
import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.domain.User;
import com.dickson.libraryapp.domain.enumeration.LibraryMemberStatus;
import com.dickson.libraryapp.repository.LibraryMemberRepository;
import com.dickson.libraryapp.repository.UserRepository;
import com.dickson.libraryapp.service.LibraryMemberService;
import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import com.dickson.libraryapp.service.mapper.LibraryMemberMapper;
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
 * Integration tests for the {@link LibraryMemberResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LibraryMemberResourceIT {

    private static final Long DEFAULT_MEMBERSHIP_NUMBER = 1L;
    private static final Long UPDATED_MEMBERSHIP_NUMBER = 2L;
    private static final Long SMALLER_MEMBERSHIP_NUMBER = 1L - 1L;

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTH_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_REGISTRATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REGISTRATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_REGISTRATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final LibraryMemberStatus DEFAULT_STATUS = LibraryMemberStatus.ACTIVE;
    private static final LibraryMemberStatus UPDATED_STATUS = LibraryMemberStatus.BLOCKED;

    private static final String ENTITY_API_URL = "/api/library-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LibraryMemberRepository libraryMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private LibraryMemberRepository libraryMemberRepositoryMock;

    @Autowired
    private LibraryMemberMapper libraryMemberMapper;

    @Mock
    private LibraryMemberService libraryMemberServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLibraryMemberMockMvc;

    private LibraryMember libraryMember;

    private LibraryMember insertedLibraryMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LibraryMember createEntity(EntityManager em) {
        LibraryMember libraryMember = new LibraryMember()
            .membershipNumber(DEFAULT_MEMBERSHIP_NUMBER)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .birthDate(DEFAULT_BIRTH_DATE)
            .address(DEFAULT_ADDRESS)
            .registrationDate(DEFAULT_REGISTRATION_DATE)
            .status(DEFAULT_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        libraryMember.setInternalUser(user);
        return libraryMember;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LibraryMember createUpdatedEntity(EntityManager em) {
        LibraryMember updatedLibraryMember = new LibraryMember()
            .membershipNumber(UPDATED_MEMBERSHIP_NUMBER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE)
            .address(UPDATED_ADDRESS)
            .registrationDate(UPDATED_REGISTRATION_DATE)
            .status(UPDATED_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedLibraryMember.setInternalUser(user);
        return updatedLibraryMember;
    }

    @BeforeEach
    void initTest() {
        libraryMember = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLibraryMember != null) {
            libraryMemberRepository.delete(insertedLibraryMember);
            insertedLibraryMember = null;
        }
    }

    @Test
    @Transactional
    void createLibraryMember() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LibraryMember
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);
        var returnedLibraryMemberDTO = om.readValue(
            restLibraryMemberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryMemberDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LibraryMemberDTO.class
        );

        // Validate the LibraryMember in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLibraryMember = libraryMemberMapper.toEntity(returnedLibraryMemberDTO);
        assertLibraryMemberUpdatableFieldsEquals(returnedLibraryMember, getPersistedLibraryMember(returnedLibraryMember));

        assertLibraryMemberMapsIdRelationshipPersistedValue(libraryMember, returnedLibraryMember);

        insertedLibraryMember = returnedLibraryMember;
    }

    @Test
    @Transactional
    void createLibraryMemberWithExistingId() throws Exception {
        // Create the LibraryMember with an existing ID
        libraryMember.setId(1L);
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibraryMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryMemberDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateLibraryMemberMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Add a new parent entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();

        // Load the libraryMember
        LibraryMember updatedLibraryMember = libraryMemberRepository.findById(libraryMember.getId()).orElseThrow();
        assertThat(updatedLibraryMember).isNotNull();
        // Disconnect from session so that the updates on updatedLibraryMember are not directly saved in db
        em.detach(updatedLibraryMember);

        // Update the User with new association value
        updatedLibraryMember.setInternalUser(user);
        LibraryMemberDTO updatedLibraryMemberDTO = libraryMemberMapper.toDto(updatedLibraryMember);
        assertThat(updatedLibraryMemberDTO).isNotNull();

        // Update the entity
        restLibraryMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLibraryMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedLibraryMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);

        /**
         * Validate the id for MapsId, the ids must be same
         * Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
         * Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
         * assertThat(testLibraryMember.getId()).isEqualTo(testLibraryMember.getInternalUser().getId());
         */
    }

    @Test
    @Transactional
    void checkMembershipNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        libraryMember.setMembershipNumber(null);

        // Create the LibraryMember, which fails.
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        restLibraryMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        libraryMember.setPhoneNumber(null);

        // Create the LibraryMember, which fails.
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        restLibraryMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRegistrationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        libraryMember.setRegistrationDate(null);

        // Create the LibraryMember, which fails.
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        restLibraryMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        libraryMember.setStatus(null);

        // Create the LibraryMember, which fails.
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        restLibraryMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLibraryMembers() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList
        restLibraryMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libraryMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].membershipNumber").value(hasItem(DEFAULT_MEMBERSHIP_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].registrationDate").value(hasItem(DEFAULT_REGISTRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLibraryMembersWithEagerRelationshipsIsEnabled() throws Exception {
        when(libraryMemberServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLibraryMemberMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(libraryMemberServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLibraryMembersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(libraryMemberServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLibraryMemberMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(libraryMemberRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLibraryMember() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get the libraryMember
        restLibraryMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, libraryMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(libraryMember.getId().intValue()))
            .andExpect(jsonPath("$.membershipNumber").value(DEFAULT_MEMBERSHIP_NUMBER.intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.registrationDate").value(DEFAULT_REGISTRATION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getLibraryMembersByIdFiltering() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        Long id = libraryMember.getId();

        defaultLibraryMemberFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLibraryMemberFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLibraryMemberFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByMembershipNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where membershipNumber equals to
        defaultLibraryMemberFiltering(
            "membershipNumber.equals=" + DEFAULT_MEMBERSHIP_NUMBER,
            "membershipNumber.equals=" + UPDATED_MEMBERSHIP_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByMembershipNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where membershipNumber in
        defaultLibraryMemberFiltering(
            "membershipNumber.in=" + DEFAULT_MEMBERSHIP_NUMBER + "," + UPDATED_MEMBERSHIP_NUMBER,
            "membershipNumber.in=" + UPDATED_MEMBERSHIP_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByMembershipNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where membershipNumber is not null
        defaultLibraryMemberFiltering("membershipNumber.specified=true", "membershipNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryMembersByMembershipNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where membershipNumber is greater than or equal to
        defaultLibraryMemberFiltering(
            "membershipNumber.greaterThanOrEqual=" + DEFAULT_MEMBERSHIP_NUMBER,
            "membershipNumber.greaterThanOrEqual=" + UPDATED_MEMBERSHIP_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByMembershipNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where membershipNumber is less than or equal to
        defaultLibraryMemberFiltering(
            "membershipNumber.lessThanOrEqual=" + DEFAULT_MEMBERSHIP_NUMBER,
            "membershipNumber.lessThanOrEqual=" + SMALLER_MEMBERSHIP_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByMembershipNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where membershipNumber is less than
        defaultLibraryMemberFiltering(
            "membershipNumber.lessThan=" + UPDATED_MEMBERSHIP_NUMBER,
            "membershipNumber.lessThan=" + DEFAULT_MEMBERSHIP_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByMembershipNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where membershipNumber is greater than
        defaultLibraryMemberFiltering(
            "membershipNumber.greaterThan=" + SMALLER_MEMBERSHIP_NUMBER,
            "membershipNumber.greaterThan=" + DEFAULT_MEMBERSHIP_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where phoneNumber equals to
        defaultLibraryMemberFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where phoneNumber in
        defaultLibraryMemberFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where phoneNumber is not null
        defaultLibraryMemberFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryMembersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where phoneNumber contains
        defaultLibraryMemberFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where phoneNumber does not contain
        defaultLibraryMemberFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByBirthDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where birthDate equals to
        defaultLibraryMemberFiltering("birthDate.equals=" + DEFAULT_BIRTH_DATE, "birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByBirthDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where birthDate in
        defaultLibraryMemberFiltering(
            "birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE,
            "birthDate.in=" + UPDATED_BIRTH_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByBirthDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where birthDate is not null
        defaultLibraryMemberFiltering("birthDate.specified=true", "birthDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryMembersByBirthDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where birthDate is greater than or equal to
        defaultLibraryMemberFiltering(
            "birthDate.greaterThanOrEqual=" + DEFAULT_BIRTH_DATE,
            "birthDate.greaterThanOrEqual=" + UPDATED_BIRTH_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByBirthDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where birthDate is less than or equal to
        defaultLibraryMemberFiltering("birthDate.lessThanOrEqual=" + DEFAULT_BIRTH_DATE, "birthDate.lessThanOrEqual=" + SMALLER_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByBirthDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where birthDate is less than
        defaultLibraryMemberFiltering("birthDate.lessThan=" + UPDATED_BIRTH_DATE, "birthDate.lessThan=" + DEFAULT_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByBirthDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where birthDate is greater than
        defaultLibraryMemberFiltering("birthDate.greaterThan=" + SMALLER_BIRTH_DATE, "birthDate.greaterThan=" + DEFAULT_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where address equals to
        defaultLibraryMemberFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where address in
        defaultLibraryMemberFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where address is not null
        defaultLibraryMemberFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryMembersByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where address contains
        defaultLibraryMemberFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where address does not contain
        defaultLibraryMemberFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByRegistrationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where registrationDate equals to
        defaultLibraryMemberFiltering(
            "registrationDate.equals=" + DEFAULT_REGISTRATION_DATE,
            "registrationDate.equals=" + UPDATED_REGISTRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByRegistrationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where registrationDate in
        defaultLibraryMemberFiltering(
            "registrationDate.in=" + DEFAULT_REGISTRATION_DATE + "," + UPDATED_REGISTRATION_DATE,
            "registrationDate.in=" + UPDATED_REGISTRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByRegistrationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where registrationDate is not null
        defaultLibraryMemberFiltering("registrationDate.specified=true", "registrationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryMembersByRegistrationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where registrationDate is greater than or equal to
        defaultLibraryMemberFiltering(
            "registrationDate.greaterThanOrEqual=" + DEFAULT_REGISTRATION_DATE,
            "registrationDate.greaterThanOrEqual=" + UPDATED_REGISTRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByRegistrationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where registrationDate is less than or equal to
        defaultLibraryMemberFiltering(
            "registrationDate.lessThanOrEqual=" + DEFAULT_REGISTRATION_DATE,
            "registrationDate.lessThanOrEqual=" + SMALLER_REGISTRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByRegistrationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where registrationDate is less than
        defaultLibraryMemberFiltering(
            "registrationDate.lessThan=" + UPDATED_REGISTRATION_DATE,
            "registrationDate.lessThan=" + DEFAULT_REGISTRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByRegistrationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where registrationDate is greater than
        defaultLibraryMemberFiltering(
            "registrationDate.greaterThan=" + SMALLER_REGISTRATION_DATE,
            "registrationDate.greaterThan=" + DEFAULT_REGISTRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllLibraryMembersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where status equals to
        defaultLibraryMemberFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where status in
        defaultLibraryMemberFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLibraryMembersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        // Get all the libraryMemberList where status is not null
        defaultLibraryMemberFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryMembersByInternalUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User internalUser = libraryMember.getInternalUser();
        libraryMemberRepository.saveAndFlush(libraryMember);
        Long internalUserId = internalUser.getId();
        // Get all the libraryMemberList where internalUser equals to internalUserId
        defaultLibraryMemberShouldBeFound("internalUserId.equals=" + internalUserId);

        // Get all the libraryMemberList where internalUser equals to (internalUserId + 1)
        defaultLibraryMemberShouldNotBeFound("internalUserId.equals=" + (internalUserId + 1));
    }

    private void defaultLibraryMemberFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLibraryMemberShouldBeFound(shouldBeFound);
        defaultLibraryMemberShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLibraryMemberShouldBeFound(String filter) throws Exception {
        restLibraryMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libraryMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].membershipNumber").value(hasItem(DEFAULT_MEMBERSHIP_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].registrationDate").value(hasItem(DEFAULT_REGISTRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restLibraryMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLibraryMemberShouldNotBeFound(String filter) throws Exception {
        restLibraryMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLibraryMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLibraryMember() throws Exception {
        // Get the libraryMember
        restLibraryMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLibraryMember() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the libraryMember
        LibraryMember updatedLibraryMember = libraryMemberRepository.findById(libraryMember.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLibraryMember are not directly saved in db
        em.detach(updatedLibraryMember);
        updatedLibraryMember
            .membershipNumber(UPDATED_MEMBERSHIP_NUMBER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE)
            .address(UPDATED_ADDRESS)
            .registrationDate(UPDATED_REGISTRATION_DATE)
            .status(UPDATED_STATUS);
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(updatedLibraryMember);

        restLibraryMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(libraryMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLibraryMemberToMatchAllProperties(updatedLibraryMember);
    }

    @Test
    @Transactional
    void putNonExistingLibraryMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        libraryMember.setId(longCount.incrementAndGet());

        // Create the LibraryMember
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(libraryMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLibraryMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        libraryMember.setId(longCount.incrementAndGet());

        // Create the LibraryMember
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(libraryMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLibraryMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        libraryMember.setId(longCount.incrementAndGet());

        // Create the LibraryMember
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMemberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLibraryMemberWithPatch() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the libraryMember using partial update
        LibraryMember partialUpdatedLibraryMember = new LibraryMember();
        partialUpdatedLibraryMember.setId(libraryMember.getId());

        partialUpdatedLibraryMember
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE)
            .address(UPDATED_ADDRESS)
            .registrationDate(UPDATED_REGISTRATION_DATE)
            .status(UPDATED_STATUS);

        restLibraryMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibraryMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLibraryMember))
            )
            .andExpect(status().isOk());

        // Validate the LibraryMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLibraryMemberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLibraryMember, libraryMember),
            getPersistedLibraryMember(libraryMember)
        );
    }

    @Test
    @Transactional
    void fullUpdateLibraryMemberWithPatch() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the libraryMember using partial update
        LibraryMember partialUpdatedLibraryMember = new LibraryMember();
        partialUpdatedLibraryMember.setId(libraryMember.getId());

        partialUpdatedLibraryMember
            .membershipNumber(UPDATED_MEMBERSHIP_NUMBER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE)
            .address(UPDATED_ADDRESS)
            .registrationDate(UPDATED_REGISTRATION_DATE)
            .status(UPDATED_STATUS);

        restLibraryMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibraryMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLibraryMember))
            )
            .andExpect(status().isOk());

        // Validate the LibraryMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLibraryMemberUpdatableFieldsEquals(partialUpdatedLibraryMember, getPersistedLibraryMember(partialUpdatedLibraryMember));
    }

    @Test
    @Transactional
    void patchNonExistingLibraryMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        libraryMember.setId(longCount.incrementAndGet());

        // Create the LibraryMember
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, libraryMemberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(libraryMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLibraryMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        libraryMember.setId(longCount.incrementAndGet());

        // Create the LibraryMember
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(libraryMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLibraryMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        libraryMember.setId(longCount.incrementAndGet());

        // Create the LibraryMember
        LibraryMemberDTO libraryMemberDTO = libraryMemberMapper.toDto(libraryMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMemberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(libraryMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LibraryMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLibraryMember() throws Exception {
        // Initialize the database
        insertedLibraryMember = libraryMemberRepository.saveAndFlush(libraryMember);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the libraryMember
        restLibraryMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, libraryMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return libraryMemberRepository.count();
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

    protected LibraryMember getPersistedLibraryMember(LibraryMember libraryMember) {
        return libraryMemberRepository.findById(libraryMember.getId()).orElseThrow();
    }

    protected void assertPersistedLibraryMemberToMatchAllProperties(LibraryMember expectedLibraryMember) {
        assertLibraryMemberAllPropertiesEquals(expectedLibraryMember, getPersistedLibraryMember(expectedLibraryMember));
    }

    protected void assertPersistedLibraryMemberToMatchUpdatableProperties(LibraryMember expectedLibraryMember) {
        assertLibraryMemberAllUpdatablePropertiesEquals(expectedLibraryMember, getPersistedLibraryMember(expectedLibraryMember));
    }
}
