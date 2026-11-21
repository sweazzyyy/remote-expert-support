package expertsupport.algo;

import expertsupport.model.Expert;
import expertsupport.model.Skill;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Навыки эксперта кладутся во множество, проверка вхождения — O(1) в среднем.
 * Один вызов — O(m + s), алгоритм целиком — O(n · (m + s)).
 */
public class HashSetSkillMatcher implements SkillMatcher {

    @Override
    public int countMatches(List<Skill> required, Expert expert) {
        Set<Skill> expertSkills = new HashSet<>(expert.getSkills());
        int matched = 0;
        for (Skill skill : required) {
            if (expertSkills.contains(skill)) {
                matched++;
            }
        }
        return matched;
    }

    @Override
    public String complexity() {
        return "O(n * (m + s))";
    }
}
