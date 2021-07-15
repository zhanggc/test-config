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
    static String basePath = "D:\\xxx\\修改\\";
    static String inputFileName = "xxx.xmind";
    static String outputFileName = "xxx-transfer.xmind";

    /**
     * 合并Json个数
     */
    static final int NUM_MERGE_JSON = 40;

    /**
     * 每个Json包含元素个数(每块512KB)，转JSON特别耗时间，每次转个数尽量少
     */
    static int NUM_TO_JSON = (1 << 23) / 24 / 40;


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
//        transferToTxt();
        transferToScr();
    }

    public static void transferToTxt() throws IOException, ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        InputStream input = new FileInputStream(basePath + inputFileName);
        int n;
        List<Integer> list = new LinkedList();
        while ((n = input.read()) != -1) {
            list.add(n);
        }
        int len = list.size();
        if (len > NUM_TO_JSON) {
            int all = (int) Math.ceil((double) len / (double) NUM_TO_JSON);
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<String>> futures = new ArrayList<>();
            int num = 0;
            for (int i = 0; i < len; ) {
                List subList;
                int current = i;
                i += NUM_TO_JSON;
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

            List<String> blocks = new ArrayList<>();
            StringBuilder merge = new StringBuilder();
            for (int i = 0; i < futures.size(); i++) {
                System.out.println("开始处理:" + (i + 1) + "/" + all);
                merge.append(futures.get(i).get());
                if ((i + 1) % NUM_MERGE_JSON == 0) {
                    blocks.add(merge.toString().replaceAll("]\\[", ","));
                    merge = new StringBuilder();
                    System.out.println("处理完:" + (i + 1) + "/" + all);
                    continue;
                }

                //最后一个
                if (futures.size() - 1 == i) {
                    blocks.add(merge.toString().replaceAll("]\\[", ","));
                    merge = new StringBuilder();
                    System.out.println("处理完:" + (i + 1) + "/" + all);
                    continue;
                }
            }
            executor.shutdown();
            for (int i = 0; i < blocks.size(); i++) {
                int index = i + 1;
                System.out.println("合并开始处理:" + index + "/" + blocks.size());
                OutputStream output = new FileOutputStream(basePath + "dst-" + index + ".txt");
                output.write(blocks.get(i).getBytes("UTF-8"));
                output.flush();
                output.close();
                System.out.println("合并处理完:" + index + "/" + blocks.size());
            }
        } else {
            System.out.println("开始处理:1/1");
            String str = JSON.toJSONString(list);
            OutputStream output = new FileOutputStream(basePath + "dst.txt");
            output.write(str.getBytes("UTF-8"));
            output.flush();
            output.close();
            System.out.println("处理完:1/1");

        }

        input.close();
        System.out.println("花费时间: " + (System.currentTimeMillis() - start) / 1000 + "秒");
    }


    public static void transferToScr() throws IOException {
        OutputStream output = new FileOutputStream(basePath + outputFileName);
        String jsonArrayStr = readAsString1();
        jsonArrayStr = jsonArrayStr.replaceAll("]\\[", ",");
        List<Integer> list = JSON.parseObject(jsonArrayStr, List.class);
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
