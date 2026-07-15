package com.dickson.libraryapp.domain;

import static com.dickson.libraryapp.domain.BookCopyTestSamples.*;
import static com.dickson.libraryapp.domain.BookTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dickson.libraryapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookCopyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookCopy.class);
        BookCopy bookCopy1 = getBookCopySample1();
        BookCopy bookCopy2 = new BookCopy();
        assertThat(bookCopy1).isNotEqualTo(bookCopy2);

        bookCopy2.setId(bookCopy1.getId());
        assertThat(bookCopy1).isEqualTo(bookCopy2);

        bookCopy2 = getBookCopySample2();
        assertThat(bookCopy1).isNotEqualTo(bookCopy2);
    }

    @Test
    void bookTest() {
        BookCopy bookCopy = getBookCopyRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        bookCopy.setBook(bookBack);
        assertThat(bookCopy.getBook()).isEqualTo(bookBack);

        bookCopy.book(null);
        assertThat(bookCopy.getBook()).isNull();
    }
}
