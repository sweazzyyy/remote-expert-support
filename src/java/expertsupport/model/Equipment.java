package expertsupport.model;

/** Сущность EQUIPMENT — единица обслуживаемого оборудования. */
public class Equipment {
    private final int id;
    private final String serialNum;
    private final String model;
    private final String location;

    public Equipment(int id, String serialNum, String model, String location) {
        this.id = id;
        this.serialNum = serialNum;
        this.model = model;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public String getModel() {
        return model;
    }

    public String getLocation() {
        return location;
    }
}
