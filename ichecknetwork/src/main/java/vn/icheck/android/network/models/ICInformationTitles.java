package vn.icheck.android.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ICInformationTitles {

    @Expose
    @SerializedName("rows")
    public List<InfoTitle> rows;
    @Expose
    @SerializedName("count")
    public int count;

    public class InfoTitle {
        @Expose
        @SerializedName("title")
        public String title;
        @Expose
        @SerializedName("id")
        public int id;
    }
}
