package com.jdktomcat.pack;

import org.gjt.jclasslib.io.ByteCodeOutput;
import org.gjt.jclasslib.io.ByteCodeOutputStream;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.MethodInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;

/**
 * 类描述：
 *
 * @author 汤旗
 * @date 2018-08-01
 */
public class Modify {

    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\Administrator\\Desktop\\iedis-2.43\\com\\seventh7\\widget\\iedis\\K.class";
        FileInputStream fis = new FileInputStream(filePath);
        DataInput di = new DataInputStream(fis);
        ClassFile cf = new ClassFile();
        cf.read(di);
        MethodInfo[] methodInfos = cf.getMethods();
        int methodCount = methodInfos.length;

        for (int i = 0; i < methodCount; i++) {
            MethodInfo methodInfo = methodInfos[i];
            String methodName = methodInfo.getName();
            AttributeInfo[] attributeInfos = methodInfo.getAttributes();
            int attributeCount = attributeInfos.length;
            for (int j = 0; j < attributeCount; j++) {
                AttributeInfo attributeInfo = attributeInfos[j];
                String attributeName = attributeInfo.getName();
                int length = attributeInfo.getAttributeLength();
                byte[] byteArray = new byte[length];
                ByteCodeOutput byteCodeOutput = new ByteCodeOutputStream(new ByteArrayOutputStream());
                attributeInfo.write(byteCodeOutput);
                byteCodeOutput.write(byteArray);

                for (int m = 0; m < length; m++) {

                    System.out.println(methodName + ":" + attributeName + ":" + m);
                }
            }
        }

//        Constant[] infos = cf.getConstantPool();
//        int count = infos.length;
//        for (int i = 0; i < count; i++) {
//            if (infos[i] != null) {
//                System.out.print(i);
//                System.out.print(" = ");
//                System.out.print(infos[i].getVerbose());
//                System.out.print(" = ");
//                System.out.println(infos[i].getVerbose());
//                if (i == 24) {
//                    ConstantUtf8Info uInfo = new ConstantUtf8Info(new ClassFile());
//                    uInfo.setString("");
//                    infos[i] = uInfo;
//                }
//
//                if (i == 37) {
//                    ConstantUtf8Info uInfo = (ConstantUtf8Info) infos[i];
//                    uInfo.setString("replace");
//                    infos[i] = uInfo;
//                }
//                if (i == 38) {
//                    ConstantUtf8Info uInfo = (ConstantUtf8Info) infos[i];
//                    uInfo.setString("(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
//                    infos[i] = uInfo;
//                }
//            }
//        }
//        cf.setConstantPool(infos);
        fis.close();
//        File f = new File(filePath);
//        ClassFileWriter.writeToFile(f, cf);

//        String conf = "/conf-descriptor.properties";
//        ClassLoader classLoader = Modify.class.getClassLoader();
//        InputStream inputStream = classLoader.getResourceAsStream(conf.replace("/", ""));
//        System.out.println(inputStream == null);
    }
}