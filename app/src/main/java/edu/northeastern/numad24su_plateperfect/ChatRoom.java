package edu.northeastern.numad24su_plateperfect;

public class ChatRoom {
    private String user1;
    private String user2;

    public ChatRoom() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatRoom.class)
    }

    public ChatRoom(String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }
}

