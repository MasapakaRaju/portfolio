package com.portfolio.service;

import com.portfolio.dto.admin.ExperienceRequest;
import com.portfolio.dto.admin.PersonalInfoUpdateRequest;
import com.portfolio.dto.admin.ProjectRequest;
import com.portfolio.dto.admin.SkillRequest;
import com.portfolio.model.ContactMessage;
import com.portfolio.model.Experience;
import com.portfolio.model.PersonalInfo;
import com.portfolio.model.Project;
import com.portfolio.model.Skill;
import com.portfolio.repository.ContactMessageRepository;
import com.portfolio.repository.ExperienceRepository;
import com.portfolio.repository.PersonalInfoRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPortfolioService {
    private final PersonalInfoRepository personalInfoRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final ContactMessageRepository contactMessageRepository;

    @Transactional(readOnly = true)
    public AdminDashboard getDashboard() {
        PersonalInfo personalInfo = personalInfoRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio profile is missing."));
        List<Skill> skills = skillRepository.findAllByOrderByCategoryAscNameAsc();
        List<Experience> experiences = experienceRepository.findAllByOrderByDisplayOrderAsc();
        List<Project> projects = projectRepository.findAllByOrderByDisplayOrderAsc();
        List<ContactMessage> messages = contactMessageRepository.findAllByOrderBySentAtDesc();
        long unreadMessages = messages.stream().filter(message -> !Boolean.TRUE.equals(message.getIsRead())).count();
        return new AdminDashboard(personalInfo, skills, experiences, projects, messages,
                new Summary(skills.size(), experiences.size(), projects.size(), messages.size(), unreadMessages));
    }

    @Transactional
    public PersonalInfo updatePersonalInfo(PersonalInfoUpdateRequest request) {
        PersonalInfo profile = personalInfoRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio profile is missing."));
        profile.setName(request.getName().trim());
        profile.setTitle(request.getTitle().trim());
        profile.setTagline(request.getTagline().trim());
        profile.setLocation(request.getLocation().trim());
        profile.setEmail(request.getEmail().trim());
        profile.setPhone(request.getPhone().trim());
        profile.setGithubUrl(cleanOptional(request.getGithubUrl()));
        profile.setLinkedinUrl(cleanOptional(request.getLinkedinUrl()));
        profile.setLeetcodeUrl(cleanOptional(request.getLeetcodeUrl()));
        profile.setResumeUrl(cleanOptional(request.getResumeUrl()));
        profile.setAbout(request.getAbout().trim());
        return personalInfoRepository.save(profile);
    }

    @Transactional
    public Skill saveSkill(Long id, SkillRequest request) {
        Skill skill = id == null ? new Skill() : skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found."));
        skill.setName(request.getName().trim());
        skill.setCategory(request.getCategory().trim());
        skill.setProficiency(request.getProficiency());
        return skillRepository.save(skill);
    }

    @Transactional
    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found.");
        skillRepository.deleteById(id);
    }

    @Transactional
    public Experience saveExperience(Long id, ExperienceRequest request) {
        Experience experience = id == null ? new Experience() : experienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found."));
        experience.setCompany(request.getCompany().trim());
        experience.setRole(request.getRole().trim());
        experience.setLocation(request.getLocation().trim());
        experience.setStartDate(request.getStartDate().trim());
        experience.setEndDate(cleanOptional(request.getEndDate()));
        experience.setIsCurrent(request.getIsCurrent());
        experience.setDescription(request.getDescription().trim());
        experience.setHighlights(request.getHighlights() == null ? List.of() : request.getHighlights().stream()
                .map(String::trim).filter(value -> !value.isEmpty()).toList());
        experience.setDisplayOrder(request.getDisplayOrder());
        return experienceRepository.save(experience);
    }

    @Transactional
    public void deleteExperience(Long id) {
        if (!experienceRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found.");
        experienceRepository.deleteById(id);
    }

    @Transactional
    public Project saveProject(Long id, ProjectRequest request) {
        Project project = id == null ? new Project() : projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found."));
        project.setName(request.getName().trim());
        project.setDescription(request.getDescription().trim());
        project.setTechStack(request.getTechStack().trim());
        project.setGithubUrl(cleanOptional(request.getGithubUrl()));
        project.setDemoUrl(cleanOptional(request.getDemoUrl()));
        project.setIsFeatured(request.getIsFeatured());
        project.setDisplayOrder(request.getDisplayOrder());
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.");
        projectRepository.deleteById(id);
    }

    @Transactional
    public ContactMessage setContactRead(Long id, boolean isRead) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact message not found."));
        message.setIsRead(isRead);
        return contactMessageRepository.save(message);
    }

    @Transactional
    public void deleteContact(Long id) {
        if (!contactMessageRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact message not found.");
        contactMessageRepository.deleteById(id);
    }

    private String cleanOptional(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }

    public record Summary(int skills, int experiences, int projects, int messages, long unreadMessages) {}
    public record AdminDashboard(PersonalInfo personalInfo, List<Skill> skills, List<Experience> experiences,
                                 List<Project> projects, List<ContactMessage> messages, Summary summary) {}
}
