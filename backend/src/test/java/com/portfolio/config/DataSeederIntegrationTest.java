package com.portfolio.config;

import com.portfolio.repository.ExperienceRepository;
import com.portfolio.repository.PersonalInfoRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.repository.SkillRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "app.slack.webhook-url="
})
class DataSeederIntegrationTest {

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void contextLoadsAndSeederPopulatesData() {
        assertFalse(personalInfoRepository.findAll().isEmpty(), "PersonalInfo should be seeded");
        assertFalse(skillRepository.findAll().isEmpty(), "Skills should be seeded");
        assertFalse(experienceRepository.findAll().isEmpty(), "Experiences should be seeded");
        assertFalse(projectRepository.findAll().isEmpty(), "Projects should be seeded");
    }

    @Test
    void experienceHighlightsArePersisted() {
        var experiences = experienceRepository.findAll();
        long highlightCount = experiences.stream()
                .flatMap(e -> e.getHighlights().stream())
                .count();
        assertTrue(highlightCount > 0, "Experience highlights should be persisted without varchar errors");
    }
}
