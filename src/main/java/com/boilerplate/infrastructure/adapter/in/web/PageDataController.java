package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.PageDataService;
import com.boilerplate.domain.model.PageData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/page-data")
@RequiredArgsConstructor
public class PageDataController {

    private final PageDataService pageDataService;

    @GetMapping("/page/{pageId}")
    public ResponseEntity<List<PageData>> getAllDataByPageId(@PathVariable Long pageId) {
        return ResponseEntity.ok(pageDataService.getAllDataByPageId(pageId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageData> getPageDataById(@PathVariable Long id) {
        return pageDataService.getPageDataById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageData> createPageData(@RequestBody PageData pageData) {
        return ResponseEntity.ok(pageDataService.createPageData(pageData));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageData> updatePageData(@PathVariable Long id, @RequestBody PageData pageData) {
        return ResponseEntity.ok(pageDataService.updatePageData(id, pageData));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePageData(@PathVariable Long id) {
        pageDataService.deletePageData(id);
        return ResponseEntity.noContent().build();
    }
}
