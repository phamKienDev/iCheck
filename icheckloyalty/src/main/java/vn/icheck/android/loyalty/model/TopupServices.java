package vn.icheck.android.loyalty.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopupServices {

    @Expose
    @SerializedName("rows")
    public List<Service> rows;
    @Expose
    @SerializedName("count")
    public int count;

    public class Service {
        @Expose
        @SerializedName("denomination")
        public List<String> denomination;
        @Expose
        @SerializedName("avatar")
        public String avatar;
        @Expose
        @SerializedName("provider")
        public String provider;
        @Expose
        @SerializedName("code")
        public String code;
        @Expose
        @SerializedName("type_name")
        public String type_name;
        @Expose
        @SerializedName("type")
        public String type;
        @Expose
        @SerializedName("id")
        public int id;

        @Expose
        @SerializedName("serviceId")
        public long serviceId;
    }
}