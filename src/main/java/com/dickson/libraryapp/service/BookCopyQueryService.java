package com.dickson.libraryapp.service;

import com.dickson.libraryapp.domain.*; // for static metamodels
import com.dickson.libraryapp.domain.BookCopy;
import com.dickson.libraryapp.repository.BookCopyRepository;
import com.dickson.libraryapp.service.criteria.BookCopyCriteria;
import com.dickson.libraryapp.service.dto.BookCopyDTO;
import com.dickson.libraryapp.service.mapper.BookCopyMapper;
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
 * Service for executing complex queries for {@link BookCopy} entities in the database.
 * The main input is a {@link BookCopyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookCopyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookCopyQueryService extends QueryService<BookCopy> {

    private static final Logger LOG = LoggerFactory.getLogger(BookCopyQueryService.class);

    private final BookCopyRepository bookCopyRepository;

    private final BookCopyMapper bookCopyMapper;

    public BookCopyQueryService(BookCopyRepository bookCopyRepository, BookCopyMapper bookCopyMapper) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookCopyMapper = bookCopyMapper;
    }

    /**
     * Return a {@link Page} of {@link BookCopyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookCopyDTO> findByCriteria(BookCopyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookCopy> specification = createSpecification(criteria);
        return bookCopyRepository.findAll(specification, page).map(bookCopyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookCopyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookCopy> specification = createSpecification(criteria);
        return bookCopyRepository.count(specification);
    }

    /**
     * Function to convert {@link BookCopyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookCopy> createSpecification(BookCopyCriteria criteria) {
        Specification<BookCopy> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(BookCopy_.book, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), BookCopy_.id),
                    buildStringSpecification(criteria.getBarcode(), BookCopy_.barcode),
                    buildStringSpecification(criteria.getShelfLocation(), BookCopy_.shelfLocation),
                    buildRangeSpecification(criteria.getAcquisitionDate(), BookCopy_.acquisitionDate),
                    buildSpecification(criteria.getStatus(), BookCopy_.status),
                    buildSpecification(criteria.getBookId(), root -> root.join(BookCopy_.book, JoinType.LEFT).get(Book_.id))
                )
            );
        }
        return specification;
    }
}
