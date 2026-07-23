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
        info.setTitle("Software Engineer");
        info.setTagline("Building high-throughput backend systems with Java, Spring Boot, event-driven design, and AI-assisted engineering workflows.");
        info.setLocation("Hyderabad, Telangana, India");
        info.setEmail("masapakaraju28@gmail.com");
        info.setPhone("+91-9515281863");
        info.setGithubUrl("https://github.com/MasapakaRaju");
        info.setLinkedinUrl("https://linkedin.com/in/raju-m-588343250/");
        info.setLeetcodeUrl("https://leetcode.com/u/rajumasapaka/");
        info.setResumeUrl("#");
        info.setAbout(
                "Software Engineer with 1+ year of experience designing and building scalable, distributed backend systems using\n" +
                        "Java and Spring Boot, with a focus on enterprise-grade trust, security remediation (SOC2), and planet-scale reliability.\n" +
                        "Strong computer science fundamentals in algorithms, data structures, and system design. Experienced in cloud-native\n" +
                        "deployments, CI/CD pipelines, and collaborating with cross-functional teams to deliver end-to-end production solutions. AInative engineer who actively uses agentic tools (Claude Code, GitHub Copilot) as a core part of the development workflow —\n" +
                        "treating AI as a high-trust collaborator to rapidly test ideas and accelerate impact. Experimental mindset with a passion for\n" +
                        "building platforms that power data at scale."
        );
        personalInfoRepository.save(info);
    }

    private void seedSkills() {
        List<Skill> skills = Arrays.asList(
// Languages
                new Skill(null, "Java", "Languages", 92),
                new Skill(null, "SQL", "Languages", 88),
                new Skill(null, "Python", "Languages", 75),
                new Skill(null, "JavaScript", "Languages", 72),

                // Backend Development
                new Skill(null, "Spring Boot", "Backend Development", 90),
                new Skill(null, "REST APIs", "Backend Development", 90),
                new Skill(null, "Microservices", "Backend Development", 85),
                new Skill(null, "Spring Data JPA", "Backend Development", 84),
                new Skill(null, "Hibernate", "Backend Development", 82),
                new Skill(null, "JWT Authentication", "Backend Development", 82),

                // Databases
                new Skill(null, "PostgreSQL", "Databases", 86),
                new Skill(null, "MySQL", "Databases", 86),
                new Skill(null, "SQL Query Optimization", "Databases", 84),
                new Skill(null, "Redis", "Databases", 70),

                // Cloud & DevOps
                new Skill(null, "Docker", "Cloud & DevOps", 84),
                new Skill(null, "GitHub Actions", "Cloud & DevOps", 82),
                new Skill(null, "CI/CD", "Cloud & DevOps", 82),
                new Skill(null, "GitHub Container Registry", "Cloud & DevOps", 80),
                new Skill(null, "Render", "Cloud & DevOps", 78),
                new Skill(null, "Kubernetes", "Cloud & DevOps", 76),

                // Messaging & Distributed Systems
                new Skill(null, "Apache Kafka", "Distributed Systems", 72),
                new Skill(null, "Multithreading", "Distributed Systems", 84),
                new Skill(null, "Concurrent Programming", "Distributed Systems", 82),

                // AI Developer Tools
                new Skill(null, "Claude Code", "AI-Assisted Development", 88),
                new Skill(null, "ChatGPT", "AI-Assisted Development", 88),
                new Skill(null, "GitHub Copilot", "AI-Assisted Development", 86),
                new Skill(null, "AI Code Review", "AI-Assisted Development", 84),

                // Testing & Development Tools
                new Skill(null, "JUnit 5", "Testing & Tools", 82),
                new Skill(null, "Mockito", "Testing & Tools", 80),
                new Skill(null, "Postman", "Testing & Tools", 84),
                new Skill(null, "Git", "Testing & Tools", 88),
                new Skill(null, "Maven", "Testing & Tools", 86),
                new Skill(null, "IntelliJ IDEA", "Testing & Tools", 90),

                //computer science
                new Skill(null, "Object-Oriented Programming", "Core Concepts", 92),
                new Skill(null, "Data Structures", "Core Concepts", 86),
                new Skill(null, "Algorithms", "Core Concepts", 84),
                new Skill(null, "DBMS", "Core Concepts", 84),
                new Skill(null, "Operating Systems", "Core Concepts", 80),
                new Skill(null, "Computer Networks", "Core Concepts", 80),
                new Skill(null, "Design Patterns", "Core Concepts", 82),
                new Skill(null, "SOLID Principles", "Core Concepts", 84),
                new Skill(null, "Exception Handling", "Core Concepts", 86),
                new Skill(null, "Collections Framework", "Core Concepts", 90),
                new Skill(null, "Concurrency", "Core Concepts", 82),
                new Skill(null, "Memory Management", "Core Concepts", 76),
                new Skill(null, "MVC Architecture", "Core Concepts", 84),
                new Skill(null, "API Design", "Core Concepts", 86)
        );
        skillRepository.saveAll(skills);
    }

    private void seedExperience() {
        Experience exp2 = new Experience();
        exp2.setCompany("OpenText");
        exp2.setRole("Associate Engineer");
        exp2.setLocation("Bangalore, India");
        exp2.setStartDate("Aug 2025");
        exp2.setEndDate("Mar 2026");
        exp2.setIsCurrent(false);
        exp2.setDescription("Owned backend API and microservice delivery across design, coding, testing, deployment, optimization, and production readiness.");
        exp2.setHighlights(Arrays.asList("Architected 12+ enterprise-grade microservices using Java 17 and Spring Boot, ensuring high availability and reliability for distributed data management platforms.",
                "Implemented AI-native engineering workflows using GitHub Copilot and Claude to automate unit test generation and refactor legacy code, increasing sprint velocity by 25%.",
                "Optimized P99 API response latency by 35% through SQL query refactoring and Redis caching, handling highconcurrency workloads in production environments.",
                "Spearheaded security remediation for 15+ critical vulnerabilities identified via Black Duck, ensuring 100% compliance with SOC2 and enterprise security standards.",
                "Orchestrated containerized deployments via Docker and Kubernetes, maintaining CI/CD pipeline health and reducing deployment preparation time by 30%.",
                "Leveraged Claude Code and GitHub Copilot as core AI-native development tools — rapidly experimenting with agentic workflows to deliver results faster and more effectively."
        ));
        exp2.setDisplayOrder(1);

        Experience exp3 = new Experience();
        exp3.setCompany("OpenText");
        exp3.setRole("Engineering Intern");
        exp3.setLocation("Bangalore, India");
        exp3.setStartDate("Mar 2025");
        exp3.setEndDate("Aug 2025");
        exp3.setIsCurrent(false);
        exp3.setDescription("Automated internal workflows and improved UI testing reliability for engineering teams.");
        exp3.setHighlights(Arrays.asList(
                "Automated 10+ internal operational workflows by developing Java-based automation scripts, reducing manual\n" +
                        "engineering effort by 15+ hours per week.\n" +
                        "Engineered an automated UI testing suite using Playwright, increasing regression test coverage by 40% and reducing\n" +
                        "execution time by 30%."));
        exp3.setDisplayOrder(2);

        experienceRepository.saveAll(Arrays.asList(exp2, exp3));
    }

    private void seedProjects() {
        Project p1 = new Project();
        p1.setName("Portfolio");
        p1.setDescription(
                "Full-stack portfolio platform powered by a Spring Boot REST API, React + TypeScript frontend, " +
                        "PostgreSQL persistence, Docker builds Image and containerize in GHCR, Render backend deployment, and Github pages for  frontend delivery."
        );
        p1.setTechStack("Spring Boot,React,TypeScript,PostgreSQL,Docker,Render,Github Pages");
        p1.setGithubUrl("https://github.com/MasapakaRaju/portfolio");
        p1.setDemoUrl("https://masapakaraju.github.io/portfolio/");
        p1.setIsFeatured(true);
        p1.setDisplayOrder(1);

        Project p2 = new Project();
        p2.setName("REST API Filtering Engine");
        p2.setDescription(
                "Designed a dynamic filtering and pagination backend service for 10K+ structured records, " +
                        "focused on scalable request handling, optimized SQL queries, and high-throughput API performance."
        );
        p2.setTechStack("Spring Boot,Java,REST APIs,SQL Optimization,Pagination,Backend Performance");
        p2.setGithubUrl("https://github.com/MasapakaRaju");
        p2.setDemoUrl("#");
        p2.setIsFeatured(true);
        p2.setDisplayOrder(2);

        Project p3 = new Project();
        p3.setName("Task Management Web Application");
        p3.setDescription(
                "Built a full-stack task management platform with authentication, CRUD operations, " +
                        "and RESTful API integration designed for 50+ simulated users."
        );
        p3.setTechStack("MongoDB,Express,React,Node.js,REST APIs,Authentication,CRUD");
        p3.setGithubUrl("https://github.com/MasapakaRaju");
        p3.setDemoUrl("#");
        p3.setIsFeatured(true);
        p3.setDisplayOrder(3);

        Project p4 = new Project();
        p4.setName("Flappy Bird Game Development");
        p4.setDescription(
                "Built an interactive desktop game using Java AWT and Swing, applying event-driven architecture, " +
                        "real-time processing, collision logic, and responsive keyboard controls."
        );
        p4.setTechStack("Java,AWT,Swing,Event-Driven Architecture,Real-Time Processing");
        p4.setGithubUrl("https://github.com/MasapakaRaju");
        p4.setDemoUrl("#");
        p4.setIsFeatured(false);
        p4.setDisplayOrder(4);

        projectRepository.saveAll(Arrays.asList(p1, p2, p3, p4));
    }
}
