package com.example.readeasy;

public class USER_INFORMATION {

    private static String type = "" , name = "", mail = "";

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        USER_INFORMATION.type = type;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        USER_INFORMATION.name = name;
    }

    public static String getMail() {
        return mail;
    }

    public static void setMail(String mail) {
        USER_INFORMATION.mail = mail;
    }
}
