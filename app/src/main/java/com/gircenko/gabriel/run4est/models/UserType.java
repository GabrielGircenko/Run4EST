package com.gircenko.gabriel.run4est.models;

/**
 * Created by Gabriel Gircenko on 28-Oct-16.
 */

public enum UserType {

    ADMIN("0"), MANAGER("1"), USER("2");

    private String type;

    UserType(String s) {
        type = s;
    }

    @Override
    public String toString() {
        return type;
    }

    public static UserType determineUserType(String s) {
        if (s != null) {
            if (s.equals(ADMIN.toString())) {
                return ADMIN;

            } else if (s.equals(MANAGER.toString())) {
                return MANAGER;
            }
        }

        return USER;
    }
}
