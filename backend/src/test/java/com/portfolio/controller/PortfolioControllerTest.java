package com.portfolio.controller;

import com.portfolio.dto.PortfolioResponse;
import com.portfolio.model.Experience;
import com.portfolio.model.PersonalInfo;
import com.portfolio.model.Project;
import com.portfolio.model.Skill;
import com.portfolio.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    private PersonalInfo samplePersonalInfo() {
        PersonalInfo info = new PersonalInfo();
        info.setId(1L);
        info.setName("Jane Doe");
        info.setTitle("Full Stack Developer");
        info.setTagline("Building things");
        info.setLocation("Hyderabad, India");
        info.setEmail("jane@example.com");
        info.setPhone("+919876543210");
        info.setGithubUrl("https://github.com/jane");
        info.setLinkedinUrl("https://linkedin.com/in/jane");
        info.setLeetcodeUrl("https://leetcode.com/jane");
        info.setResumeUrl("https://example.com/resume.pdf");
        info.setAbout("Experienced developer");
        return info;
    }

    private Skill sampleSkill() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");
        skill.setCategory("Backend");
        skill.setProficiency(90);
        return skill;
    }

    private Experience sampleExperience() {
        Experience exp = new Experience();
        exp.setId(1L);
        exp.setCompany("Tech Corp");
        exp.setRole("Software Engineer");
        exp.setLocation("Hyderabad");
        exp.setStartDate("2022-01");
        exp.setEndDate("Present");
        exp.setIsCurrent(true);
        exp.setDescription("Built microservices");
        exp.setHighlights(List.of("Improved performance by 40%"));
        exp.setDisplayOrder(1);
        return exp;
    }

    private Project sampleProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Portfolio");
        project.setDescription("My portfolio website");
        project.setTechStack("Spring Boot, React");
        project.setGithubUrl("https://github.com/jane/portfolio");
        project.setDemoUrl("https://jane.dev");
        project.setIsFeatured(true);
        project.setDisplayOrder(1);
        return project;
    }

    private PortfolioResponse fullPortfolioResponse() {
        return new PortfolioResponse(
                samplePersonalInfo(),
                List.of(sampleSkill()),
                List.of(sampleExperience()),
                List.of(sampleProject())
        );
    }

    @Test
    void getPortfolio_returns200WithAllData() throws Exception {
        when(portfolioService.getAll()).thenReturn(fullPortfolioResponse());

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalInfo.name").value("Jane Doe"))
                .andExpect(jsonPath("$.personalInfo.title").value("Full Stack Developer"))
                .andExpect(jsonPath("$.personalInfo.email").value("jane@example.com"))
                .andExpect(jsonPath("$.skills.length()").value(1))
                .andExpect(jsonPath("$.skills[0].name").value("Java"))
                .andExpect(jsonPath("$.skills[0].category").value("Backend"))
                .andExpect(jsonPath("$.skills[0].proficiency").value(90))
                .andExpect(jsonPath("$.experiences.length()").value(1))
                .andExpect(jsonPath("$.experiences[0].company").value("Tech Corp"))
                .andExpect(jsonPath("$.experiences[0].role").value("Software Engineer"))
                .andExpect(jsonPath("$.projects.length()").value(1))
                .andExpect(jsonPath("$.projects[0].name").value("Portfolio"))
                .andExpect(jsonPath("$.projects[0].isFeatured").value(true));
    }

    @Test
    void getPortfolio_returns200WithNullPersonalInfo() throws Exception {
        PortfolioResponse response = new PortfolioResponse(null, List.of(), List.of(), List.of());
        when(portfolioService.getAll()).thenReturn(response);

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalInfo").isEmpty())
                .andExpect(jsonPath("$.skills.length()").value(0))
                .andExpect(jsonPath("$.experiences.length()").value(0))
                .andExpect(jsonPath("$.projects.length()").value(0));
    }

    @Test
    void getPortfolio_returns200WithMultipleSkills() throws Exception {
        Skill java = new Skill(1L, "Java", "Backend", 90);
        Skill react = new Skill(2L, "React", "Frontend", 85);
        Skill mysql = new Skill(3L, "MySQL", "Database", 80);

        PortfolioResponse response = new PortfolioResponse(
                samplePersonalInfo(), List.of(java, react, mysql), List.of(), List.of()
        );
        when(portfolioService.getAll()).thenReturn(response);

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills.length()").value(3))
                .andExpect(jsonPath("$.skills[0].name").value("Java"))
                .andExpect(jsonPath("$.skills[1].name").value("React"))
                .andExpect(jsonPath("$.skills[2].name").value("MySQL"));
    }

    @Test
    void getPortfolio_returns200WithMultipleExperiences() throws Exception {
        Experience exp1 = new Experience();
        exp1.setId(1L);
        exp1.setCompany("Company A");
        exp1.setRole("Junior Dev");
        exp1.setDisplayOrder(1);

        Experience exp2 = new Experience();
        exp2.setId(2L);
        exp2.setCompany("Company B");
        exp2.setRole("Senior Dev");
        exp2.setDisplayOrder(2);

        PortfolioResponse response = new PortfolioResponse(
                samplePersonalInfo(), List.of(), List.of(exp1, exp2), List.of()
        );
        when(portfolioService.getAll()).thenReturn(response);

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.experiences.length()").value(2))
                .andExpect(jsonPath("$.experiences[0].company").value("Company A"))
                .andExpect(jsonPath("$.experiences[1].company").value("Company B"));
    }

    @Test
    void getPortfolio_returns200WithMultipleProjects() throws Exception {
        Project p1 = new Project(1L, "Project A", "Desc A", "Java", "", "", false, 1);
        Project p2 = new Project(2L, "Project B", "Desc B", "React", "", "", true, 2);

        PortfolioResponse response = new PortfolioResponse(
                samplePersonalInfo(), List.of(), List.of(), List.of(p1, p2)
        );
        when(portfolioService.getAll()).thenReturn(response);

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects.length()").value(2))
                .andExpect(jsonPath("$.projects[0].name").value("Project A"))
                .andExpect(jsonPath("$.projects[1].isFeatured").value(true));
    }

    @Test
    void getPortfolio_serviceThrowsException_returns500() throws Exception {
        when(portfolioService.getAll()).thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isInternalServerError());
    }
}
