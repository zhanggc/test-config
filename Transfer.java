package org.guocai.test.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class Transfer {
    static String basePath = "D:\\修改\\";
    static String inputFileName = "1.xmind";
    static String outputFileName = "1-transfer.xmind";

    /**
     * 每个块存储元素个数(每块2M)
     */
    static int numPerBlock = (1 << 23) / 24;

    public static void main(String[] args) throws IOException {
//        transferToTxt();
        transferToScr();
    }

    public static void transferToTxt() throws IOException {
        InputStream input = new FileInputStream(basePath + inputFileName);
        OutputStream output = new FileOutputStream(basePath + "dst.txt");
        int n;
        List<Integer> list = new LinkedList();
        while ((n = input.read()) != -1) {
            list.add(n);
        }
        int len = list.size();
        if (len > numPerBlock) {
            List subList;
            int num = 1;
            int all = len/numPerBlock;
            for (int i = 0; i <= len; ) {
                int current = i;
                i += numPerBlock;
                int end = i;
                if (end >= len - 1) {
                    subList = list.subList(current, len);
                } else {
                    subList = list.subList(current, end);
                }
                String str = JSON.toJSONString(subList);
                output.write(str.getBytes("UTF-8"));
                output.write("||".getBytes("UTF-8"));
                output.flush();
                System.out.println("处理完"+num+"/"+all);
                num++;
            }
        } else {
            String str = JSON.toJSONString(list);
            output.write(str.getBytes("UTF-8"));
        }
        output.close();
        input.close();
    }


    public static void transferToScr() throws IOException {
        OutputStream output = new FileOutputStream(basePath + outputFileName);
        List<Integer> list = JSON.parseObject(readAsString1(), List.class);
        list.forEach(i -> {
            try {
                output.write(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        output.close();
    }

    public static String readAsString1() throws IOException {
        InputStream input = new FileInputStream(basePath + "dst.txt");
        int n;
        StringBuilder sb = new StringBuilder();

        while ((n = input.read()) != -1) {
            sb.append((char) n);
        }
        return sb.toString();
    }
}
