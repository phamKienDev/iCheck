package vn.icheck.android.network.util;


import java.io.IOException;

import vn.icheck.android.network.base.ICNetworkError;
import vn.icheck.android.network.base.RetrofitException;


public class RetrofitErrorUtils {
  public static ICNetworkError getError(Throwable throwable) {
      if (throwable instanceof RetrofitException) {
          RetrofitException retrofitException = (RetrofitException) throwable;
          ICNetworkError error = null;
          try {
              error = retrofitException.getErrorBodyAs(ICNetworkError.class);
          } catch (IOException e) {
              e.printStackTrace();
          } finally {
              if (error != null) {
                  return error;
              } else {
                  return new ICNetworkError(505,"ERR_UNEXPECTED");
              }
          }
      } else {
          return new ICNetworkError(505,"ERR_UNEXPECTED");
      }
  }

}
