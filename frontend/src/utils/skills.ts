import type { Skill } from '../types'

const preferredCategoryOrder = [
  'Languages',
  'Backend Development',
  'Databases',
  'Cloud & DevOps',
  'Distributed Systems',
  'AI-Assisted Development',
  'Testing & Tools',
  'Core Concepts',
]

export function groupSkillsByCategory(skills: Skill[]) {
  const grouped = new Map<string, Skill[]>()

  skills.forEach(skill => {
    const category = skill.category?.trim() || 'Other'
    const categorySkills = grouped.get(category) ?? []
    categorySkills.push(skill)
    grouped.set(category, categorySkills)
  })

  const categories = [...grouped.keys()].sort((left, right) => {
    const leftIndex = preferredCategoryOrder.indexOf(left)
    const rightIndex = preferredCategoryOrder.indexOf(right)
    if (leftIndex >= 0 || rightIndex >= 0) {
      if (leftIndex < 0) return 1
      if (rightIndex < 0) return -1
      if (leftIndex !== rightIndex) return leftIndex - rightIndex
    }
    return left.localeCompare(right)
  })

  return categories.map(category => ({
    category,
    skills: grouped.get(category)!.sort((left, right) =>
      right.proficiency - left.proficiency || left.name.localeCompare(right.name)),
  }))
}
