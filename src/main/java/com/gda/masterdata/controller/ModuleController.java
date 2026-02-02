package com.gda.masterdata.controller;

import com.gda.masterdata.dto.module.ModuleDto;
import com.gda.masterdata.service.module.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/masterdata/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @GetMapping
    public ResponseEntity<List<ModuleDto>> getModules() {
        return ResponseEntity.ok(moduleService.getModulesForCurrentUser());
    }
}
