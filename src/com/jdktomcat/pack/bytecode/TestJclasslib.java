package com.jdktomcat.pack.bytecode;

/**
 * 类描述：
 *
 * @author 汤旗
 * @date 2018-08-03
 */

import org.gjt.jclasslib.io.ClassFileWriter;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.Constant;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;
import org.gjt.jclasslib.structures.attributes.LineNumberTableAttribute;
import org.gjt.jclasslib.structures.attributes.LineNumberTableEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestJclasslib {
    /**
     * 要展示盒修改的方法名
     */
    private static String methodName = "main";
    /**
     * Code对应的常量池index
     */
    private static int codeConstantIndex;

    /**
     * LineNumberTable对应的常量池index
     */
    private static int lineNumberTableConstantIndex;

    private static String classFileName = "D:\\workspace\\java\\out\\production\\java\\com\\jdktomcat\\pack\\bytecode\\Test2.class";

    private static InputStream fis;

    private static ClassFile cf = new ClassFile();

    private String deletedByteStr = "43,-74,0,7";

    /**
     * 被删除的代码的字节行号，在调整字节码行号表时使用
     */
    private int deletedCodeNumber = 12;

    /**
     * 删除的代码字节数
     */
    private int deletedCodeCount = 4;

    public static void main(String[] args) throws Exception {
        initParams();
        System.out.println("Method info");
        MethodInfo[] methods = cf.getMethods();
        for (int i = 0; i < methods.length; i++) {
            System.out.println(methods[i].getName());
            //根据方法名找出对应的方法
            if (methodName.equals(methods[i].getName())) {
                MethodInfo mi = methods[i];
                //方法体属性，包括Code属性
                AttributeInfo[] attributes = mi.getAttributes();
                for (AttributeInfo ai : attributes) {
                    System.out.println("AttributeInfo.name:" + ai.getName());
                    //找出此方法的Code，即为方法体代码
                    if (ai.getAttributeNameIndex() == codeConstantIndex) {
                        CodeAttribute codeAttribute = (CodeAttribute) ai;
                        int attributeLength = codeAttribute.getAttributeLength();
                        System.out.println("attributeLength:" + attributeLength);
                        for (byte b : codeAttribute.getCode()) {
                            System.out.println(b);
                        }
                        AttributeInfo[] codeAis = codeAttribute.getAttributes();
                        System.out.println("code attributeInfo length:" + codeAis.length);
                        for (AttributeInfo codeAi : codeAis) {
                            System.out.println(codeAi.getAttributeNameIndex());
                            //此处找出对应LineNumberTalbe，显示对应行对应关系。
                            if (codeAi.getAttributeNameIndex() == lineNumberTableConstantIndex) {
                                LineNumberTableAttribute lnta = (LineNumberTableAttribute) codeAi;
                                LineNumberTableEntry[] lineNumberTableEntrys = lnta.getLineNumberTable();
                                for (LineNumberTableEntry lineNumberTableEntry : lineNumberTableEntrys) {
                                    System.out.println(lineNumberTableEntry.getStartPc() + "=" + lineNumberTableEntry.getLineNumber());
                                }
                            }
                        }
                    }
                }
            }
        }
        fis.close();
    }

    /**
     * 此方法用来修改class文件，删除class文件中的方法调用，修改对应的attribute_length，修改对应的code_length,修改LineNumberTable
     *
     * @throws Exception
     */
    public void updateClassFile() throws Exception {

        //将需要用到的参数初始化
        initParams();

        MethodInfo[] methods = cf.getMethods();
        for (int i = 0; i < methods.length; i++) {
            System.out.println(methods[i].getName());
            //根据方法名找出对应的方法
            if (methodName.equals(methods[i].getName())) {
                MethodInfo mi = methods[i];
                //方法体属性，包括Code属性
                AttributeInfo[] attributes = mi.getAttributes();
                for (AttributeInfo ai : attributes) {
                    System.out.println(ai.getName());
                    //找出此方法的Code，即为方法体代码
                    if (ai.getAttributeNameIndex() == codeConstantIndex) {
                        CodeAttribute codeAttribute = (CodeAttribute) ai;
                        int attributeLength = codeAttribute.getAttributeLength();
                        //此处attributeLength为动态计算所得，不需要更改，直接更改code数组
                        System.out.println("attributeLength:" + attributeLength);
                        byte[] changedCode = changeCode(codeAttribute.getCode());
                        codeAttribute.setCode(changedCode);
                        AttributeInfo[] codeAis = codeAttribute.getAttributes();
                        System.out.println("code attributeInfo length:" + codeAis.length);
                        for (AttributeInfo codeAi : codeAis) {
                            System.out.println(codeAi.getAttributeNameIndex());
                            //此处找出对应LineNumberTalbe，显示对应行对应关系。
                            if (codeAi.getAttributeNameIndex() == lineNumberTableConstantIndex) {
                                LineNumberTableAttribute lnta = (LineNumberTableAttribute) codeAi;
                                LineNumberTableEntry[] lineNumberTableEntrys = lnta.getLineNumberTable();
                                for (LineNumberTableEntry lineNumberTableEntry : lineNumberTableEntrys) {
                                    int startPc = lineNumberTableEntry.getStartPc();
                                    if (lineNumberTableEntry.getStartPc() > deletedCodeNumber) {
                                        lineNumberTableEntry.setStartPc(startPc - deletedCodeCount);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        fis.close();
        File f = new File(classFileName);
        ClassFileWriter.writeToFile(f, cf);
    }


    public byte[] changeCode(byte[] code) {
        System.out.println(deletedByteStr);
        StringBuffer sb = new StringBuffer();
        for (byte b : code) {
            sb.append(b).append(",");
        }
        System.out.println(sb);
        sb.delete(sb.indexOf(deletedByteStr), sb.indexOf(deletedByteStr) + deletedByteStr.length());
        System.out.println(sb);
        String[] bStrs = sb.toString().split(",");
        List<Byte> bytes = new ArrayList<Byte>();
        for (String bs : bStrs) {
            if (bs != null && bs.equalsIgnoreCase("")) {
                bytes.add(Byte.valueOf(bs));
            }
        }
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    private static void initParams() throws Exception {
        File file = new File(classFileName);
        fis = new FileInputStream(file);

        DataInput di = new DataInputStream(fis);
        cf.read(di);

        Constant[] infos = cf.getConstantPool();
        System.out.println(infos.length);

        for (int i = 0; i < infos.length; i++) {
            if (infos[i] != null) {
                System.out.print(i);
                System.out.print("=");
                System.out.print(infos[i].getVerbose());
                System.out.print("=");
                System.out.println(infos[i].getVerbose());

                if (infos[i].getVerbose().equals("Code")) {
                    codeConstantIndex = i;
                }
                if (infos[i].getVerbose().equals("LineNumberTable")) {
                    lineNumberTableConstantIndex = i;
                }
            }
        }
    }
}
