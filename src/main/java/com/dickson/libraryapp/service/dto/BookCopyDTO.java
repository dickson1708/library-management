package com.dickson.libraryapp.service.dto;

import com.dickson.libraryapp.domain.enumeration.BookCopyStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.dickson.libraryapp.domain.BookCopy} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCopyDTO implements Serializable {

    private Long id;

    @NotNull
    private String barcode;

    @NotNull
    private String shelfLocation;

    private LocalDate acquisitionDate;

    @NotNull
    private BookCopyStatus status;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BookCopyStatus getStatus() {
        return status;
    }

    public void setStatus(BookCopyStatus status) {
        this.status = status;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookCopyDTO)) {
            return false;
        }

        BookCopyDTO bookCopyDTO = (BookCopyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookCopyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCopyDTO{" +
            "id=" + getId() +
            ", barcode='" + getBarcode() + "'" +
            ", shelfLocation='" + getShelfLocation() + "'" +
            ", acquisitionDate='" + getAcquisitionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", book=" + getBook() +
            "}";
    }
}
