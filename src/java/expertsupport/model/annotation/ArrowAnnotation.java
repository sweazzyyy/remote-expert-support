package expertsupport.model.annotation;

/** Стрелка: направление от точки (x1, y1) к точке (x2, y2). */
public class ArrowAnnotation extends Annotation {

    public ArrowAnnotation(int id, double x1, double y1, double x2, double y2, String color) {
        super(id, x1, y1, x2, y2, color);
    }

    @Override
    public String getToolType() {
        return "arrow";
    }

    @Override
    public String describeGeometry() {
        return "(" + getX1() + ", " + getY1() + ") -> (" + getX2() + ", " + getY2() + ")";
    }

    @Override
    public double size() {
        return Math.hypot(getX2() - getX1(), getY2() - getY1());
    }
}
