package com.dickson.libraryapp.domain;

import static com.dickson.libraryapp.domain.BookTestSamples.*;
import static com.dickson.libraryapp.domain.CategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dickson.libraryapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Book.class);
        Book book1 = getBookSample1();
        Book book2 = new Book();
        assertThat(book1).isNotEqualTo(book2);

        book2.setId(book1.getId());
        assertThat(book1).isEqualTo(book2);

        book2 = getBookSample2();
        assertThat(book1).isNotEqualTo(book2);
    }

    @Test
    void categoryTest() {
        Book book = getBookRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        book.setCategory(categoryBack);
        assertThat(book.getCategory()).isEqualTo(categoryBack);

        book.category(null);
        assertThat(book.getCategory()).isNull();
    }
}
