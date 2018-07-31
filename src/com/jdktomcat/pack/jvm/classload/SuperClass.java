package com.jdktomcat.pack.jvm.classload;

/**
 * 类描述：测试父类
 *
 * @author 汤旗
 * @date 2018-07-31
 */
public class SuperClass {
    static {
        System.out.println("SuperClass Init!!");
    }

    public static final int value = 1213;
}
