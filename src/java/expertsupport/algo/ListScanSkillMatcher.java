package expertsupport.algo;

import expertsupport.model.Expert;
import expertsupport.model.Skill;

import java.util.List;

/**
 * Поиск каждого навыка линейным проходом по списку навыков эксперта —
 * так же, как `skill in expert["skills"]` в процедурной версии. O(m · s).
 */
public class ListScanSkillMatcher implements SkillMatcher {

    @Override
    public int countMatches(List<Skill> required, Expert expert) {
        List<Skill> expertSkills = expert.getSkills();
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
        return "O(n * m * s)";
    }
}
