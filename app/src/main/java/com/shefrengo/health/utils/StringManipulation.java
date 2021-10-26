package com.shefrengo.health.utils;

import com.shefrengo.health.model.Posts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StringManipulation {
    public static String[] removeArrayDuplication(String[] arr) {
        Set<String> set = new HashSet<String>(Arrays.asList(arr));

        arr = set.toArray(new String[0]);

        return arr;
    }

    public static List<Posts> removePostListDuplicates(List<Posts> postsList) {

        Set<Posts> set = new HashSet<Posts>(postsList);

        postsList.addAll(set);
        return postsList;
    }
   public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
       return salt.toString();

    }
}