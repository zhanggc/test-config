package org.guocai.test.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;

public class Transfer {
    static String basePath = "D:\\xx\\修改\\";
    static String inputFileName = "xx.xmind";
    static String outputFileName = "xx-transfer.xmind";

    /**
     * 每个块存储元素个数(每块2M)
     */
    static int numPerBlock = (1 << 23) / 24;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        transferToTxt();
//        transferToScr();
    }

    public static void transferToTxt() throws IOException, ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        InputStream input = new FileInputStream(basePath + inputFileName);
        OutputStream output = new FileOutputStream(basePath + "dst.txt");
        int n;
        List<Integer> list = new LinkedList();
        while ((n = input.read()) != -1) {
            list.add(n);
        }
        int len = list.size();
        if (len > numPerBlock) {
            int all = (int) Math.ceil((double) len / (double) numPerBlock);
            ExecutorService executor = Executors.newFixedThreadPool(all);
            List<Future<String>> futures = new ArrayList<>();
            int num = 0;
            for (int i = 0; i < len; ) {
                List subList;
                int current = i;
                i += numPerBlock;
                int end = i;
                if (end >= len - 1) {
                    subList = list.subList(current, len);
                } else {
                    subList = list.subList(current, end);
                }
                int finalNum = num;
                Future<String> future = executor.submit(() -> {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    System.out.println((finalNum + 1) + "/" + all + "进行Json序列化开始于 " + hour + ":" + minute + ":" + second);
                    String str = JSON.toJSONString(subList);
                    calendar = Calendar.getInstance();
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                    second = calendar.get(Calendar.SECOND);
                    System.out.println((finalNum + 1) + "/" + all + "进行Json序列化结束于 " + hour + ":" + minute + ":" + second);
                    return str;
                });
                futures.add(future);
                num++;
            }

            for (int i = 0; i < futures.size(); i++) {
                System.out.println("开始处理:" + (i + 1) + "/" + all);
                output.write(futures.get(i).get().getBytes("UTF-8"));
                output.write("||".getBytes("UTF-8"));
                output.flush();
                System.out.println("处理完:" + (i + 1) + "/" + all);
            }
            executor.shutdown();
        } else {
            System.out.println("开始处理:1/1");
            String str = JSON.toJSONString(list);
            output.write(str.getBytes("UTF-8"));
            System.out.println("处理完:1/1");

        }

        output.close();
        input.close();
        System.out.println("花费时间: " + (System.currentTimeMillis() - start) / 1000 + "秒");
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
