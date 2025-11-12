package com.example.library.service;

import com.example.library.dto.response.Publicationdto;
import com.example.library.entity.Publication;

import java.util.Optional;

public interface IPublicationService {
    Optional<Publicationdto> findByBarcode(String barcode);
    Optional<Publicationdto> findByPublId(Long publicationId);
    Optional<Publication> findByPublicationId(Long publicationId);
    Optional<Publication> findEntityByBarcode(String barcode);
    boolean save(Publication publication);
}
