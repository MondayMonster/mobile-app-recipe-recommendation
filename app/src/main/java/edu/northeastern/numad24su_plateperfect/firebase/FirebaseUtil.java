package edu.northeastern.numad24su_plateperfect.firebase;

public  class FirebaseUtil {

    private static String currentUser;

    public static void setCurrentUser(String user) {
        currentUser = user;
    }

    public static String getCurrentUser() {
        return currentUser;
    }
}
