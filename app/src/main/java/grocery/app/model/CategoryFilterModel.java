package grocery.app.model;

import com.adoisstudio.helper.Json;

public class CategoryFilterModel {

    String id;
    String name;
    String filter_id;
    Json value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(String filter_id) {
        this.filter_id = filter_id;
    }

    public Json getValue() {
        return value;
    }

    public void setValue(Json value) {
        this.value = value;
    }
}
