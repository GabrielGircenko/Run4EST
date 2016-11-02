package com.gircenko.gabriel.run4est.intefaces;

/**
 * Created by Gabriel Gircenko on 31-Oct-16.
 */

public interface OnUserTypeReceivedListener {
    void onUserAdded(String userId, String name);
    void onUserRemoved(String userId);
}
