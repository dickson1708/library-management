package com.dickson.libraryapp.domain;

import com.dickson.libraryapp.domain.enumeration.BookCopyStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BookCopy.
 */
@Entity
@Table(name = "book_copy")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCopy implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "barcode", nullable = false, unique = true)
    private String barcode;

    @NotNull
    @Column(name = "shelf_location", nullable = false)
    private String shelfLocation;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookCopyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookCopy id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public BookCopy barcode(String barcode) {
        this.setBarcode(barcode);
        return this;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getShelfLocation() {
        return this.shelfLocation;
    }

    public BookCopy shelfLocation(String shelfLocation) {
        this.setShelfLocation(shelfLocation);
        return this;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public LocalDate getAcquisitionDate() {
        return this.acquisitionDate;
    }

    public BookCopy acquisitionDate(LocalDate acquisitionDate) {
        this.setAcquisitionDate(acquisitionDate);
        return this;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BookCopyStatus getStatus() {
        return this.status;
    }

    public BookCopy status(BookCopyStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BookCopyStatus status) {
        this.status = status;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookCopy book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookCopy)) {
            return false;
        }
        return getId() != null && getId().equals(((BookCopy) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCopy{" +
            "id=" + getId() +
            ", barcode='" + getBarcode() + "'" +
            ", shelfLocation='" + getShelfLocation() + "'" +
            ", acquisitionDate='" + getAcquisitionDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
