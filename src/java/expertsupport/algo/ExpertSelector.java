package expertsupport.algo;

import expertsupport.model.Expert;
import expertsupport.model.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * АЛГОРИТМ 1 (операция 3 «Подобрать эксперта по профилю»).
 * В процедурной версии — функция assign_best_expert().
 * Способ подсчёта совпадений задаётся интерфейсом SkillMatcher.
 */
public class ExpertSelector {
    private final List<Expert> experts;
    private final SkillMatcher matcher;

    public ExpertSelector(List<Expert> experts, SkillMatcher matcher) {
        this.experts = new ArrayList<>(experts);
        this.matcher = matcher;
    }

    /**
     * score = совпавшие навыки · 100 + рейтинг; выбирается максимум.
     * Если активных экспертов нет или ни один навык не совпал — пустой результат.
     */
    public Optional<MatchResult> findBestExpert(List<Skill> requiredSkills) {
        Expert best = null;
        double maxScore = -1;

        for (Expert expert : experts) {
            if (!expert.isAvailable()) {
                continue;
            }
            int matched = matcher.countMatches(requiredSkills, expert);
            double score = matched * 100 + expert.getRating();
            if (score > maxScore) {
                maxScore = score;
                best = expert;
            }
        }

        if (best == null || maxScore < 100) {
            return Optional.empty();
        }
        return Optional.of(new MatchResult(best, maxScore));
    }

    public List<Expert> getExperts() {
        return Collections.unmodifiableList(experts);
    }

    public SkillMatcher getMatcher() {
        return matcher;
    }
}
