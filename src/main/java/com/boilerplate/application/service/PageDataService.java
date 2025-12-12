package com.boilerplate.application.service;

import com.boilerplate.domain.model.PageData;
import com.boilerplate.domain.port.out.PageDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageDataService {

    private final PageDataRepository pageDataRepository;
    private final ActivityLogService activityLogService;

    public PageData createPageData(PageData pageData) {
        PageData saved = pageDataRepository.save(pageData);
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log("CREATE_PAGE_DATA", "Created data for page ID: " + saved.getPageId(), currentUserEmail);
        return saved;
    }

    public List<PageData> getAllDataByPageId(Long pageId) {
        return pageDataRepository.findAllByPageId(pageId);
    }

    public Optional<PageData> getPageDataById(Long id) {
        return pageDataRepository.findById(id);
    }

    public PageData updatePageData(Long id, PageData updatedData) {
        return pageDataRepository.findById(id).map(existing -> {
            existing.setData(updatedData.getData());
            PageData saved = pageDataRepository.save(existing);
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            activityLogService.log("UPDATE_PAGE_DATA", "Updated data ID: " + saved.getId(), currentUserEmail);
            return saved;
        }).orElseThrow(() -> new RuntimeException("Page Data not found"));
    }

    public void deletePageData(Long id) {
        pageDataRepository.deleteById(id);
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log("DELETE_PAGE_DATA", "Deleted data ID: " + id, currentUserEmail);
    }
}
