package vn.icheck.android.network.models.bookmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ICBookmarkShop {

    @Expose
    @SerializedName("rows")
    public List<Rows> rows;
    @Expose
    @SerializedName("count")
    public int count;

    public class Rows {
        @Expose
        @SerializedName("updated_at")
        public String updated_at;
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("shop")
        public Shop shop;
        @Expose
        @SerializedName("shop_id")
        public long shop_id;
        @Expose
        @SerializedName("user_id")
        public long user_id;
        @Expose
        @SerializedName("id")
        public long id;
    }

    public class Shop {
        @Expose
        @SerializedName("page_id")
        public long page_id;
        @Expose
        @SerializedName("hide_all_product")
        public boolean hide_all_product;
        @Expose
        @SerializedName("cover_thumbnails")
        public Cover_thumbnails cover_thumbnails;
        @Expose
        @SerializedName("avatar_thumbnails")
        public Avatar_thumbnails avatar_thumbnails;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("sales")
        public int sales;
        @Expose
        @SerializedName("review_count")
        public int review_count;
        @Expose
        @SerializedName("rating")
        public double rating;
        @Expose
        @SerializedName("updated_at")
        public String updated_at;
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("is_offline")
        public boolean is_offline;
        @Expose
        @SerializedName("is_online")
        public boolean is_online;
        @Expose
        @SerializedName("location")
        public Location location;
        @Expose
        @SerializedName("verified")
        public Boolean verified;
        @Expose
        @SerializedName("blocked_reason")
        public String blocked_reason;
        @Expose
        @SerializedName("blocked")
        public boolean blocked;
        @Expose
        @SerializedName("ward_id")
        public long ward_id;
        @Expose
        @SerializedName("district_id")
        public long district_id;
        @Expose
        @SerializedName("city_id")
        public long city_id;
        @Expose
        @SerializedName("country_id")
        public int country_id;
        @Expose
        @SerializedName("address")
        public String address;
        @Expose
        @SerializedName("email")
        public String email;
        @Expose
        @SerializedName("phone")
        public String phone;
        @Expose
        @SerializedName("cover")
        public String cover;
        @Expose
        @SerializedName("avatar")
        public String avatar;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public long id;
    }

    public  class Cover_thumbnails {
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

    public  class Avatar_thumbnails {
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

    public  class Location {
        @Expose
        @SerializedName("lon")
        public double lon;
        @Expose
        @SerializedName("lat")
        public double lat;
    }
}
