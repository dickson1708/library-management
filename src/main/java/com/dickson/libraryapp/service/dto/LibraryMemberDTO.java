package com.dickson.libraryapp.service.dto;

import com.dickson.libraryapp.domain.enumeration.LibraryMemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.dickson.libraryapp.domain.LibraryMember} entity.
 */
@Schema(
    description = "==========================================================\nENTITIES\n=========================================================="
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LibraryMemberDTO implements Serializable {

    private Long id;

    @NotNull
    private Long membershipNumber;

    @NotNull
    @Size(max = 20)
    private String phoneNumber;

    private LocalDate birthDate;

    @Size(max = 255)
    private String address;

    @NotNull
    private LocalDate registrationDate;

    @NotNull
    private LibraryMemberStatus status;

    private UserDTO internalUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(Long membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LibraryMemberStatus getStatus() {
        return status;
    }

    public void setStatus(LibraryMemberStatus status) {
        this.status = status;
    }

    public UserDTO getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(UserDTO internalUser) {
        this.internalUser = internalUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LibraryMemberDTO)) {
            return false;
        }

        LibraryMemberDTO libraryMemberDTO = (LibraryMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, libraryMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryMemberDTO{" +
            "id=" + getId() +
            ", membershipNumber=" + getMembershipNumber() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", address='" + getAddress() + "'" +
            ", registrationDate='" + getRegistrationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", internalUser=" + getInternalUser() +
            "}";
    }
}
