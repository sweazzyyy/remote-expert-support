package expertsupport.model;

/**
 * Справочник PRIORITY: уровень приоритета и его вес.
 * В процедурной версии — функция get_priority_weight().
 */
public enum PriorityLevel {
    CRITICAL("critical", 100),
    HIGH("high", 75),
    MEDIUM("medium", 50),
    LOW("low", 25);

    private final String code;
    private final int weight;

    PriorityLevel(String code, int weight) {
        this.code = code;
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public int getWeight() {
        return weight;
    }

    /** Поиск уровня по коду; неизвестный код — исключение, как ValueError в Python. */
    public static PriorityLevel fromCode(String code) {
        for (PriorityLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Неизвестный уровень приоритета: " + code);
    }
}
