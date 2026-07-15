package com.dickson.libraryapp.service.criteria;

import com.dickson.libraryapp.domain.enumeration.ReservationStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dickson.libraryapp.domain.Reservation} entity. This class is used
 * in {@link com.dickson.libraryapp.web.rest.ReservationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reservations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ReservationStatus
     */
    public static class ReservationStatusFilter extends Filter<ReservationStatus> {

        public ReservationStatusFilter() {}

        public ReservationStatusFilter(ReservationStatusFilter filter) {
            super(filter);
        }

        @Override
        public ReservationStatusFilter copy() {
            return new ReservationStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter reservationDate;

    private InstantFilter expirationDate;

    private IntegerFilter queuePosition;

    private ReservationStatusFilter status;

    private LongFilter memberId;

    private LongFilter bookId;

    private Boolean distinct;

    public ReservationCriteria() {}

    public ReservationCriteria(ReservationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reservationDate = other.optionalReservationDate().map(InstantFilter::copy).orElse(null);
        this.expirationDate = other.optionalExpirationDate().map(InstantFilter::copy).orElse(null);
        this.queuePosition = other.optionalQueuePosition().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ReservationStatusFilter::copy).orElse(null);
        this.memberId = other.optionalMemberId().map(LongFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReservationCriteria copy() {
        return new ReservationCriteria(this);
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

    public InstantFilter getReservationDate() {
        return reservationDate;
    }

    public Optional<InstantFilter> optionalReservationDate() {
        return Optional.ofNullable(reservationDate);
    }

    public InstantFilter reservationDate() {
        if (reservationDate == null) {
            setReservationDate(new InstantFilter());
        }
        return reservationDate;
    }

    public void setReservationDate(InstantFilter reservationDate) {
        this.reservationDate = reservationDate;
    }

    public InstantFilter getExpirationDate() {
        return expirationDate;
    }

    public Optional<InstantFilter> optionalExpirationDate() {
        return Optional.ofNullable(expirationDate);
    }

    public InstantFilter expirationDate() {
        if (expirationDate == null) {
            setExpirationDate(new InstantFilter());
        }
        return expirationDate;
    }

    public void setExpirationDate(InstantFilter expirationDate) {
        this.expirationDate = expirationDate;
    }

    public IntegerFilter getQueuePosition() {
        return queuePosition;
    }

    public Optional<IntegerFilter> optionalQueuePosition() {
        return Optional.ofNullable(queuePosition);
    }

    public IntegerFilter queuePosition() {
        if (queuePosition == null) {
            setQueuePosition(new IntegerFilter());
        }
        return queuePosition;
    }

    public void setQueuePosition(IntegerFilter queuePosition) {
        this.queuePosition = queuePosition;
    }

    public ReservationStatusFilter getStatus() {
        return status;
    }

    public Optional<ReservationStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ReservationStatusFilter status() {
        if (status == null) {
            setStatus(new ReservationStatusFilter());
        }
        return status;
    }

    public void setStatus(ReservationStatusFilter status) {
        this.status = status;
    }

    public LongFilter getMemberId() {
        return memberId;
    }

    public Optional<LongFilter> optionalMemberId() {
        return Optional.ofNullable(memberId);
    }

    public LongFilter memberId() {
        if (memberId == null) {
            setMemberId(new LongFilter());
        }
        return memberId;
    }

    public void setMemberId(LongFilter memberId) {
        this.memberId = memberId;
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
        final ReservationCriteria that = (ReservationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reservationDate, that.reservationDate) &&
            Objects.equals(expirationDate, that.expirationDate) &&
            Objects.equals(queuePosition, that.queuePosition) &&
            Objects.equals(status, that.status) &&
            Objects.equals(memberId, that.memberId) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reservationDate, expirationDate, queuePosition, status, memberId, bookId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReservationDate().map(f -> "reservationDate=" + f + ", ").orElse("") +
            optionalExpirationDate().map(f -> "expirationDate=" + f + ", ").orElse("") +
            optionalQueuePosition().map(f -> "queuePosition=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalMemberId().map(f -> "memberId=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
