package com.dickson.libraryapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dickson.libraryapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LibraryMemberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LibraryMemberDTO.class);
        LibraryMemberDTO libraryMemberDTO1 = new LibraryMemberDTO();
        libraryMemberDTO1.setId(1L);
        LibraryMemberDTO libraryMemberDTO2 = new LibraryMemberDTO();
        assertThat(libraryMemberDTO1).isNotEqualTo(libraryMemberDTO2);
        libraryMemberDTO2.setId(libraryMemberDTO1.getId());
        assertThat(libraryMemberDTO1).isEqualTo(libraryMemberDTO2);
        libraryMemberDTO2.setId(2L);
        assertThat(libraryMemberDTO1).isNotEqualTo(libraryMemberDTO2);
        libraryMemberDTO1.setId(null);
        assertThat(libraryMemberDTO1).isNotEqualTo(libraryMemberDTO2);
    }
}
