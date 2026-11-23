package com.portfolio.controller;

import com.portfolio.dto.admin.ContactMessageReadRequest;
import com.portfolio.dto.admin.ExperienceRequest;
import com.portfolio.dto.admin.PersonalInfoUpdateRequest;
import com.portfolio.dto.admin.ProjectRequest;
import com.portfolio.dto.admin.SkillRequest;
import com.portfolio.model.ContactMessage;
import com.portfolio.model.Experience;
import com.portfolio.model.PersonalInfo;
import com.portfolio.model.Project;
import com.portfolio.model.Skill;
import com.portfolio.service.AdminAuthService;
import com.portfolio.service.AdminPortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPortfolioController {
    private final AdminAuthService adminAuthService;
    private final AdminPortfolioService adminPortfolioService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminPortfolioService.AdminDashboard> dashboard(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        requireAdmin(authorization);
        return privateResponse(adminPortfolioService.getDashboard());
    }

    @PutMapping("/profile")
    public ResponseEntity<PersonalInfo> updateProfile(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody PersonalInfoUpdateRequest request) {
        requireAdmin(authorization);
        return privateResponse(adminPortfolioService.updatePersonalInfo(request));
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> addSkill(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody SkillRequest request) {
        requireAdmin(authorization);
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore())
                .body(adminPortfolioService.saveSkill(null, request));
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<Skill> updateSkill(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody SkillRequest request) {
        requireAdmin(authorization);
        return privateResponse(adminPortfolioService.saveSkill(id, request));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id) {
        requireAdmin(authorization);
        adminPortfolioService.deleteSkill(id);
        return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
    }

    @PostMapping("/experience")
    public ResponseEntity<Experience> addExperience(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody ExperienceRequest request) {
        requireAdmin(authorization);
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore())
                .body(adminPortfolioService.saveExperience(null, request));
    }

    @PutMapping("/experience/{id}")
    public ResponseEntity<Experience> updateExperience(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequest request) {
        requireAdmin(authorization);
        return privateResponse(adminPortfolioService.saveExperience(id, request));
    }

    @DeleteMapping("/experience/{id}")
    public ResponseEntity<Void> deleteExperience(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id) {
        requireAdmin(authorization);
        adminPortfolioService.deleteExperience(id);
        return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody ProjectRequest request) {
        requireAdmin(authorization);
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore())
                .body(adminPortfolioService.saveProject(null, request));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> updateProject(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        requireAdmin(authorization);
        return privateResponse(adminPortfolioService.saveProject(id, request));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id) {
        requireAdmin(authorization);
        adminPortfolioService.deleteProject(id);
        return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
    }

    @PatchMapping("/messages/{id}/read")
    public ResponseEntity<ContactMessage> updateContactReadState(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody ContactMessageReadRequest request) {
        requireAdmin(authorization);
        return privateResponse(adminPortfolioService.setContactRead(id, request.getIsRead()));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteContactMessage(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id) {
        requireAdmin(authorization);
        adminPortfolioService.deleteContact(id);
        return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
    }

    private void requireAdmin(String authorization) {
        String token = authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)
                ? authorization.substring(7).trim() : null;
        if (!adminAuthService.isValidSession(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin session is invalid or expired.");
        }
    }

    private <T> ResponseEntity<T> privateResponse(T body) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(body);
    }
}
