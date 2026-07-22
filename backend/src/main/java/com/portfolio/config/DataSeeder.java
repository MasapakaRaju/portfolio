package com.portfolio.config;

import com.portfolio.model.Experience;
import com.portfolio.model.PersonalInfo;
import com.portfolio.model.Project;
import com.portfolio.model.Skill;
import com.portfolio.repository.ExperienceRepository;
import com.portfolio.repository.PersonalInfoRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PersonalInfoRepository personalInfoRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;

    @Override
    public void run(String... args) {
        personalInfoRepository.deleteAll();
        skillRepository.deleteAll();
        experienceRepository.deleteAll();
        projectRepository.deleteAll();

        seedPersonalInfo();
        seedSkills();
        seedExperience();
        seedProjects();
    }

    private void seedPersonalInfo() {
        PersonalInfo info = new PersonalInfo();
        info.setName("Masapaka Raju");
        info.setTitle("Java Backend Engineer");
        info.setTagline("Building high-throughput backend systems with Java, Spring Boot, event-driven design, and AI-assisted engineering workflows.");
        info.setLocation("Hyderabad, Telangana, India");
        info.setEmail("masapakaraju28@gmail.com");
        info.setPhone("+91 9515281863");
        info.setGithubUrl("https://github.com/MasapakaRaju");
        info.setLinkedinUrl("https://linkedin.com/in/raju-m-588343250/");
        info.setLeetcodeUrl("https://leetcode.com/u/rajumasapaka/");
        info.setResumeUrl("#");
        info.setAbout(
            "Java Backend Engineer with 1+ year of experience owning the full development lifecycle for scalable, " +
            "high-throughput backend systems using Java, Spring Boot, RESTful services, and SQL optimization. " +
            "Experienced in microservices, event processing, multithreading, system integration, Docker/Kubernetes deployments, " +
            "and production AI coding workflows with Claude Code, GitHub Copilot, and ChatGPT. Strong foundation in OOP, " +
            "DBMS, data structures, algorithms, enterprise integration patterns, and performance-focused backend design."
        );
        personalInfoRepository.save(info);
    }

    private void seedSkills() {
        List<Skill> skills = Arrays.asList(
            new Skill(null, "Java", "Languages", 92),
            new Skill(null, "Python", "Languages", 72),
            new Skill(null, "SQL", "Languages", 86),
            new Skill(null, "JavaScript", "Languages", 72),

            new Skill(null, "Spring Boot", "Backend", 90),
            new Skill(null, "RESTful/Web Services", "Backend", 90),
            new Skill(null, "Microservices", "Backend", 84),
            new Skill(null, "MVC Architecture", "Backend", 82),
            new Skill(null, "System Integration", "Backend", 82),
            new Skill(null, "Software Design", "Backend", 80),

            new Skill(null, "Multithreading", "Performance", 84),
            new Skill(null, "Event Processing", "Performance", 82),
            new Skill(null, "Distributed Systems", "Performance", 78),
            new Skill(null, "Low-Latency Design", "Performance", 76),
            new Skill(null, "High-Throughput APIs", "Performance", 84),

            new Skill(null, "MySQL", "Databases", 86),
            new Skill(null, "PostgreSQL", "Databases", 84),
            new Skill(null, "SQL Query Optimization", "Databases", 86),
            new Skill(null, "Redis", "Databases", 62),
            new Skill(null, "Database Management", "Databases", 80),

            new Skill(null, "Docker", "Cloud & DevOps", 82),
            new Skill(null, "Kubernetes", "Cloud & DevOps", 76),
            new Skill(null, "CI/CD Pipelines", "Cloud & DevOps", 76),
            new Skill(null, "Jenkins", "Cloud & DevOps", 72),
            new Skill(null, "Kafka", "Cloud & DevOps", 62),

            new Skill(null, "Claude Code", "AI Workflows", 88),
            new Skill(null, "GitHub Copilot", "AI Workflows", 84),
            new Skill(null, "ChatGPT", "AI Workflows", 86),
            new Skill(null, "AI Code Review", "AI Workflows", 82),

            new Skill(null, "JUnit", "Testing & Tools", 78),
            new Skill(null, "TestNG", "Testing & Tools", 74),
            new Skill(null, "Postman", "Testing & Tools", 82),
            new Skill(null, "Git", "Testing & Tools", 86),
            new Skill(null, "Maven", "Testing & Tools", 84),
            new Skill(null, "IntelliJ IDEA", "Testing & Tools", 86),

            new Skill(null, "OOP", "Core Concepts", 92),
            new Skill(null, "Data Structures", "Core Concepts", 84),
            new Skill(null, "Algorithms", "Core Concepts", 82),
            new Skill(null, "DBMS", "Core Concepts", 84),
            new Skill(null, "Exception Handling", "Core Concepts", 84)
        );
        skillRepository.saveAll(skills);
    }

    private void seedExperience() {
        Experience exp1 = new Experience();
        exp1.setCompany("OpenText");
        exp1.setRole("Associate Engineer");
        exp1.setLocation("Bangalore, India");
        exp1.setStartDate("Aug 2025");
        exp1.setEndDate("Mar 2026");
        exp1.setIsCurrent(false);
        exp1.setDescription("Owned backend API and microservice delivery across design, coding, testing, deployment, optimization, and production readiness.");
        exp1.setHighlights(Arrays.asList(
            "Owned the full development lifecycle for 12+ scalable RESTful APIs and microservices using Java and Spring Boot",
            "Integrated Claude Code, GitHub Copilot, and ChatGPT into planning, technical design, code generation, testing, and code/security review workflows",
            "Built backend infrastructure for high-volume data processing and request workflows using multithreading and event-driven patterns",
            "Resolved 15+ system bottlenecks and security vulnerabilities through Black Duck analysis, improving reliability and dependency compliance",
            "Optimized 20+ SQL queries and backend workflows to reduce API response latency across high-throughput services",
            "Supported Docker and Kubernetes cloud-native deployments with CI/CD pipelines for high-availability backend infrastructure"
        ));
        exp1.setDisplayOrder(1);

        Experience exp2 = new Experience();
        exp2.setCompany("OpenText");
        exp2.setRole("Engineering Intern");
        exp2.setLocation("Bangalore, India");
        exp2.setStartDate("Mar 2025");
        exp2.setEndDate("Aug 2025");
        exp2.setIsCurrent(false);
        exp2.setDescription("Automated internal workflows and improved UI testing reliability for engineering teams.");
        exp2.setHighlights(Arrays.asList(
            "Automated 10+ internal operational workflows end-to-end, reducing repetitive manual effort using Java backend services",
            "Streamlined UI testing pipelines using Playwright, improving execution consistency and reducing testing time by 30%"
        ));
        exp2.setDisplayOrder(2);

        experienceRepository.saveAll(Arrays.asList(exp1, exp2));
    }

    private void seedProjects() {
        Project p1 = new Project();
        p1.setName("REST API Filtering Engine");
        p1.setDescription(
            "Designed a dynamic filtering and pagination backend service for 10K+ structured records, " +
            "focused on scalable request handling, optimized SQL queries, and high-throughput API performance."
        );
        p1.setTechStack("Spring Boot,Java,REST APIs,SQL Optimization,Pagination,Backend Performance");
        p1.setGithubUrl("https://github.com/MasapakaRaju");
        p1.setDemoUrl("#");
        p1.setIsFeatured(true);
        p1.setDisplayOrder(1);

        Project p2 = new Project();
        p2.setName("Task Management Web Application");
        p2.setDescription(
            "Built a full-stack task management platform with authentication, CRUD operations, " +
            "and RESTful API integration designed for 50+ simulated users."
        );
        p2.setTechStack("MongoDB,Express,React,Node.js,REST APIs,Authentication,CRUD");
        p2.setGithubUrl("https://github.com/MasapakaRaju");
        p2.setDemoUrl("#");
        p2.setIsFeatured(true);
        p2.setDisplayOrder(2);

        Project p3 = new Project();
        p3.setName("Flappy Bird Game Development");
        p3.setDescription(
            "Built an interactive desktop game using Java AWT and Swing, applying event-driven architecture, " +
            "real-time processing, collision logic, and responsive keyboard controls."
        );
        p3.setTechStack("Java,AWT,Swing,Event-Driven Architecture,Real-Time Processing");
        p3.setGithubUrl("https://github.com/MasapakaRaju");
        p3.setDemoUrl("#");
        p3.setIsFeatured(false);
        p3.setDisplayOrder(3);

        Project p4 = new Project();
        p4.setName("Portfolio");
        p4.setDescription(
            "Full-stack portfolio platform powered by a Spring Boot REST API, React + TypeScript frontend, " +
            "PostgreSQL persistence, Docker builds, Render backend deployment, and Netlify frontend delivery."
        );
        p4.setTechStack("Spring Boot,React,TypeScript,PostgreSQL,Docker,Render,Netlify");
        p4.setGithubUrl("https://github.com/MasapakaRaju/my-portfolio");
        p4.setDemoUrl("#");
        p4.setIsFeatured(true);
        p4.setDisplayOrder(4);

        projectRepository.saveAll(Arrays.asList(p1, p2, p3, p4));
    }
}
