package grocery.app.model;

public class CartModel {

    String id;
    String temp_id;
    String product_id;
    String products_variants_id;
    String qty;
    String option1;
    String option2;
    String option3;
    String price;
    String total_price;
    String coupon_discount_amount;
    String cart_image;
    String name;

    public CartModel(String id, String product_id, String qty,String total_price, String price, String cart_image, String name) {
        this.id = id;
        this.product_id = product_id;
        this.qty = qty;
        this.total_price = total_price;
        this.price = price;
        this.cart_image = cart_image;
        this.name = name;
    }

    public CartModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemp_id() {
        return temp_id;
    }

    public void setTemp_id(String temp_id) {
        this.temp_id = temp_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProducts_variants_id() {
        return products_variants_id;
    }

    public void setProducts_variants_id(String products_variants_id) {
        this.products_variants_id = products_variants_id;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getCoupon_discount_amount() {
        return coupon_discount_amount;
    }

    public void setCoupon_discount_amount(String coupon_discount_amount) {
        this.coupon_discount_amount = coupon_discount_amount;
    }

    public String getCart_image() {
        return cart_image;
    }

    public void setCart_image(String cart_image) {
        this.cart_image = cart_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
