package com.dickson.libraryapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReservationCriteriaTest {

    @Test
    void newReservationCriteriaHasAllFiltersNullTest() {
        var reservationCriteria = new ReservationCriteria();
        assertThat(reservationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void reservationCriteriaFluentMethodsCreatesFiltersTest() {
        var reservationCriteria = new ReservationCriteria();

        setAllFilters(reservationCriteria);

        assertThat(reservationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void reservationCriteriaCopyCreatesNullFilterTest() {
        var reservationCriteria = new ReservationCriteria();
        var copy = reservationCriteria.copy();

        assertThat(reservationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(reservationCriteria)
        );
    }

    @Test
    void reservationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reservationCriteria = new ReservationCriteria();
        setAllFilters(reservationCriteria);

        var copy = reservationCriteria.copy();

        assertThat(reservationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(reservationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reservationCriteria = new ReservationCriteria();

        assertThat(reservationCriteria).hasToString("ReservationCriteria{}");
    }

    private static void setAllFilters(ReservationCriteria reservationCriteria) {
        reservationCriteria.id();
        reservationCriteria.reservationDate();
        reservationCriteria.expirationDate();
        reservationCriteria.queuePosition();
        reservationCriteria.status();
        reservationCriteria.memberId();
        reservationCriteria.bookId();
        reservationCriteria.distinct();
    }

    private static Condition<ReservationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReservationDate()) &&
                condition.apply(criteria.getExpirationDate()) &&
                condition.apply(criteria.getQueuePosition()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getMemberId()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReservationCriteria> copyFiltersAre(ReservationCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReservationDate(), copy.getReservationDate()) &&
                condition.apply(criteria.getExpirationDate(), copy.getExpirationDate()) &&
                condition.apply(criteria.getQueuePosition(), copy.getQueuePosition()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getMemberId(), copy.getMemberId()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
