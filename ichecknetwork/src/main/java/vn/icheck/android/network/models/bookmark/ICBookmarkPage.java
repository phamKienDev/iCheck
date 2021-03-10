package vn.icheck.android.network.models.bookmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ICBookmarkPage {

    @Expose
    @SerializedName("rows")
    public List<Rows> rows;
    @Expose
    @SerializedName("count")
    public long count;

    public static class Rows {
        @Expose
        @SerializedName("page")
        public Page page;
        @Expose
        @SerializedName("updated_at")
        public String updated_at;
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("page_id")
        public long page_id;
        @Expose
        @SerializedName("user_id")
        public long user_id;
        @Expose
        @SerializedName("id")
        public long id;
        @Expose
        @SerializedName("title")
        public Title title;
    }

    public static class Page {
        @Expose
        @SerializedName("product_count")
        public long product_count;
        @Expose
        @SerializedName("updated_at")
        public String updated_at;
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("verified")
        public boolean verified;
        @Expose
        @SerializedName("blocked")
        public boolean blocked;
        @Expose
        @SerializedName("scan_count")
        public long scan_count;
        @Expose
        @SerializedName("featured")
        public boolean featured;
        @Expose
        @SerializedName("phone")
        public String phone;
        @Expose
        @SerializedName("country_id")
        public long country_id;
        @Expose
        @SerializedName("address")
        public String address;
        @Expose
        @SerializedName("prefix")
        public String prefix;
        @Expose
        @SerializedName("avatar")
        public String avatar;
        @Expose
        @SerializedName("gln_code")
        public String gln_code;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public long id;
        @Expose
        @SerializedName("website")
        public String website;
        @Expose
        @SerializedName("email")
        public String email;
    }

    public static class Title {
        @Expose
        @SerializedName("id")
        public long id;
        @Expose
        @SerializedName("title")
        public String title;
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("updated_at")
        public String updated_at;
    }
}
