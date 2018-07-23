package com.jdktomcat.pack.task;

import java.util.HashMap;
import java.util.Map;


/**
 * 类描述：单例注册表示例类
 *
 * @author 汤旗
 * @date 2018-06-27
 */
public class RegSingleton {

    /**
     * 使用一个map来当注册表
     */
    private static Map<String, Object> registry = new HashMap<String, Object>();

    // 静态块，在类被加载时自动执行
    static {
        RegSingleton rs = new RegSingleton();
        registry.put(rs.getClass().getName(), rs);
    }

    /**
     * 受保护的默认构造函数，如果为继承关系，则可以调用，克服了单例类不能为继承的缺点
     */
    protected RegSingleton() {
    }

    /**
     * 静态工厂方法，返回此类的唯一实例
     */
    public static RegSingleton getInstance(String name) {
        if (name == null) {
            name = "RegSingleton";
        }
        if (registry.get(name) == null) {
            try {
                registry.put(name, Class.forName(name).newInstance());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (RegSingleton) registry.get(name);
    }
}
