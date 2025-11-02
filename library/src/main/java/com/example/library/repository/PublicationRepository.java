package com.example.library.repository;

import com.example.library.entity.Publication;
import org.apache.el.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Optional<Publication> findByBarcode(String barcode);
    Optional<Publication> findByPublicationId(Long publicationId);

}
