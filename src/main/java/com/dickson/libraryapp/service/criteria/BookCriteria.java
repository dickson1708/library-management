package com.dickson.libraryapp.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dickson.libraryapp.domain.Book} entity. This class is used
 * in {@link com.dickson.libraryapp.web.rest.BookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter isbn;

    private StringFilter title;

    private StringFilter author;

    private StringFilter publisher;

    private LocalDateFilter publicationDate;

    private StringFilter language;

    private IntegerFilter pages;

    private StringFilter coverImage;

    private BooleanFilter availableForLoan;

    private LongFilter categoryId;

    private Boolean distinct;

    public BookCriteria() {}

    public BookCriteria(BookCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.isbn = other.optionalIsbn().map(StringFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.author = other.optionalAuthor().map(StringFilter::copy).orElse(null);
        this.publisher = other.optionalPublisher().map(StringFilter::copy).orElse(null);
        this.publicationDate = other.optionalPublicationDate().map(LocalDateFilter::copy).orElse(null);
        this.language = other.optionalLanguage().map(StringFilter::copy).orElse(null);
        this.pages = other.optionalPages().map(IntegerFilter::copy).orElse(null);
        this.coverImage = other.optionalCoverImage().map(StringFilter::copy).orElse(null);
        this.availableForLoan = other.optionalAvailableForLoan().map(BooleanFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getIsbn() {
        return isbn;
    }

    public Optional<StringFilter> optionalIsbn() {
        return Optional.ofNullable(isbn);
    }

    public StringFilter isbn() {
        if (isbn == null) {
            setIsbn(new StringFilter());
        }
        return isbn;
    }

    public void setIsbn(StringFilter isbn) {
        this.isbn = isbn;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getAuthor() {
        return author;
    }

    public Optional<StringFilter> optionalAuthor() {
        return Optional.ofNullable(author);
    }

    public StringFilter author() {
        if (author == null) {
            setAuthor(new StringFilter());
        }
        return author;
    }

    public void setAuthor(StringFilter author) {
        this.author = author;
    }

    public StringFilter getPublisher() {
        return publisher;
    }

    public Optional<StringFilter> optionalPublisher() {
        return Optional.ofNullable(publisher);
    }

    public StringFilter publisher() {
        if (publisher == null) {
            setPublisher(new StringFilter());
        }
        return publisher;
    }

    public void setPublisher(StringFilter publisher) {
        this.publisher = publisher;
    }

    public LocalDateFilter getPublicationDate() {
        return publicationDate;
    }

    public Optional<LocalDateFilter> optionalPublicationDate() {
        return Optional.ofNullable(publicationDate);
    }

    public LocalDateFilter publicationDate() {
        if (publicationDate == null) {
            setPublicationDate(new LocalDateFilter());
        }
        return publicationDate;
    }

    public void setPublicationDate(LocalDateFilter publicationDate) {
        this.publicationDate = publicationDate;
    }

    public StringFilter getLanguage() {
        return language;
    }

    public Optional<StringFilter> optionalLanguage() {
        return Optional.ofNullable(language);
    }

    public StringFilter language() {
        if (language == null) {
            setLanguage(new StringFilter());
        }
        return language;
    }

    public void setLanguage(StringFilter language) {
        this.language = language;
    }

    public IntegerFilter getPages() {
        return pages;
    }

    public Optional<IntegerFilter> optionalPages() {
        return Optional.ofNullable(pages);
    }

    public IntegerFilter pages() {
        if (pages == null) {
            setPages(new IntegerFilter());
        }
        return pages;
    }

    public void setPages(IntegerFilter pages) {
        this.pages = pages;
    }

    public StringFilter getCoverImage() {
        return coverImage;
    }

    public Optional<StringFilter> optionalCoverImage() {
        return Optional.ofNullable(coverImage);
    }

    public StringFilter coverImage() {
        if (coverImage == null) {
            setCoverImage(new StringFilter());
        }
        return coverImage;
    }

    public void setCoverImage(StringFilter coverImage) {
        this.coverImage = coverImage;
    }

    public BooleanFilter getAvailableForLoan() {
        return availableForLoan;
    }

    public Optional<BooleanFilter> optionalAvailableForLoan() {
        return Optional.ofNullable(availableForLoan);
    }

    public BooleanFilter availableForLoan() {
        if (availableForLoan == null) {
            setAvailableForLoan(new BooleanFilter());
        }
        return availableForLoan;
    }

    public void setAvailableForLoan(BooleanFilter availableForLoan) {
        this.availableForLoan = availableForLoan;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCriteria that = (BookCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isbn, that.isbn) &&
            Objects.equals(title, that.title) &&
            Objects.equals(author, that.author) &&
            Objects.equals(publisher, that.publisher) &&
            Objects.equals(publicationDate, that.publicationDate) &&
            Objects.equals(language, that.language) &&
            Objects.equals(pages, that.pages) &&
            Objects.equals(coverImage, that.coverImage) &&
            Objects.equals(availableForLoan, that.availableForLoan) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            isbn,
            title,
            author,
            publisher,
            publicationDate,
            language,
            pages,
            coverImage,
            availableForLoan,
            categoryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIsbn().map(f -> "isbn=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalAuthor().map(f -> "author=" + f + ", ").orElse("") +
            optionalPublisher().map(f -> "publisher=" + f + ", ").orElse("") +
            optionalPublicationDate().map(f -> "publicationDate=" + f + ", ").orElse("") +
            optionalLanguage().map(f -> "language=" + f + ", ").orElse("") +
            optionalPages().map(f -> "pages=" + f + ", ").orElse("") +
            optionalCoverImage().map(f -> "coverImage=" + f + ", ").orElse("") +
            optionalAvailableForLoan().map(f -> "availableForLoan=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
