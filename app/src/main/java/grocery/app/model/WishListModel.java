package grocery.app.model;

public class WishListModel {

    String vegImage;
    String price;
    String discountstrike;
    String offprice;
    String vegSize;
    String weight;

    public WishListModel(String vegImage, String price, String discountstrike, String offprice, String vegSize, String weight) {
        this.vegImage  = vegImage;
        this.price  = price;
        this.discountstrike  = discountstrike;
        this.offprice  = offprice;
        this.vegSize  = vegSize;
        this.weight  = weight;
    }

    public String getVegImage() {
        return vegImage;
    }

    public void setVegImage(String vegImage) {
        this.vegImage = vegImage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountstrike() {
        return discountstrike;
    }

    public void setDiscountstrike(String discountstrike) {
        this.discountstrike = discountstrike;
    }

    public String getOffprice() {
        return offprice;
    }

    public void setOffprice(String offprice) {
        this.offprice = offprice;
    }

    public String getVegSize() {
        return vegSize;
    }

    public void setVegSize(String vegSize) {
        this.vegSize = vegSize;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
