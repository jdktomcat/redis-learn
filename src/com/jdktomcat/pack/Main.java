package com.jdktomcat.pack;

import com.jdktomcat.pack.util.HashMap;

import java.util.Map;

/**
 * @author Administrator
 */
public class Main {

    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(35);
        for (int i = 0; i < 100; i++) {
            map.put(i, i + 1);
        }
        Map<Integer, Integer> otherMap = new HashMap<Integer, Integer>(map);
        System.out.println(otherMap.size());
    }
}
