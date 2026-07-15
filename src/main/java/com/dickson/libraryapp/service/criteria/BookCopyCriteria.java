package com.dickson.libraryapp.service.criteria;

import com.dickson.libraryapp.domain.enumeration.BookCopyStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dickson.libraryapp.domain.BookCopy} entity. This class is used
 * in {@link com.dickson.libraryapp.web.rest.BookCopyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /book-copies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCopyCriteria implements Serializable, Criteria {

    /**
     * Class for filtering BookCopyStatus
     */
    public static class BookCopyStatusFilter extends Filter<BookCopyStatus> {

        public BookCopyStatusFilter() {}

        public BookCopyStatusFilter(BookCopyStatusFilter filter) {
            super(filter);
        }

        @Override
        public BookCopyStatusFilter copy() {
            return new BookCopyStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter barcode;

    private StringFilter shelfLocation;

    private LocalDateFilter acquisitionDate;

    private BookCopyStatusFilter status;

    private LongFilter bookId;

    private Boolean distinct;

    public BookCopyCriteria() {}

    public BookCopyCriteria(BookCopyCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.barcode = other.optionalBarcode().map(StringFilter::copy).orElse(null);
        this.shelfLocation = other.optionalShelfLocation().map(StringFilter::copy).orElse(null);
        this.acquisitionDate = other.optionalAcquisitionDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(BookCopyStatusFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookCopyCriteria copy() {
        return new BookCopyCriteria(this);
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

    public StringFilter getBarcode() {
        return barcode;
    }

    public Optional<StringFilter> optionalBarcode() {
        return Optional.ofNullable(barcode);
    }

    public StringFilter barcode() {
        if (barcode == null) {
            setBarcode(new StringFilter());
        }
        return barcode;
    }

    public void setBarcode(StringFilter barcode) {
        this.barcode = barcode;
    }

    public StringFilter getShelfLocation() {
        return shelfLocation;
    }

    public Optional<StringFilter> optionalShelfLocation() {
        return Optional.ofNullable(shelfLocation);
    }

    public StringFilter shelfLocation() {
        if (shelfLocation == null) {
            setShelfLocation(new StringFilter());
        }
        return shelfLocation;
    }

    public void setShelfLocation(StringFilter shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public LocalDateFilter getAcquisitionDate() {
        return acquisitionDate;
    }

    public Optional<LocalDateFilter> optionalAcquisitionDate() {
        return Optional.ofNullable(acquisitionDate);
    }

    public LocalDateFilter acquisitionDate() {
        if (acquisitionDate == null) {
            setAcquisitionDate(new LocalDateFilter());
        }
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDateFilter acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BookCopyStatusFilter getStatus() {
        return status;
    }

    public Optional<BookCopyStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public BookCopyStatusFilter status() {
        if (status == null) {
            setStatus(new BookCopyStatusFilter());
        }
        return status;
    }

    public void setStatus(BookCopyStatusFilter status) {
        this.status = status;
    }

    public LongFilter getBookId() {
        return bookId;
    }

    public Optional<LongFilter> optionalBookId() {
        return Optional.ofNullable(bookId);
    }

    public LongFilter bookId() {
        if (bookId == null) {
            setBookId(new LongFilter());
        }
        return bookId;
    }

    public void setBookId(LongFilter bookId) {
        this.bookId = bookId;
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
        final BookCopyCriteria that = (BookCopyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(barcode, that.barcode) &&
            Objects.equals(shelfLocation, that.shelfLocation) &&
            Objects.equals(acquisitionDate, that.acquisitionDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, barcode, shelfLocation, acquisitionDate, status, bookId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCopyCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBarcode().map(f -> "barcode=" + f + ", ").orElse("") +
            optionalShelfLocation().map(f -> "shelfLocation=" + f + ", ").orElse("") +
            optionalAcquisitionDate().map(f -> "acquisitionDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
