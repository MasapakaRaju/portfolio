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
        info.setTitle("Backend Engineer");
        info.setTagline("Building reliable, high-performance enterprise applications");
        info.setLocation("India");
        info.setEmail("masapakaraju28@gmail.com");
        info.setPhone("+91 9515281863");
        info.setGithubUrl("https://github.com/MasapakaRaju");
        info.setLinkedinUrl("https://linkedin.com/in/raju-m-588343250/");
        info.setResumeUrl("#");
        info.setAbout(
            "Backend Engineer with experience in designing and developing scalable REST APIs " +
            "using Spring Boot and Java. Strong expertise in building microservices-based " +
            "applications, working with SQL databases, and applying object-oriented design " +
            "principles. Experienced in Agile development environments, cloud-based architectures, " +
            "and distributed systems. Passionate about building reliable, high-performance " +
            "enterprise applications. " +
            "Achievements: Solved 100+ DSA problems on LeetCode · A Grade in AMCAT · " +
            "HackerRank Java & SQL Certifications · B.Tech IT, CVR College of Engineering (CGPA: 7.7)"
        );
        personalInfoRepository.save(info);
    }

    private void seedSkills() {
        List<Skill> skills = Arrays.asList(
            // Backend
            new Skill(null, "Java", "Backend", 90),
            new Skill(null, "Spring Boot", "Backend", 85),
            new Skill(null, "RESTful APIs", "Backend", 85),
            new Skill(null, "Microservices", "Backend", 80),
            // Frontend
            new Skill(null, "React", "Frontend", 65),
            new Skill(null, "Angular", "Frontend", 60),
            // Database
            new Skill(null, "PostgreSQL", "Database", 80),
            new Skill(null, "MySQL", "Database", 80),
            new Skill(null, "Database Design", "Database", 75),
            // Testing
            new Skill(null, "JUnit", "Testing", 75),
            new Skill(null, "TestNG", "Testing", 70),
            // DevOps & Tools
            new Skill(null, "Docker", "DevOps", 80),
            new Skill(null, "Kubernetes", "DevOps", 75),
            new Skill(null, "Git", "DevOps", 85),
            new Skill(null, "Jenkins", "DevOps", 70),
            new Skill(null, "Maven", "DevOps", 80),
            // Core
            new Skill(null, "OOP", "Core Concepts", 90),
            new Skill(null, "Data Structures", "Core Concepts", 80),
            new Skill(null, "System Design", "Core Concepts", 75),
            new Skill(null, "Distributed Systems", "Core Concepts", 75),
            new Skill(null, "Linux/Unix", "Core Concepts", 75)
        );
        skillRepository.saveAll(skills);
    }

    private void seedExperience() {
        Experience exp1 = new Experience();
        exp1.setCompany("OpenText");
        exp1.setRole("Associate Engineer");
        exp1.setLocation("India");
        exp1.setStartDate("Aug 2025");
        exp1.setEndDate("Mar 2026");
        exp1.setIsCurrent(false);
        exp1.setDescription("Developed scalable enterprise applications in the Micro Focus ZENworks system using Spring Boot microservices.");
        exp1.setHighlights(Arrays.asList(
            "Developed scalable RESTful APIs using Spring Boot following microservices architecture and enterprise design patterns",
            "Analysed and fixed security vulnerabilities flagged by Black Duck Software Composition Analysis in third-party dependencies, ensuring license compliance",
            "Developed a dynamic filtering API for patch data using user-defined parameters, optimizing database queries and improving accuracy of results in the ZENworks system",
            "Performed system debugging and root cause analysis in distributed environments, improving system reliability",
            "Containerized applications using Docker and worked with Kubernetes for deployment in cloud-based environments",
            "Collaborated with cross-functional teams in an Agile environment to deliver features and enhancements"
        ));
        exp1.setDisplayOrder(1);

        Experience exp2 = new Experience();
        exp2.setCompany("OpenText (XCP Product)");
        exp2.setRole("Engineering Intern");
        exp2.setLocation("India");
        exp2.setStartDate("Mar 2025");
        exp2.setEndDate("Aug 2025");
        exp2.setIsCurrent(false);
        exp2.setDescription("Worked on the XCP product team automating internal workflows and UI processes.");
        exp2.setHighlights(Arrays.asList(
            "Developed automation scripts to streamline internal workflows, reducing manual intervention and improving process efficiency",
            "Automated repetitive UI processes using Playwright, reducing manual effort and improving operational efficiency"
        ));
        exp2.setDisplayOrder(2);

        experienceRepository.saveAll(Arrays.asList(exp1, exp2));
    }

    private void seedProjects() {
        Project p1 = new Project();
        p1.setName("IoT-Based Object Detection System");
        p1.setDescription(
            "Designed and implemented an event-driven system for real-time object detection. " +
            "Demonstrates understanding of distributed system concepts including event streaming, " +
            "asynchronous processing, and IoT device integration."
        );
        p1.setTechStack("IoT,Event-Driven Architecture,Distributed Systems,Real-Time Processing");
        p1.setGithubUrl("https://github.com/MasapakaRaju");
        p1.setDemoUrl("#");
        p1.setIsFeatured(true);
        p1.setDisplayOrder(1);

        Project p2 = new Project();
        p2.setName("Task Management Web Application");
        p2.setDescription(
            "Full-stack task management app built with the MERN stack featuring user authentication, " +
            "CRUD operations, and a dynamic frontend interface. Includes REST API design and " +
            "role-based access control."
        );
        p2.setTechStack("MongoDB,Express,React,Node.js,REST APIs,JWT");
        p2.setGithubUrl("https://github.com/MasapakaRaju");
        p2.setDemoUrl("#");
        p2.setIsFeatured(true);
        p2.setDisplayOrder(2);

        Project p3 = new Project();
        p3.setName("Portfolio Website");
        p3.setDescription(
            "This portfolio — a full-stack application with a Spring Boot REST API backend, " +
            "React + TypeScript frontend, and PostgreSQL database. Containerized with Docker " +
            "and orchestrated with Kubernetes."
        );
        p3.setTechStack("Spring Boot,Java,React,TypeScript,PostgreSQL,Docker,Kubernetes");
        p3.setGithubUrl("https://github.com/MasapakaRaju");
        p3.setDemoUrl("#");
        p3.setIsFeatured(true);
        p3.setDisplayOrder(3);

        projectRepository.saveAll(Arrays.asList(p1, p2, p3));
    }
}
