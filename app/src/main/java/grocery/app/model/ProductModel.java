package grocery.app.model;

import com.adoisstudio.helper.JsonList;

import org.json.JSONArray;

public class ProductModel {

    String id;
    String parent_id;
    String name;
    String image;
    String main_parent_id;
    String filter_id;
    String category_name;
    String product_image;
    String is_wishlisted;
    String price;
    String saleprice;
    String slug;
    String discount_amount;
    String discount;
    String variants_name;
    String externalFilterId;
    String externalFilterName;
    int position;
    String jsonArrayData;
    JsonList filter_option;

    public ProductModel() {

    }

    public String getExternalFilterName() {
        return externalFilterName;
    }

    public void setExternalFilterName(String externalFilterName) {
        this.externalFilterName = externalFilterName;
    }

    public String getSlug() {
        return slug;
    }

    public JsonList getFilter_option() {
        return filter_option;
    }

    public void setFilter_option(JsonList filter_option) {
        this.filter_option = filter_option;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getJsonArrayData() {
        return jsonArrayData;
    }

    public void setJsonArrayData(String jsonArrayData) {
        this.jsonArrayData = jsonArrayData;
    }

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public String getExternalFilterId() {
        return externalFilterId;
    }

    public void setExternalFilterId(String externalFilterId) {
        this.externalFilterId = externalFilterId;
    }

    public String getVariants_name() {
        return variants_name;
    }

    public void setVariants_name(String variants_name) {
        this.variants_name = variants_name;
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

    public String getIs_wishlisted() {
        return is_wishlisted;
    }

    public void setIs_wishlisted(String is_wishlisted) {
        this.is_wishlisted = is_wishlisted;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSaleprice() {
        return saleprice;
    }

    public void setSaleprice(String saleprice) {
        this.saleprice = saleprice;
    }

    public String getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(String discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
