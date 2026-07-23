package com.portfolio.service;

import com.portfolio.dto.PortfolioResponse;
import com.portfolio.model.Experience;
import com.portfolio.model.PersonalInfo;
import com.portfolio.model.Project;
import com.portfolio.model.Skill;
import com.portfolio.repository.ExperienceRepository;
import com.portfolio.repository.PersonalInfoRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.repository.SkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PersonalInfoRepository personalInfoRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void getAll_returnsCompletePortfolio() {
        PersonalInfo info = new PersonalInfo();
        info.setName("Jane Doe");

        Skill skill = new Skill();
        skill.setName("Java");

        Experience exp = new Experience();
        exp.setCompany("Tech Corp");

        Project project = new Project();
        project.setName("Portfolio");

        when(personalInfoRepository.findAll()).thenReturn(List.of(info));
        when(skillRepository.findAllByOrderByCategory()).thenReturn(List.of(skill));
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(exp));
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(project));

        PortfolioResponse response = portfolioService.getAll();

        assertNotNull(response);
        assertEquals("Jane Doe", response.getPersonalInfo().getName());
        assertEquals(1, response.getSkills().size());
        assertEquals("Java", response.getSkills().get(0).getName());
        assertEquals(1, response.getExperiences().size());
        assertEquals("Tech Corp", response.getExperiences().get(0).getCompany());
        assertEquals(1, response.getProjects().size());
        assertEquals("Portfolio", response.getProjects().get(0).getName());
    }

    @Test
    void getAll_returnsNullPersonalInfoWhenEmpty() {
        when(personalInfoRepository.findAll()).thenReturn(Collections.emptyList());
        when(skillRepository.findAllByOrderByCategory()).thenReturn(Collections.emptyList());
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(Collections.emptyList());
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(Collections.emptyList());

        PortfolioResponse response = portfolioService.getAll();

        assertNotNull(response);
        assertNull(response.getPersonalInfo());
        assertTrue(response.getSkills().isEmpty());
        assertTrue(response.getExperiences().isEmpty());
        assertTrue(response.getProjects().isEmpty());
    }

    @Test
    void getAll_usesFirstPersonalInfoWhenMultipleExist() {
        PersonalInfo first = new PersonalInfo();
        first.setName("First");
        PersonalInfo second = new PersonalInfo();
        second.setName("Second");

        when(personalInfoRepository.findAll()).thenReturn(List.of(first, second));
        when(skillRepository.findAllByOrderByCategory()).thenReturn(Collections.emptyList());
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(Collections.emptyList());
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(Collections.emptyList());

        PortfolioResponse response = portfolioService.getAll();

        assertEquals("First", response.getPersonalInfo().getName());
    }

    @Test
    void getAll_callsRepositoriesInOrder() {
        when(personalInfoRepository.findAll()).thenReturn(List.of());
        when(skillRepository.findAllByOrderByCategory()).thenReturn(List.of());
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());

        portfolioService.getAll();

        verify(personalInfoRepository).findAll();
        verify(skillRepository).findAllByOrderByCategory();
        verify(experienceRepository).findAllByOrderByDisplayOrderAsc();
        verify(projectRepository).findAllByOrderByDisplayOrderAsc();
    }

    @Test
    void getAll_preservesMultipleSkillsSortedByCategory() {
        Skill backend = new Skill();
        backend.setName("Java");
        backend.setCategory("Backend");
        Skill frontend = new Skill();
        frontend.setName("React");
        frontend.setCategory("Frontend");
        Skill database = new Skill();
        database.setName("MySQL");
        database.setCategory("Database");

        when(personalInfoRepository.findAll()).thenReturn(List.of());
        when(skillRepository.findAllByOrderByCategory()).thenReturn(List.of(backend, frontend, database));
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());

        PortfolioResponse response = portfolioService.getAll();

        assertEquals(3, response.getSkills().size());
        assertEquals("Backend", response.getSkills().get(0).getCategory());
        assertEquals("Frontend", response.getSkills().get(1).getCategory());
        assertEquals("Database", response.getSkills().get(2).getCategory());
    }

    @Test
    void getAll_preservesExperienceDetails() {
        Experience exp = new Experience();
        exp.setId(1L);
        exp.setCompany("Tech Corp");
        exp.setRole("Software Engineer");
        exp.setLocation("Hyderabad");
        exp.setStartDate("2022-01");
        exp.setEndDate("Present");
        exp.setIsCurrent(true);
        exp.setDescription("Built microservices");
        exp.setHighlights(List.of("Improved perf by 40%", "Led team of 3"));
        exp.setDisplayOrder(1);

        when(personalInfoRepository.findAll()).thenReturn(List.of());
        when(skillRepository.findAllByOrderByCategory()).thenReturn(List.of());
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(exp));
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());

        PortfolioResponse response = portfolioService.getAll();

        Experience result = response.getExperiences().get(0);
        assertEquals("Tech Corp", result.getCompany());
        assertEquals("Software Engineer", result.getRole());
        assertEquals("Hyderabad", result.getLocation());
        assertEquals("2022-01", result.getStartDate());
        assertEquals("Present", result.getEndDate());
        assertTrue(result.getIsCurrent());
        assertEquals(2, result.getHighlights().size());
    }

    @Test
    void getAll_preservesProjectDetails() {
        Project project = new Project();
        project.setId(1L);
        project.setName("E-Commerce App");
        project.setDescription("Full stack e-commerce");
        project.setTechStack("Spring Boot, React, PostgreSQL");
        project.setGithubUrl("https://github.com/jane/ecommerce");
        project.setDemoUrl("https://shop.jane.dev");
        project.setIsFeatured(true);
        project.setDisplayOrder(1);

        when(personalInfoRepository.findAll()).thenReturn(List.of());
        when(skillRepository.findAllByOrderByCategory()).thenReturn(List.of());
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(project));

        PortfolioResponse response = portfolioService.getAll();

        Project result = response.getProjects().get(0);
        assertEquals("E-Commerce App", result.getName());
        assertEquals("Full stack e-commerce", result.getDescription());
        assertEquals("Spring Boot, React, PostgreSQL", result.getTechStack());
        assertEquals("https://github.com/jane/ecommerce", result.getGithubUrl());
        assertEquals("https://shop.jane.dev", result.getDemoUrl());
        assertTrue(result.getIsFeatured());
    }

    @Test
    void getAll_handlesEmptyHighlightsList() {
        Experience exp = new Experience();
        exp.setCompany("Corp");
        exp.setHighlights(Collections.emptyList());

        when(personalInfoRepository.findAll()).thenReturn(List.of());
        when(skillRepository.findAllByOrderByCategory()).thenReturn(List.of());
        when(experienceRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(exp));
        when(projectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());

        PortfolioResponse response = portfolioService.getAll();

        assertTrue(response.getExperiences().get(0).getHighlights().isEmpty());
    }
}
