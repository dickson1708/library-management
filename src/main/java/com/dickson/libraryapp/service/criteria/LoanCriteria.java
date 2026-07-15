package com.dickson.libraryapp.service.criteria;

import com.dickson.libraryapp.domain.enumeration.LoanStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dickson.libraryapp.domain.Loan} entity. This class is used
 * in {@link com.dickson.libraryapp.web.rest.LoanResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /loans?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoanCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LoanStatus
     */
    public static class LoanStatusFilter extends Filter<LoanStatus> {

        public LoanStatusFilter() {}

        public LoanStatusFilter(LoanStatusFilter filter) {
            super(filter);
        }

        @Override
        public LoanStatusFilter copy() {
            return new LoanStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter loanDate;

    private InstantFilter dueDate;

    private InstantFilter returnDate;

    private IntegerFilter renewals;

    private StringFilter notes;

    private LoanStatusFilter status;

    private LongFilter memberId;

    private LongFilter bookCopyId;

    private Boolean distinct;

    public LoanCriteria() {}

    public LoanCriteria(LoanCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.loanDate = other.optionalLoanDate().map(InstantFilter::copy).orElse(null);
        this.dueDate = other.optionalDueDate().map(InstantFilter::copy).orElse(null);
        this.returnDate = other.optionalReturnDate().map(InstantFilter::copy).orElse(null);
        this.renewals = other.optionalRenewals().map(IntegerFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(LoanStatusFilter::copy).orElse(null);
        this.memberId = other.optionalMemberId().map(LongFilter::copy).orElse(null);
        this.bookCopyId = other.optionalBookCopyId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LoanCriteria copy() {
        return new LoanCriteria(this);
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

    public InstantFilter getLoanDate() {
        return loanDate;
    }

    public Optional<InstantFilter> optionalLoanDate() {
        return Optional.ofNullable(loanDate);
    }

    public InstantFilter loanDate() {
        if (loanDate == null) {
            setLoanDate(new InstantFilter());
        }
        return loanDate;
    }

    public void setLoanDate(InstantFilter loanDate) {
        this.loanDate = loanDate;
    }

    public InstantFilter getDueDate() {
        return dueDate;
    }

    public Optional<InstantFilter> optionalDueDate() {
        return Optional.ofNullable(dueDate);
    }

    public InstantFilter dueDate() {
        if (dueDate == null) {
            setDueDate(new InstantFilter());
        }
        return dueDate;
    }

    public void setDueDate(InstantFilter dueDate) {
        this.dueDate = dueDate;
    }

    public InstantFilter getReturnDate() {
        return returnDate;
    }

    public Optional<InstantFilter> optionalReturnDate() {
        return Optional.ofNullable(returnDate);
    }

    public InstantFilter returnDate() {
        if (returnDate == null) {
            setReturnDate(new InstantFilter());
        }
        return returnDate;
    }

    public void setReturnDate(InstantFilter returnDate) {
        this.returnDate = returnDate;
    }

    public IntegerFilter getRenewals() {
        return renewals;
    }

    public Optional<IntegerFilter> optionalRenewals() {
        return Optional.ofNullable(renewals);
    }

    public IntegerFilter renewals() {
        if (renewals == null) {
            setRenewals(new IntegerFilter());
        }
        return renewals;
    }

    public void setRenewals(IntegerFilter renewals) {
        this.renewals = renewals;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public LoanStatusFilter getStatus() {
        return status;
    }

    public Optional<LoanStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public LoanStatusFilter status() {
        if (status == null) {
            setStatus(new LoanStatusFilter());
        }
        return status;
    }

    public void setStatus(LoanStatusFilter status) {
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

    public LongFilter getBookCopyId() {
        return bookCopyId;
    }

    public Optional<LongFilter> optionalBookCopyId() {
        return Optional.ofNullable(bookCopyId);
    }

    public LongFilter bookCopyId() {
        if (bookCopyId == null) {
            setBookCopyId(new LongFilter());
        }
        return bookCopyId;
    }

    public void setBookCopyId(LongFilter bookCopyId) {
        this.bookCopyId = bookCopyId;
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
        final LoanCriteria that = (LoanCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(loanDate, that.loanDate) &&
            Objects.equals(dueDate, that.dueDate) &&
            Objects.equals(returnDate, that.returnDate) &&
            Objects.equals(renewals, that.renewals) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(status, that.status) &&
            Objects.equals(memberId, that.memberId) &&
            Objects.equals(bookCopyId, that.bookCopyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loanDate, dueDate, returnDate, renewals, notes, status, memberId, bookCopyId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoanCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLoanDate().map(f -> "loanDate=" + f + ", ").orElse("") +
            optionalDueDate().map(f -> "dueDate=" + f + ", ").orElse("") +
            optionalReturnDate().map(f -> "returnDate=" + f + ", ").orElse("") +
            optionalRenewals().map(f -> "renewals=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalMemberId().map(f -> "memberId=" + f + ", ").orElse("") +
            optionalBookCopyId().map(f -> "bookCopyId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
