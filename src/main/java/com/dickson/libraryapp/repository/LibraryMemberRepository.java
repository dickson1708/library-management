package com.dickson.libraryapp.repository;

import com.dickson.libraryapp.domain.LibraryMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LibraryMember entity.
 */
@Repository
public interface LibraryMemberRepository extends JpaRepository<LibraryMember, Long>, JpaSpecificationExecutor<LibraryMember> {
    default Optional<LibraryMember> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LibraryMember> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LibraryMember> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select libraryMember from LibraryMember libraryMember left join fetch libraryMember.internalUser",
        countQuery = "select count(libraryMember) from LibraryMember libraryMember"
    )
    Page<LibraryMember> findAllWithToOneRelationships(Pageable pageable);

    @Query("select libraryMember from LibraryMember libraryMember left join fetch libraryMember.internalUser")
    List<LibraryMember> findAllWithToOneRelationships();

    @Query("select libraryMember from LibraryMember libraryMember left join fetch libraryMember.internalUser where libraryMember.id =:id")
    Optional<LibraryMember> findOneWithToOneRelationships(@Param("id") Long id);
}
