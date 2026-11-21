package expertsupport.model.annotation;

import java.time.LocalDateTime;

/**
 * Сущность ANNOTATION — AR-метка на видеокадре.
 * Абстрактный класс: каждый инструмент (стрелка, рамка, линия) по-своему
 * описывает геометрию и считает свой размер.
 */
public abstract class Annotation {
    private final int id;
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;
    private final String color;
    private final LocalDateTime createdAt;

    protected Annotation(int id, double x1, double y1, double x2, double y2, String color) {
        if (!inFrame(x1) || !inFrame(y1) || !inFrame(x2) || !inFrame(y2)) {
            throw new IllegalArgumentException("Координаты метки должны быть в диапазоне [0; 1]");
        }
        this.id = id;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.createdAt = LocalDateTime.now();
    }

    /** Фабрика: создаёт метку нужного подкласса по коду инструмента (tool_type). */
    public static Annotation create(int id, String toolType, double x1, double y1, double x2, double y2,
                                    String color) {
        switch (toolType) {
            case "arrow":
                return new ArrowAnnotation(id, x1, y1, x2, y2, color);
            case "rect":
                return new RectAnnotation(id, x1, y1, x2, y2, color);
            case "line":
                return new LineAnnotation(id, x1, y1, x2, y2, color);
            default:
                throw new IllegalArgumentException("Неизвестный инструмент AR-метки: " + toolType);
        }
    }

    private static boolean inFrame(double v) {
        return v >= 0.0 && v <= 1.0;
    }

    /** Значение атрибута ANNOTATION.tool_type. */
    public abstract String getToolType();

    /** Текстовое описание геометрии метки. */
    public abstract String describeGeometry();

    /** Размер метки в долях кадра: длина для стрелки и линии, площадь для рамки. */
    public abstract double size();

    public int getId() {
        return id;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public String getColor() {
        return color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
