package com.dickson.libraryapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookCopyCriteriaTest {

    @Test
    void newBookCopyCriteriaHasAllFiltersNullTest() {
        var bookCopyCriteria = new BookCopyCriteria();
        assertThat(bookCopyCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookCopyCriteriaFluentMethodsCreatesFiltersTest() {
        var bookCopyCriteria = new BookCopyCriteria();

        setAllFilters(bookCopyCriteria);

        assertThat(bookCopyCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookCopyCriteriaCopyCreatesNullFilterTest() {
        var bookCopyCriteria = new BookCopyCriteria();
        var copy = bookCopyCriteria.copy();

        assertThat(bookCopyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookCopyCriteria)
        );
    }

    @Test
    void bookCopyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookCopyCriteria = new BookCopyCriteria();
        setAllFilters(bookCopyCriteria);

        var copy = bookCopyCriteria.copy();

        assertThat(bookCopyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookCopyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookCopyCriteria = new BookCopyCriteria();

        assertThat(bookCopyCriteria).hasToString("BookCopyCriteria{}");
    }

    private static void setAllFilters(BookCopyCriteria bookCopyCriteria) {
        bookCopyCriteria.id();
        bookCopyCriteria.barcode();
        bookCopyCriteria.shelfLocation();
        bookCopyCriteria.acquisitionDate();
        bookCopyCriteria.status();
        bookCopyCriteria.bookId();
        bookCopyCriteria.distinct();
    }

    private static Condition<BookCopyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBarcode()) &&
                condition.apply(criteria.getShelfLocation()) &&
                condition.apply(criteria.getAcquisitionDate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookCopyCriteria> copyFiltersAre(BookCopyCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBarcode(), copy.getBarcode()) &&
                condition.apply(criteria.getShelfLocation(), copy.getShelfLocation()) &&
                condition.apply(criteria.getAcquisitionDate(), copy.getAcquisitionDate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
