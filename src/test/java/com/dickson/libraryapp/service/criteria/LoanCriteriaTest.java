package com.dickson.libraryapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LoanCriteriaTest {

    @Test
    void newLoanCriteriaHasAllFiltersNullTest() {
        var loanCriteria = new LoanCriteria();
        assertThat(loanCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void loanCriteriaFluentMethodsCreatesFiltersTest() {
        var loanCriteria = new LoanCriteria();

        setAllFilters(loanCriteria);

        assertThat(loanCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void loanCriteriaCopyCreatesNullFilterTest() {
        var loanCriteria = new LoanCriteria();
        var copy = loanCriteria.copy();

        assertThat(loanCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(loanCriteria)
        );
    }

    @Test
    void loanCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var loanCriteria = new LoanCriteria();
        setAllFilters(loanCriteria);

        var copy = loanCriteria.copy();

        assertThat(loanCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(loanCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var loanCriteria = new LoanCriteria();

        assertThat(loanCriteria).hasToString("LoanCriteria{}");
    }

    private static void setAllFilters(LoanCriteria loanCriteria) {
        loanCriteria.id();
        loanCriteria.loanDate();
        loanCriteria.dueDate();
        loanCriteria.returnDate();
        loanCriteria.renewals();
        loanCriteria.notes();
        loanCriteria.status();
        loanCriteria.memberId();
        loanCriteria.bookCopyId();
        loanCriteria.distinct();
    }

    private static Condition<LoanCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLoanDate()) &&
                condition.apply(criteria.getDueDate()) &&
                condition.apply(criteria.getReturnDate()) &&
                condition.apply(criteria.getRenewals()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getMemberId()) &&
                condition.apply(criteria.getBookCopyId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LoanCriteria> copyFiltersAre(LoanCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLoanDate(), copy.getLoanDate()) &&
                condition.apply(criteria.getDueDate(), copy.getDueDate()) &&
                condition.apply(criteria.getReturnDate(), copy.getReturnDate()) &&
                condition.apply(criteria.getRenewals(), copy.getRenewals()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getMemberId(), copy.getMemberId()) &&
                condition.apply(criteria.getBookCopyId(), copy.getBookCopyId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
