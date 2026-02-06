package com.gda.masterdata.dto.cache;

import lombok.Data;

// Простой DTO для ответа
@Data
public class CacheClearResponse {
    private final String message;
    private final long deletedKeysCount;
}
