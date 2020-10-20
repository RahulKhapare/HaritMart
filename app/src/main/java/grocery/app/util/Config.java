package grocery.app.util;

import com.adoisstudio.helper.Json;

import grocery.app.model.AddressModel;

public class Config {

    public static int CATEGORY_POSITION = 0;
    public static int SUB_CATEGORY_POSITION = 0;
    public static int CHILD_CATEGORY_POSITION = 0;
    public static String PRODUCT_IMAGE_PATH ;
    public static boolean FROM_HOME = false;
    public static AddressModel addressModel;

    public static String PARENT_POSITION = "PARENT_POSITION";
    public static String SUB_POSITION = "SUB_POSITION";
    public static String CHILD_POSITION = "CHILD_POSITION";
    public static String FROM_POSITION = "FROM_POSITION";
    public static String TITLE = "TITLE";
    public static String CHILD_JSON = "CHILD_JSON";
    public static String LOGIN_NUMBER = "LOGIN_NUMBER";
    public static String LOGIN_OTP = "LOGIN_OTP";
    public static String GET_CURRENT_LOCATION = "GET_CURRENT_LOCATION";
    public static String ADDRESS_LOCATION = "ADDRESS_LOCATION";
    public static String EDIT_ADDRESS = "EDIT_ADDRESS";
    public static String FOR_CHECKOUT = "FOR_CHECKOUT";

    public static String PRODUCT_ID = "PRODUCT_ID";
    public static String PRODUCT_FILTER_ID = "PRODUCT_FILTER_ID";
    public static String CHECK_CART_DATA = "CHECK_CART_DATA";

    public static boolean FROM_ADDRESS = false;
    public static String PUSH_NOTIFICATION = "pushNotification";
    public static String HOME = "HOME";
    public static String FAVORITE = "FAVORITE";
    public static String SEARCH = "SEARCH";
    public static String CART = "CART";
    public static String MORE = "MORE";
    public static boolean Update_Favorite_Home = false;
    public static boolean Update_Favorite_List = false;
    public static boolean Update_Favorite_Wish = false;
    public static boolean Update_Direct_Home = false;
    public static String currentFlag = "";

    public static String SpecialProductArrived = "Special Product";
    public static String NewProductArrived = "New Product";
    public static String TrendingProductArrived = "Trending Product";

    public static String GOOGLE_ADDRESS = "GOOGLE_ADDRESS";

    public static Json CART_JSON;

    public static int commonUserID = 0;
    public static int OPEN = 1;
    public static int SHARE = 2;
}
