package com.dickson.libraryapp.service;

import com.dickson.libraryapp.domain.*; // for static metamodels
import com.dickson.libraryapp.domain.Book;
import com.dickson.libraryapp.repository.BookRepository;
import com.dickson.libraryapp.service.criteria.BookCriteria;
import com.dickson.libraryapp.service.dto.BookDTO;
import com.dickson.libraryapp.service.mapper.BookMapper;
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
 * Service for executing complex queries for {@link Book} entities in the database.
 * The main input is a {@link BookCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookQueryService extends QueryService<Book> {

    private static final Logger LOG = LoggerFactory.getLogger(BookQueryService.class);

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookQueryService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Return a {@link Page} of {@link BookDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> findByCriteria(BookCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, page).map(bookMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.count(specification);
    }

    /**
     * Function to convert {@link BookCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Book_.category, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Book_.id),
                    buildStringSpecification(criteria.getIsbn(), Book_.isbn),
                    buildStringSpecification(criteria.getTitle(), Book_.title),
                    buildStringSpecification(criteria.getAuthor(), Book_.author),
                    buildStringSpecification(criteria.getPublisher(), Book_.publisher),
                    buildRangeSpecification(criteria.getPublicationDate(), Book_.publicationDate),
                    buildStringSpecification(criteria.getLanguage(), Book_.language),
                    buildRangeSpecification(criteria.getPages(), Book_.pages),
                    buildStringSpecification(criteria.getCoverImage(), Book_.coverImage),
                    buildSpecification(criteria.getAvailableForLoan(), Book_.availableForLoan),
                    buildSpecification(criteria.getCategoryId(), root -> root.join(Book_.category, JoinType.LEFT).get(Category_.id))
                )
            );
        }
        return specification;
    }
}
