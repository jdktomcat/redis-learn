package com.jdktomcat.pack;

import com.jdktomcat.pack.bean.Person;
import com.jdktomcat.pack.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class Main {

    public static void main(String[] args) {
        HashMap<Person, Integer> map = new HashMap<Person, Integer>(15, 2.0f);
        for (int i = 0; i < 100; i++) {
            map.put(new Person("Student-" + i, i), i);
        }
        Map<Person, Integer> otherMap = new HashMap<Person, Integer>(map);
        System.out.println(otherMap.size());

        List<String> list = new ArrayList<>();


    }
}
