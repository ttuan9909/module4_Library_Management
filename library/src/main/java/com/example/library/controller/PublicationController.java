package com.example.library.controller;

import com.example.library.dto.response.Publicationdto;
import com.example.library.service.IPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/publications")
public class PublicationController {
    @Autowired
    IPublicationService publicationService;

    @GetMapping("/{barcode}")
    public ResponseEntity<Publicationdto> findByBarcode(@PathVariable String barcode) {
        Publicationdto dto = publicationService.findByBarcode(barcode).orElse(null);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(dto);
        }
    }
}
