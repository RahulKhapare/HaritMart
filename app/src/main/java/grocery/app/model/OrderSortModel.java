package grocery.app.model;

public class OrderSortModel {

    int key;
    String value;

    public OrderSortModel(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public OrderSortModel() {

    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
