package com.dickson.libraryapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LibraryMemberCriteriaTest {

    @Test
    void newLibraryMemberCriteriaHasAllFiltersNullTest() {
        var libraryMemberCriteria = new LibraryMemberCriteria();
        assertThat(libraryMemberCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void libraryMemberCriteriaFluentMethodsCreatesFiltersTest() {
        var libraryMemberCriteria = new LibraryMemberCriteria();

        setAllFilters(libraryMemberCriteria);

        assertThat(libraryMemberCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void libraryMemberCriteriaCopyCreatesNullFilterTest() {
        var libraryMemberCriteria = new LibraryMemberCriteria();
        var copy = libraryMemberCriteria.copy();

        assertThat(libraryMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(libraryMemberCriteria)
        );
    }

    @Test
    void libraryMemberCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var libraryMemberCriteria = new LibraryMemberCriteria();
        setAllFilters(libraryMemberCriteria);

        var copy = libraryMemberCriteria.copy();

        assertThat(libraryMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(libraryMemberCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var libraryMemberCriteria = new LibraryMemberCriteria();

        assertThat(libraryMemberCriteria).hasToString("LibraryMemberCriteria{}");
    }

    private static void setAllFilters(LibraryMemberCriteria libraryMemberCriteria) {
        libraryMemberCriteria.id();
        libraryMemberCriteria.membershipNumber();
        libraryMemberCriteria.phoneNumber();
        libraryMemberCriteria.birthDate();
        libraryMemberCriteria.address();
        libraryMemberCriteria.registrationDate();
        libraryMemberCriteria.status();
        libraryMemberCriteria.internalUserId();
        libraryMemberCriteria.distinct();
    }

    private static Condition<LibraryMemberCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMembershipNumber()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getBirthDate()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getRegistrationDate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getInternalUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LibraryMemberCriteria> copyFiltersAre(
        LibraryMemberCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMembershipNumber(), copy.getMembershipNumber()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getBirthDate(), copy.getBirthDate()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getRegistrationDate(), copy.getRegistrationDate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getInternalUserId(), copy.getInternalUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
