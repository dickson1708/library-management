package com.dickson.libraryapp.service.criteria;

import com.dickson.libraryapp.domain.enumeration.LibraryMemberStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dickson.libraryapp.domain.LibraryMember} entity. This class is used
 * in {@link com.dickson.libraryapp.web.rest.LibraryMemberResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /library-members?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LibraryMemberCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LibraryMemberStatus
     */
    public static class LibraryMemberStatusFilter extends Filter<LibraryMemberStatus> {

        public LibraryMemberStatusFilter() {}

        public LibraryMemberStatusFilter(LibraryMemberStatusFilter filter) {
            super(filter);
        }

        @Override
        public LibraryMemberStatusFilter copy() {
            return new LibraryMemberStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter membershipNumber;

    private StringFilter phoneNumber;

    private LocalDateFilter birthDate;

    private StringFilter address;

    private LocalDateFilter registrationDate;

    private LibraryMemberStatusFilter status;

    private LongFilter internalUserId;

    private Boolean distinct;

    public LibraryMemberCriteria() {}

    public LibraryMemberCriteria(LibraryMemberCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.membershipNumber = other.optionalMembershipNumber().map(LongFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.birthDate = other.optionalBirthDate().map(LocalDateFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.registrationDate = other.optionalRegistrationDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(LibraryMemberStatusFilter::copy).orElse(null);
        this.internalUserId = other.optionalInternalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LibraryMemberCriteria copy() {
        return new LibraryMemberCriteria(this);
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

    public LongFilter getMembershipNumber() {
        return membershipNumber;
    }

    public Optional<LongFilter> optionalMembershipNumber() {
        return Optional.ofNullable(membershipNumber);
    }

    public LongFilter membershipNumber() {
        if (membershipNumber == null) {
            setMembershipNumber(new LongFilter());
        }
        return membershipNumber;
    }

    public void setMembershipNumber(LongFilter membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            setPhoneNumber(new StringFilter());
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateFilter getBirthDate() {
        return birthDate;
    }

    public Optional<LocalDateFilter> optionalBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public LocalDateFilter birthDate() {
        if (birthDate == null) {
            setBirthDate(new LocalDateFilter());
        }
        return birthDate;
    }

    public void setBirthDate(LocalDateFilter birthDate) {
        this.birthDate = birthDate;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public LocalDateFilter getRegistrationDate() {
        return registrationDate;
    }

    public Optional<LocalDateFilter> optionalRegistrationDate() {
        return Optional.ofNullable(registrationDate);
    }

    public LocalDateFilter registrationDate() {
        if (registrationDate == null) {
            setRegistrationDate(new LocalDateFilter());
        }
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateFilter registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LibraryMemberStatusFilter getStatus() {
        return status;
    }

    public Optional<LibraryMemberStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public LibraryMemberStatusFilter status() {
        if (status == null) {
            setStatus(new LibraryMemberStatusFilter());
        }
        return status;
    }

    public void setStatus(LibraryMemberStatusFilter status) {
        this.status = status;
    }

    public LongFilter getInternalUserId() {
        return internalUserId;
    }

    public Optional<LongFilter> optionalInternalUserId() {
        return Optional.ofNullable(internalUserId);
    }

    public LongFilter internalUserId() {
        if (internalUserId == null) {
            setInternalUserId(new LongFilter());
        }
        return internalUserId;
    }

    public void setInternalUserId(LongFilter internalUserId) {
        this.internalUserId = internalUserId;
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
        final LibraryMemberCriteria that = (LibraryMemberCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(membershipNumber, that.membershipNumber) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(address, that.address) &&
            Objects.equals(registrationDate, that.registrationDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(internalUserId, that.internalUserId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, membershipNumber, phoneNumber, birthDate, address, registrationDate, status, internalUserId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryMemberCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMembershipNumber().map(f -> "membershipNumber=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalBirthDate().map(f -> "birthDate=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalRegistrationDate().map(f -> "registrationDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalInternalUserId().map(f -> "internalUserId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
