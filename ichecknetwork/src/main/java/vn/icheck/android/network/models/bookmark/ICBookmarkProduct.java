package vn.icheck.android.network.models.bookmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import vn.icheck.android.network.models.ICShop;

public class ICBookmarkProduct {

    @Expose
    @SerializedName("rows")
    public List<Rows> rows;
    @Expose
    @SerializedName("count")
    public int count;

    public static class Rows {
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("product")
        public Product product;
        @Expose
        @SerializedName("scan_address")
        public String scan_address;
        @Expose
        @SerializedName("scan_lng")
        public double scan_lng;
        @Expose
        @SerializedName("scan_lat")
        public double scan_lat;
        @Expose
        @SerializedName("price")
        public long price;
        @Expose
        @SerializedName("product_id")
        public long product_id;
        @Expose
        @SerializedName("user_id")
        public long user_id;
        @Expose
        @SerializedName("id")
        public long id;
        @Expose
        public ICShop shop;
        @Expose
        public Variant variant;
    }

    public static class Variant{
        @Expose
        public long id;
        @SerializedName("can_add_to_cart")
        public boolean canAddToCart;
        @SerializedName("price")
        public long price;
    }

    public static class Product {
        @Expose
        @SerializedName("thumbnails")
        public Thumbnails thumbnails;
        @Expose
        @SerializedName("image")
        public String image;
        @Expose
        @SerializedName("price")
        public long price;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("barcode")
        public String barcode;
        @Expose
        @SerializedName("id")
        public long id;
        @Expose
        public Float rating;
        @Expose
        public boolean verified;
    }

    public static class Thumbnails {
        @Expose
        @SerializedName("medium")
        public String medium;
        @Expose
        @SerializedName("small")
        public String small;
        @Expose
        @SerializedName("thumbnail")
        public String thumbnail;
        @Expose
        @SerializedName("square")
        public String square;
        @Expose
        @SerializedName("original")
        public String original;
    }
}
