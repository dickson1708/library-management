package com.dickson.libraryapp.domain;

import com.dickson.libraryapp.domain.enumeration.LoanStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Loan.
 */
@Entity
@Table(name = "loan")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Loan implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "loan_date", nullable = false)
    private Instant loanDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "return_date")
    private Instant returnDate;

    @Min(value = 0)
    @Column(name = "renewals")
    private Integer renewals;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "internalUser" }, allowSetters = true)
    private LibraryMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "book" }, allowSetters = true)
    private BookCopy bookCopy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Loan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLoanDate() {
        return this.loanDate;
    }

    public Loan loanDate(Instant loanDate) {
        this.setLoanDate(loanDate);
        return this;
    }

    public void setLoanDate(Instant loanDate) {
        this.loanDate = loanDate;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public Loan dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getReturnDate() {
        return this.returnDate;
    }

    public Loan returnDate(Instant returnDate) {
        this.setReturnDate(returnDate);
        return this;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

    public Integer getRenewals() {
        return this.renewals;
    }

    public Loan renewals(Integer renewals) {
        this.setRenewals(renewals);
        return this;
    }

    public void setRenewals(Integer renewals) {
        this.renewals = renewals;
    }

    public String getNotes() {
        return this.notes;
    }

    public Loan notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LoanStatus getStatus() {
        return this.status;
    }

    public Loan status(LoanStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LibraryMember getMember() {
        return this.member;
    }

    public void setMember(LibraryMember libraryMember) {
        this.member = libraryMember;
    }

    public Loan member(LibraryMember libraryMember) {
        this.setMember(libraryMember);
        return this;
    }

    public BookCopy getBookCopy() {
        return this.bookCopy;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }

    public Loan bookCopy(BookCopy bookCopy) {
        this.setBookCopy(bookCopy);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Loan)) {
            return false;
        }
        return getId() != null && getId().equals(((Loan) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Loan{" +
            "id=" + getId() +
            ", loanDate='" + getLoanDate() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", renewals=" + getRenewals() +
            ", notes='" + getNotes() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
