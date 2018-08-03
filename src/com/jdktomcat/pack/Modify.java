package com.jdktomcat.pack;

import org.gjt.jclasslib.bytecode.ImmediateByteInstruction;
import org.gjt.jclasslib.bytecode.ImmediateShortInstruction;
import org.gjt.jclasslib.bytecode.Instruction;
import org.gjt.jclasslib.bytecode.Opcode;
import org.gjt.jclasslib.io.ByteCodeReader;
import org.gjt.jclasslib.io.ByteCodeWriter;
import org.gjt.jclasslib.io.ClassFileWriter;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.Constant;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;
import org.gjt.jclasslib.structures.constants.ConstantStringInfo;
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * 类描述：
 *
 * @author 汤旗
 * @date 2018-08-01
 */
public class Modify {

    private static final String TARGET_METHOD = "a";

    private static final String TARGET_ATTRIBUTE = "Code";

    private static final int stackIndex = 17;

    private static final int indexConstant = 3;

    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\Administrator\\Desktop\\iedis-2.43\\com\\seventh7\\widget\\iedis\\K.class";
        FileInputStream fis = new FileInputStream(filePath);
        DataInput di = new DataInputStream(fis);
        ClassFile cf = new ClassFile();
        cf.read(di);
        MethodInfo[] methods = cf.getMethods();
        int methodCount = methods.length;

        // 修改方法逻辑
        for (int i = 0; i < methodCount; i++) {
            MethodInfo methodInfo = methods[i];
            String methodName = methodInfo.getName();
            if (TARGET_METHOD.equalsIgnoreCase(methodName)) {
                AttributeInfo[] attributes = methodInfo.getAttributes();
                int attributeCount = attributes.length;
                for (int j = 0; j < attributeCount; j++) {
                    AttributeInfo attributeInfo = attributes[j];
                    String attributeName = attributeInfo.getName();
                    if (TARGET_ATTRIBUTE.equalsIgnoreCase(attributeName)) {
                        CodeAttribute codeAttribute = (CodeAttribute) attributeInfo;
                        byte[] code = codeAttribute.getCode();
                        List<Instruction> instructionList = ByteCodeReader.readByteCode(code);
                        ImmediateShortInstruction instruction = (ImmediateShortInstruction) instructionList.get(stackIndex);
                        instructionList.set(stackIndex, new ImmediateByteInstruction(Opcode.LDC, false, instruction.getImmediateShort()));
                        code = ByteCodeWriter.writeByteCode(instructionList);
                        codeAttribute.setCode(code);
                    }
                }
            }
        }

        // 修改常量
        Constant[] constants = cf.getConstantPool();
        int count = constants.length;
        Constant[] newConstants = new Constant[count + 1];
        for (int i = 0; i < count; i++) {
            if (constants[i] != null) {
                if (i == indexConstant) {
                    ConstantStringInfo constantStringInfo = new ConstantStringInfo(cf);
                    constantStringInfo.setStringIndex(count);
                    constants[i] = constantStringInfo;
                }
                if (i == 37) {
                    ConstantUtf8Info uInfo = (ConstantUtf8Info) constants[i];
                    uInfo.setString("replace");
                    constants[i] = uInfo;
                }
                if (i == 38) {
                    ConstantUtf8Info uInfo = (ConstantUtf8Info) constants[i];
                    uInfo.setString("(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
                    constants[i] = uInfo;
                }
                newConstants[i] = constants[i];
            }
        }
        ConstantUtf8Info constantStringInfo = new ConstantUtf8Info(cf);
        constantStringInfo.setString("");
        newConstants[count] = constantStringInfo;
        cf.setConstantPool(newConstants);

        fis.close();
        File f = new File(filePath);
        ClassFileWriter.writeToFile(f, cf);
    }
}