package grocery.app.model;

public class SearchModel {

    String category_name;
    String id;
    String filter_id;
    String name;
    String product_image;
    String is_wishlisted;

    public String getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(String filter_id) {
        this.filter_id = filter_id;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_wishlisted() {
        return is_wishlisted;
    }

    public void setIs_wishlisted(String is_wishlisted) {
        this.is_wishlisted = is_wishlisted;
    }
}
