package org.guocai.test.java;

import java.io.File;
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
    static String basePath = "D:\\Workspaces\\中软国际\\考试\\考试资料\\Java架构师知识脑图\\修改\\";
    static String inputFileName = "代码整洁之道.pdf";
    static String outputFileName = "代码整洁之道-transfer.pdf";

    /**
     * 合并Json个数
     */
    static final int NUM_MERGE_JSON = 10;

    /**
     * Json最小包含元素个数(每块1MB)，转JSON特别耗时间，每次转个数尽量少
     */
    static int NUM_TO_JSON = (1 << 23) / 24;

    /**
     * 块包含元素个数
     */
    static int NUM_TO_BLOCK = NUM_TO_JSON * NUM_MERGE_JSON;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
//        transferToTxt();
        transferToScr();
    }

    public static void transferToTxt() throws IOException, ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        createDestDir();
        InputStream input = new FileInputStream(basePath + inputFileName);
        int n;
        List<Integer> list = new LinkedList();
        while ((n = input.read()) != -1) {
            list.add(n);
        }
        int len = list.size();
        if (len > NUM_TO_BLOCK) {
            int all = (int) Math.ceil((double) len / (double) NUM_TO_BLOCK);
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<String>> futures = new ArrayList<>();
            int num = 0;
            for (int i = 0; i < len; ) {
                List subList;
                int current = i;
                i += NUM_TO_BLOCK;
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
                    String str = transferBlockToStr(subList);
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
                String block = futures.get(i).get();
                writeFile(block, i + 1);
                System.out.println("处理完:" + (i + 1) + "/" + all);
            }
            executor.shutdown();
        } else {
            System.out.println("开始处理:1/1");
            String block = transferBlockToStr(list);
            writeFile(block, 1);
            System.out.println("处理完:1/1");
        }
        input.close();
        System.out.println("花费时间: " + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    /**
     * block 转 str
     *
     * @param block 块
     * @return string
     */
    private static String transferBlockToStr(List block) {
        if (null == block || 0 == block.size()) {
            throw new RuntimeException("block 不允许为空!");
        }
        StringBuilder builder = new StringBuilder();
        block.forEach((item) -> {
            builder.append(item).append(",");
        });
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * 块写入文件
     *
     * @param block
     * @param index
     * @throws IOException
     */
    private static void writeFile(String block, int index) throws IOException {
        OutputStream output = new FileOutputStream(basePath + "dest" + System.getProperty("file.separator") + "dest-" + index + ".txt");
        output.write(block.getBytes("UTF-8"));
        output.flush();
        output.close();
    }

    private static void createDestDir() {
        File file = new File(basePath + System.getProperty("file.separator") + "dest");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void transferToScr() throws IOException {
        OutputStream output = new FileOutputStream(basePath + "dest" + System.getProperty("file.separator") + outputFileName);
        String jsonArrayStr = readAsString1();
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
        StringBuilder sb = new StringBuilder("[");
        for (int i = 1; ; i++) {
            File file = new File(basePath + System.getProperty("file.separator") + "dest" + System.getProperty("file.separator") + "dest-" + i + ".txt");
            if (!file.exists()) {
                break;
            }
            InputStream input = new FileInputStream(file);
            int n;
            while ((n = input.read()) != -1) {
                sb.append((char) n);
            }
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1) + "]";
    }
}
