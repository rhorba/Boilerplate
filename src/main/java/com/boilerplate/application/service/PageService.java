package com.boilerplate.application.service;

import com.boilerplate.domain.model.Page;
import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.domain.port.out.PageRepository;
import com.boilerplate.domain.port.out.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final UserGroupRepository userGroupRepository;
    private final ActivityLogService activityLogService;

    public Page createPage(Page page) {
        if (page.getSlug() == null || page.getSlug().isEmpty()) {
            page.setSlug(page.getTitle().toLowerCase().replace(" ", "-"));
        }
        Page savedPage = pageRepository.save(page);
        logActivity("CREATE_PAGE", "Created page: " + savedPage.getTitle());
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
            existingPage.setRoles(updatedPage.getRoles()); // Existing role logic, keep for backward compat or migrate
            if (updatedPage.getSlug() != null && !updatedPage.getSlug().isEmpty()) {
                existingPage.setSlug(updatedPage.getSlug());
            }
            existingPage.setSchema(updatedPage.getSchema());
            existingPage.setAccessControl(updatedPage.getAccessControl());
            existingPage.setIcon(updatedPage.getIcon());

            // Handle groups assignment
            if (updatedPage.getGroups() != null) {
                // Fetch groups from DB to ensure they exist and have IDs
                List<UserGroup> groups = updatedPage.getGroups().stream()
                        .filter(g -> g.getId() != null)
                        .map(g -> userGroupRepository.findById(g.getId()).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList());
                existingPage.setGroups(groups);
            }

            Page saved = pageRepository.save(existingPage);
            logActivity("UPDATE_PAGE", "Updated page: " + saved.getTitle());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Page not found"));
    }

    public void deletePage(Long id) {
        pageRepository.deleteById(id);
        logActivity("DELETE_PAGE", "Deleted page ID: " + id);
    }

    private void logActivity(String action, String details) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log(action, details, currentUserEmail);
    }
}
