package com.gda.masterdata.controller;

import com.gda.masterdata.dto.cache.CacheClearResponse;
import com.gda.masterdata.service.сache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/masterdata/cache")
@RequiredArgsConstructor
public class CacheController {
    private final CacheService cacheService;

    @GetMapping("/flush-all")
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CacheClearResponse> flushAll() {
        long count = cacheService.flushAll();
        return ResponseEntity.ok(new CacheClearResponse("All caches cleared successfully", count));
    }

    @GetMapping("/clear")
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CacheClearResponse> clearByTag(
        @RequestParam String tagName
    ) {
        long count = cacheService.clearCacheByTag(tagName);
        return ResponseEntity.ok(new CacheClearResponse("Cleared cache group: " + tagName, count));
    }

}
