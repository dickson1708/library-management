package com.dickson.libraryapp.domain;

import static com.dickson.libraryapp.domain.BookTestSamples.*;
import static com.dickson.libraryapp.domain.LibraryMemberTestSamples.*;
import static com.dickson.libraryapp.domain.ReviewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dickson.libraryapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void memberTest() {
        Review review = getReviewRandomSampleGenerator();
        LibraryMember libraryMemberBack = getLibraryMemberRandomSampleGenerator();

        review.setMember(libraryMemberBack);
        assertThat(review.getMember()).isEqualTo(libraryMemberBack);

        review.member(null);
        assertThat(review.getMember()).isNull();
    }

    @Test
    void bookTest() {
        Review review = getReviewRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        review.setBook(bookBack);
        assertThat(review.getBook()).isEqualTo(bookBack);

        review.book(null);
        assertThat(review.getBook()).isNull();
    }
}
