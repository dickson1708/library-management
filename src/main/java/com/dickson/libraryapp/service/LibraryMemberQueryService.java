package com.dickson.libraryapp.service;

import com.dickson.libraryapp.domain.*; // for static metamodels
import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.repository.LibraryMemberRepository;
import com.dickson.libraryapp.service.criteria.LibraryMemberCriteria;
import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import com.dickson.libraryapp.service.mapper.LibraryMemberMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LibraryMember} entities in the database.
 * The main input is a {@link LibraryMemberCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LibraryMemberDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LibraryMemberQueryService extends QueryService<LibraryMember> {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryMemberQueryService.class);

    private final LibraryMemberRepository libraryMemberRepository;

    private final LibraryMemberMapper libraryMemberMapper;

    public LibraryMemberQueryService(LibraryMemberRepository libraryMemberRepository, LibraryMemberMapper libraryMemberMapper) {
        this.libraryMemberRepository = libraryMemberRepository;
        this.libraryMemberMapper = libraryMemberMapper;
    }

    /**
     * Return a {@link Page} of {@link LibraryMemberDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryMemberDTO> findByCriteria(LibraryMemberCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LibraryMember> specification = createSpecification(criteria);
        return libraryMemberRepository.findAll(specification, page).map(libraryMemberMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LibraryMemberCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LibraryMember> specification = createSpecification(criteria);
        return libraryMemberRepository.count(specification);
    }

    /**
     * Function to convert {@link LibraryMemberCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LibraryMember> createSpecification(LibraryMemberCriteria criteria) {
        Specification<LibraryMember> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(LibraryMember_.internalUser, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), LibraryMember_.id),
                    buildRangeSpecification(criteria.getMembershipNumber(), LibraryMember_.membershipNumber),
                    buildStringSpecification(criteria.getPhoneNumber(), LibraryMember_.phoneNumber),
                    buildRangeSpecification(criteria.getBirthDate(), LibraryMember_.birthDate),
                    buildStringSpecification(criteria.getAddress(), LibraryMember_.address),
                    buildRangeSpecification(criteria.getRegistrationDate(), LibraryMember_.registrationDate),
                    buildSpecification(criteria.getStatus(), LibraryMember_.status),
                    buildSpecification(criteria.getInternalUserId(), root ->
                        root.join(LibraryMember_.internalUser, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
