import { describe, expect, it } from 'vitest'
import { groupSkillsByCategory } from '../utils/skills'
import type { Skill } from '../types'

const skills: Skill[] = [
  { id: 1, name: 'TypeScript', category: 'Other tools', proficiency: 70 },
  { id: 2, name: 'Java', category: 'Languages', proficiency: 88 },
  { id: 3, name: 'Python', category: 'Languages', proficiency: 75 },
  { id: 4, name: 'Spring Boot', category: 'Backend Development', proficiency: 92 },
]

describe('groupSkillsByCategory', () => {
  it('groups every skill by category and sorts each group by descending proficiency', () => {
    expect(groupSkillsByCategory(skills)).toEqual([
      { category: 'Languages', skills: [skills[1], skills[2]] },
      { category: 'Backend Development', skills: [skills[3]] },
      { category: 'Other tools', skills: [skills[0]] },
    ])
  })
})
