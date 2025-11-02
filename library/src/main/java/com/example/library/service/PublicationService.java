package com.example.library.service;

import com.example.library.dto.response.Publicationdto;
import com.example.library.entity.Publication;
import com.example.library.entity.enums.PublicationStatus;
import com.example.library.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PublicationService implements IPublicationService{
    @Autowired
    PublicationRepository publicationRepository;

    @Override
    public boolean save(Publication publication) {
        return publicationRepository.save(publication).getPublicationId() != null;
    }

    @Override
    public Optional<Publicationdto> findByBarcode(String barcode) {
        return publicationRepository.findByBarcode(barcode)
                .filter(pub -> pub.getStatus() == PublicationStatus.Available)
                .map(pub -> {
                    var book = pub.getBook();
                    var category = book.getCategory();

                    return Publicationdto.builder()
                            .publicationId(pub.getPublicationId())
                            .bookId(book.getBookId())
                            .barcode(pub.getBarcode())
                            .notes(pub.getNotes())
                            .title(book.getTitle())
                            .publisher(book.getPublisher())
                            .publishYear(book.getPublishYear())
                            .language(book.getLanguage())
                            .description(book.getDescription())
                            .categoryId(category != null ? category.getCategoryId() : null)
                            .categoryName(category != null ? category.getCategoryName() : null)
                            .build();
                });
    }

    @Override
    public Optional<Publicationdto> findByPublId(Long publicationId) {
        return publicationRepository.findByPublicationId(publicationId)
                .filter(pub -> pub.getStatus() == PublicationStatus.Available)
                .map(pub -> {
                    var book = pub.getBook();
                    var category = book.getCategory();

                    return Publicationdto.builder()
                            .publicationId(pub.getPublicationId())
                            .bookId(book.getBookId())
                            .barcode(pub.getBarcode())
                            .notes(pub.getNotes())
                            .title(book.getTitle())
                            .publisher(book.getPublisher())
                            .publishYear(book.getPublishYear())
                            .language(book.getLanguage())
                            .description(book.getDescription())
                            .categoryId(category != null ? category.getCategoryId() : null)
                            .categoryName(category != null ? category.getCategoryName() : null)
                            .build();
                });
    }

    @Override
    public Optional<Publication> findByPublicationId(Long publicationId) {
        return Optional.ofNullable(publicationRepository.findByPublicationId(publicationId)
                .filter(pub -> pub.getStatus() == PublicationStatus.Available)
                .orElse(null));
    }
}
