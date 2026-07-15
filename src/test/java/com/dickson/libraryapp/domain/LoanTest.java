package com.dickson.libraryapp.domain;

import static com.dickson.libraryapp.domain.BookCopyTestSamples.*;
import static com.dickson.libraryapp.domain.LibraryMemberTestSamples.*;
import static com.dickson.libraryapp.domain.LoanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dickson.libraryapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LoanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Loan.class);
        Loan loan1 = getLoanSample1();
        Loan loan2 = new Loan();
        assertThat(loan1).isNotEqualTo(loan2);

        loan2.setId(loan1.getId());
        assertThat(loan1).isEqualTo(loan2);

        loan2 = getLoanSample2();
        assertThat(loan1).isNotEqualTo(loan2);
    }

    @Test
    void memberTest() {
        Loan loan = getLoanRandomSampleGenerator();
        LibraryMember libraryMemberBack = getLibraryMemberRandomSampleGenerator();

        loan.setMember(libraryMemberBack);
        assertThat(loan.getMember()).isEqualTo(libraryMemberBack);

        loan.member(null);
        assertThat(loan.getMember()).isNull();
    }

    @Test
    void bookCopyTest() {
        Loan loan = getLoanRandomSampleGenerator();
        BookCopy bookCopyBack = getBookCopyRandomSampleGenerator();

        loan.setBookCopy(bookCopyBack);
        assertThat(loan.getBookCopy()).isEqualTo(bookCopyBack);

        loan.bookCopy(null);
        assertThat(loan.getBookCopy()).isNull();
    }
}
