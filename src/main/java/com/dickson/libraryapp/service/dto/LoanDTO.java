package com.dickson.libraryapp.service.dto;

import com.dickson.libraryapp.domain.enumeration.LoanStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.dickson.libraryapp.domain.Loan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoanDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant loanDate;

    @NotNull
    private Instant dueDate;

    private Instant returnDate;

    @Min(value = 0)
    private Integer renewals;

    @Size(max = 500)
    private String notes;

    @NotNull
    private LoanStatus status;

    private LibraryMemberDTO member;

    private BookCopyDTO bookCopy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Instant loanDate) {
        this.loanDate = loanDate;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

    public Integer getRenewals() {
        return renewals;
    }

    public void setRenewals(Integer renewals) {
        this.renewals = renewals;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LibraryMemberDTO getMember() {
        return member;
    }

    public void setMember(LibraryMemberDTO member) {
        this.member = member;
    }

    public BookCopyDTO getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopyDTO bookCopy) {
        this.bookCopy = bookCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoanDTO)) {
            return false;
        }

        LoanDTO loanDTO = (LoanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, loanDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoanDTO{" +
            "id=" + getId() +
            ", loanDate='" + getLoanDate() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", renewals=" + getRenewals() +
            ", notes='" + getNotes() + "'" +
            ", status='" + getStatus() + "'" +
            ", member=" + getMember() +
            ", bookCopy=" + getBookCopy() +
            "}";
    }
}
