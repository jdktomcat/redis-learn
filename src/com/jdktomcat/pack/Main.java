package com.jdktomcat.pack;

import java.io.File;

/**
 * @author Administrator
 */
public class Main {

    public static void main(String[] args) {
        String str = "/conf-descriptor.properties";
        System.out.println(str.replace("/", File.separator));
    }
}

