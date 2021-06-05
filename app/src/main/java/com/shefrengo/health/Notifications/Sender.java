package com.shefrengo.health.Notifications;

public class Sender {
    private NotificationsData notificationsData;

    private String to;

    public Sender() {
    }

    public Sender(NotificationsData notificationsData, String to) {
        this.notificationsData = notificationsData;
        this.to = to;
    }

    public NotificationsData getNotificationsData() {
        return notificationsData;
    }

    public void setNotificationsData(NotificationsData notificationsData) {
        this.notificationsData = notificationsData;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
