package com.kk_electronic.kkportal.core.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DualCallback<T> extends AsyncCallback<T>,Callback<T>,ErrorCallback {
}
