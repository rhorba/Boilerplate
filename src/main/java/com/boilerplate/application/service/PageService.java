package com.boilerplate.application.service;

import com.boilerplate.domain.model.Page;
import com.boilerplate.domain.port.out.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final ActivityLogService activityLogService;

    public Page createPage(Page page) {
        // Basic validation or slug generation could go here
        if (page.getSlug() == null || page.getSlug().isEmpty()) {
            page.setSlug(page.getTitle().toLowerCase().replace(" ", "-"));
        }
        Page savedPage = pageRepository.save(page);
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log("CREATE_PAGE", "Created page: " + savedPage.getTitle(), currentUserEmail);
        return savedPage;
    }

    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }

    public Optional<Page> getPageBySlug(String slug) {
        return pageRepository.findBySlug(slug);
    }

    public Page updatePage(Long id, Page updatedPage) {
        return pageRepository.findById(id).map(existingPage -> {
            existingPage.setTitle(updatedPage.getTitle());
            existingPage.setContent(updatedPage.getContent());
            existingPage.setRoles(updatedPage.getRoles());
            // Slug update logic if needed
            if (updatedPage.getSlug() != null && !updatedPage.getSlug().isEmpty()) {
                existingPage.setSlug(updatedPage.getSlug());
            }
            existingPage.setSchema(updatedPage.getSchema());
            existingPage.setAccessControl(updatedPage.getAccessControl());
            existingPage.setIcon(updatedPage.getIcon());
            Page saved = pageRepository.save(existingPage);
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            activityLogService.log("UPDATE_PAGE", "Updated page: " + saved.getTitle(), currentUserEmail);
            return saved;
        }).orElseThrow(() -> new RuntimeException("Page not found"));
    }

    public void deletePage(Long id) {
        pageRepository.deleteById(id);
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log("DELETE_PAGE", "Deleted page ID: " + id, currentUserEmail);
    }
}
