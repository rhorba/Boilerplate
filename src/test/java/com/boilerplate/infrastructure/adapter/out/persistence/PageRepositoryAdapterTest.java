package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Page;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.PageEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataPageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageRepositoryAdapterTest {

    @Mock
    private SpringDataPageRepository repository;

    @InjectMocks
    private PageRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnPage() {
        Page page = Page.builder().title("Test Page").build();
        PageEntity entity = PageEntity.builder().id(1L).title("Test Page").build();

        when(repository.save(any(PageEntity.class))).thenReturn(entity);

        Page result = adapter.save(page);

        assertEquals(1L, result.getId());
        assertEquals("Test Page", result.getTitle());
    }

    @Test
    void findBySlug_ShouldReturnPage() {
        PageEntity entity = PageEntity.builder().id(1L).slug("test-page").build();
        when(repository.findBySlug("test-page")).thenReturn(Optional.of(entity));

        Optional<Page> result = adapter.findBySlug("test-page");

        assertTrue(result.isPresent());
        assertEquals("test-page", result.get().getSlug());
    }

    @Test
    void findAll_ShouldReturnList() {
        PageEntity entity = PageEntity.builder().id(1L).build();
        when(repository.findAll()).thenReturn(Collections.singletonList(entity));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void findById_ShouldReturnPage() {
        PageEntity entity = PageEntity.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Page> result = adapter.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void deleteById_ShouldCallRepo() {
        adapter.deleteById(1L);
        verify(repository).deleteById(1L);
    }
}
