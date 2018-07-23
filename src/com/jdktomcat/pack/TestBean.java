package com.jdktomcat.pack;

/**
 * 类描述：
 *
 * @author: 汤旗
 * @date:2018-05-29
 */
public class TestBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestBean(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
