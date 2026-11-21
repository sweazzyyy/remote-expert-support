package expertsupport.algo;

import expertsupport.model.Expert;

/** Результат Алгоритма 1: выбранный эксперт и его оценка. */
public class MatchResult {
    private final Expert expert;
    private final double score;

    public MatchResult(Expert expert, double score) {
        this.expert = expert;
        this.score = score;
    }

    public Expert getExpert() {
        return expert;
    }

    public double getScore() {
        return score;
    }
}
