package com.example.library.repository;

import com.example.library.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Optional<Publication> findByBarcode(String barcode);
    Optional<Publication> findByPublicationId(Long publicationId);
}
