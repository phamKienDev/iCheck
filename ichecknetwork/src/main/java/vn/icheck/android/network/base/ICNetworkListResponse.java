package vn.icheck.android.network.base;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ICNetworkListResponse<T> {
    @Expose
    int count;
    @Expose
    List<T> rows;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
