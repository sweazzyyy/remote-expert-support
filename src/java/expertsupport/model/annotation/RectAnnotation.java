package expertsupport.model.annotation;

/** Рамка: прямоугольник с противоположными углами (x1, y1) и (x2, y2). */
public class RectAnnotation extends Annotation {

    public RectAnnotation(int id, double x1, double y1, double x2, double y2, String color) {
        super(id, x1, y1, x2, y2, color);
    }

    @Override
    public String getToolType() {
        return "rect";
    }

    @Override
    public String describeGeometry() {
        return "рамка [" + getX1() + ", " + getY1() + "] x [" + getX2() + ", " + getY2() + "]";
    }

    @Override
    public double size() {
        return Math.abs(getX2() - getX1()) * Math.abs(getY2() - getY1());
    }
}
