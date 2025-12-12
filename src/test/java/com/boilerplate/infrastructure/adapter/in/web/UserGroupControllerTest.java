package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.UserGroupService;
import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.infrastructure.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserGroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserGroupService userGroupService;

    @InjectMocks
    private UserGroupController userGroupController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userGroupController).build();
    }

    @Test
    void getAllGroups_ShouldReturnList() throws Exception {
        when(userGroupService.getAllGroups())
                .thenReturn(List.of(UserGroup.builder().id(1L).name("Admin Group").build()));

        mockMvc.perform(get("/api/v1/user-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin Group"));
    }

    @Test
    void createGroup_ShouldReturnCreated() throws Exception {
        UserGroup group = UserGroup.builder().name("New Group").build();
        when(userGroupService.createGroup(any(UserGroup.class))).thenReturn(group);

        mockMvc.perform(post("/api/v1/user-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"New Group\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Group"));
    }

    @Test
    void deleteGroup_ShouldCallService() throws Exception {
        mockMvc.perform(delete("/api/v1/user-groups/1"))
                .andExpect(status().isNoContent());
    }
}
