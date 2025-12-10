package com.boilerplate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageData {
    private Long id;
    private Long pageId;
    private String data; // JSON string containing the dynamic data
}
