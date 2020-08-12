package grocery.app.model;

public class ProductModel {

    String id;
    String parent_id;
    String name;
    String image;
    String main_parent_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMain_parent_id() {
        return main_parent_id;
    }

    public void setMain_parent_id(String main_parent_id) {
        this.main_parent_id = main_parent_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public ProductModel(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public ProductModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
