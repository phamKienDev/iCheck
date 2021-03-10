package vn.icheck.android.network.base;

public class ICNetworkError {
    int statusCode;
    String message;

    public ICNetworkError(int statusCode) {
        this.statusCode = statusCode;
    }

    public ICNetworkError(String message) {
        this.message = message;
    }

    public ICNetworkError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
