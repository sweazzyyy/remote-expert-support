package expertsupport.model;

/**
 * Сущность USER. Абстрактный базовый класс для ролей системы.
 * Подклассы различаются поведением: роль, доступность для назначения.
 */
public abstract class User {
    private final int id;
    private final String fullName;
    private final String email;

    protected User(int id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    /** Значение атрибута USER.role. */
    public abstract String getRole();

    /** Может ли пользователь сейчас участвовать в сеансе. По умолчанию — да. */
    public boolean isAvailable() {
        return true;
    }
}
