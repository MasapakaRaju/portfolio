export interface PersonalInfo {
  id: number;
  name: string;
  title: string;
  tagline: string;
  location: string;
  email: string;
  phone: string;
  githubUrl: string;
  linkedinUrl: string;
  leetcodeUrl: string;
  resumeUrl: string;
  about: string;
}

export interface Skill {
  id: number;
  name: string;
  category: string;
  proficiency: number;
}

export interface Experience {
  id: number;
  company: string;
  role: string;
  location: string;
  startDate: string;
  endDate: string | null;
  isCurrent: boolean;
  description: string;
  highlights: string[];
  displayOrder: number;
}

export interface Project {
  id: number;
  name: string;
  description: string;
  techStack: string;
  githubUrl: string;
  demoUrl: string;
  isFeatured: boolean;
  displayOrder: number;
}

export interface PortfolioData {
  personalInfo: PersonalInfo;
  skills: Skill[];
  experiences: Experience[];
  projects: Project[];
}

export interface ContactForm {
  name: string;
  email: string;
  phone: string;
  subject: string;
  message: string;
}
