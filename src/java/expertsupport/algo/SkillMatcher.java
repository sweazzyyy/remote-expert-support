package expertsupport.algo;

import expertsupport.model.Expert;
import expertsupport.model.Skill;

import java.util.List;

/**
 * Правило подсчёта совпавших навыков эксперта и инцидента.
 * Две реализации дают одинаковый результат, но разную сложность.
 */
public interface SkillMatcher {

    /** Сколько навыков из required есть у эксперта. */
    int countMatches(List<Skill> required, Expert expert);

    /** Оценка сложности одного вызова (для вывода). */
    String complexity();
}
