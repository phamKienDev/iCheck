package vn.icheck.android.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ICListGift {

    @Expose
    @SerializedName("rows")
    private List<Rows> rows;
    @Expose
    @SerializedName("count")
    private int count;

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static class Rows {
        @Expose
        @SerializedName("remain")
        private int remain;
        @Expose
        @SerializedName("price")
        private int price;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("id")
        private int id;

        public int getRemain() {
            return remain;
        }

        public void setRemain(int remain) {
            this.remain = remain;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
