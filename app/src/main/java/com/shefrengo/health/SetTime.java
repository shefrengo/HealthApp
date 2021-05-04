package com.shefrengo.health;

import android.annotation.SuppressLint;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SetTime {
    private static long then;

    public static String TwitterTimeDifferentitaion(String responseTime) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        ParsePosition pos = new ParsePosition(0);

        try {
            then = Objects.requireNonNull(formatter.parse(responseTime, pos)).getTime();
        } catch (Exception e) {

            e.getMessage();
            e.printStackTrace();
        }

        long now = new Date().getTime();

        long seconds = (now - then) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = months / 12;
        String friendly;
        long num;

        if (years > 0) {
            num = years;
            friendly = years + " years ago";
        } else if (months > 0) {
            num = months;
            friendly = months + " months ago";
        } else if (days > 0) {
            num = days;
            friendly = days + " days ago";
        } else if (hours > 0) {
            num = hours;
            friendly = hours + " hours ago";
        } else if (minutes > 0) {
            num = minutes;
            friendly = minutes + " minutes ago";
        } else {
            num = seconds;
            friendly = seconds + " seconds ago";
        }

        if (num > 1) {
            friendly += "";
        }

        return friendly + " ";

    }


    public static String LongTwitterTimeDifferentitaion(long responseTime) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        ParsePosition pos = new ParsePosition(0);



        String dateString = formatter.format(new Date(responseTime));
        long then = Objects.requireNonNull(formatter.parse(dateString, pos)).getTime();
        long now = new Date().getTime();
        long seconds = (now - then) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = months / 12;
        String friendly;
        long num;

        if (years > 0) {
            num = years;
            friendly = years + " y";
        } else if (months > 0) {
            num = months;
            friendly = months + " mo";
        } else if (days > 0) {
            num = days;
            friendly = days + " d";
        } else if (hours > 0) {
            num = hours;
            friendly = hours + " h";
        } else if (minutes > 0) {
            num = minutes;
            friendly = minutes + " m";
        } else {
            num = seconds;
            friendly = seconds + " s";
        }

        if (num > 1) {
            friendly += "";
        }

        return friendly;

    }
}
