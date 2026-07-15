package com.dickson.libraryapp.repository;

import com.dickson.libraryapp.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reservation entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    default Optional<Reservation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Reservation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Reservation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select reservation from Reservation reservation left join fetch reservation.member left join fetch reservation.book",
        countQuery = "select count(reservation) from Reservation reservation"
    )
    Page<Reservation> findAllWithToOneRelationships(Pageable pageable);

    @Query("select reservation from Reservation reservation left join fetch reservation.member left join fetch reservation.book")
    List<Reservation> findAllWithToOneRelationships();

    @Query(
        "select reservation from Reservation reservation left join fetch reservation.member left join fetch reservation.book where reservation.id =:id"
    )
    Optional<Reservation> findOneWithToOneRelationships(@Param("id") Long id);
}
