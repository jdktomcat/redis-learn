package com.jdktomcat.pack.bytecode;

import java.io.File;

/**
 * 类描述：
 *
 * @author 汤旗
 * @date 2018-08-03
 */
public class Test {
    public void makeup(String content){
        content.replace("/", File.separator);
    }
}
