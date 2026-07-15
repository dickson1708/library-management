package com.dickson.libraryapp.service;

import com.dickson.libraryapp.domain.*; // for static metamodels
import com.dickson.libraryapp.domain.Loan;
import com.dickson.libraryapp.repository.LoanRepository;
import com.dickson.libraryapp.service.criteria.LoanCriteria;
import com.dickson.libraryapp.service.dto.LoanDTO;
import com.dickson.libraryapp.service.mapper.LoanMapper;
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
 * Service for executing complex queries for {@link Loan} entities in the database.
 * The main input is a {@link LoanCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LoanDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LoanQueryService extends QueryService<Loan> {

    private static final Logger LOG = LoggerFactory.getLogger(LoanQueryService.class);

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    public LoanQueryService(LoanRepository loanRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
    }

    /**
     * Return a {@link Page} of {@link LoanDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LoanDTO> findByCriteria(LoanCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Loan> specification = createSpecification(criteria);
        return loanRepository.findAll(specification, page).map(loanMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LoanCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Loan> specification = createSpecification(criteria);
        return loanRepository.count(specification);
    }

    /**
     * Function to convert {@link LoanCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Loan> createSpecification(LoanCriteria criteria) {
        Specification<Loan> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Loan_.member, JoinType.LEFT);
                root.fetch(Loan_.bookCopy, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Loan_.id),
                    buildRangeSpecification(criteria.getLoanDate(), Loan_.loanDate),
                    buildRangeSpecification(criteria.getDueDate(), Loan_.dueDate),
                    buildRangeSpecification(criteria.getReturnDate(), Loan_.returnDate),
                    buildRangeSpecification(criteria.getRenewals(), Loan_.renewals),
                    buildStringSpecification(criteria.getNotes(), Loan_.notes),
                    buildSpecification(criteria.getStatus(), Loan_.status),
                    buildSpecification(criteria.getMemberId(), root -> root.join(Loan_.member, JoinType.LEFT).get(LibraryMember_.id)),
                    buildSpecification(criteria.getBookCopyId(), root -> root.join(Loan_.bookCopy, JoinType.LEFT).get(BookCopy_.id))
                )
            );
        }
        return specification;
    }
}
