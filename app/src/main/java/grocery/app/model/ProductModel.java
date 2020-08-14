package grocery.app.model;

public class ProductModel {

    String id;
    String parent_id;
    String name;
    String image;
    String main_parent_id;
    String filter_id;
    String category_name;
    String product_image;

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(String filter_id) {
        this.filter_id = filter_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

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
