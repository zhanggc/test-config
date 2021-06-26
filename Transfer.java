package org.guocai.test.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class Test {
    static String basePath = "D:\\";
    static String inputFileName = "1.xmind";
    static String outputFileName = "1-transfer.xmind";

    public static void main(String[] args) throws IOException {
//        transferToTxt();
        transferToScr();
    }

    public static void transferToTxt() throws IOException {
        InputStream input = new FileInputStream(basePath+inputFileName);
        OutputStream output = new FileOutputStream(basePath+"dst.txt");
        int n;
        List<Integer> list = new LinkedList();
        while ((n = input.read()) != -1) {
            list.add(n);
        }
        String str = JSON.toJSONString(list);
        output.write(str.getBytes("UTF-8"));
        output.close();
        input.close();
    }


    public static void transferToScr() throws IOException {
        OutputStream output = new FileOutputStream(basePath+outputFileName);
        List<Integer> list = JSON.parseObject(readAsString1(), List.class);
        list.forEach(i->{
            try {
                output.write(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        output.close();
    }

    public static String readAsString1() throws IOException {
        InputStream input = new FileInputStream(basePath+"dst.txt");
        int n;
        StringBuilder sb = new StringBuilder();

        while ((n = input.read()) != -1) {
            sb.append((char)n);
        }
        return sb.toString();
    }
}











