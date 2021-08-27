package org.guocai.test.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;

public class Transfer {
    static final String basePath = "C:\\Users\\zwx934029\\Desktop\\classes\\dest";
    static final String inputFileName = "1.rar";
    static final String outputFileName = "1-transfer.rar";
    static final String charMapFileName = "char-map.txt";

    /**
     * 块包含元素个数(1M)
     */
    static final int NUM_TO_BLOCK = (1 << 19);

    //    static final String ONE_BYTE_CHAR_STR = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
//    static final char[] ONE_BYTE_CHARS = new char[ONE_BYTE_CHAR_STR.length()];
//    static final String TWO_BYTE_CHAR_STR = "¡¢£¤¥¦§¨©ª«¬®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǀǁǂǃǄǅǆǇǈǉǊǋǌǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿ";
//    static final char[] TWO_BYTE_CHARS = new char[TWO_BYTE_CHAR_STR.length()];
    static final Character[] CHAR_CAHCE = new Character[256];
    static final List<Character> CHAR_TRANSFER = new ArrayList<>(256);
    static final Integer[] INTEGER_CAHCE = new Integer[256];

    static {
//        ONE_BYTE_CHAR_STR.getChars(0, ONE_BYTE_CHAR_STR.length(), ONE_BYTE_CHARS, 0);
//        TWO_BYTE_CHAR_STR.getChars(0, TWO_BYTE_CHAR_STR.length(), TWO_BYTE_CHARS, 0);
        for (char i = 0; i < CHAR_CAHCE.length; i++) {
            CHAR_CAHCE[i] = Character.valueOf(i);
                if(i > 13 && i != 32 && i != 160){
                    CHAR_TRANSFER.add(CHAR_CAHCE[i]);
                }
        }
        for (char i = 256; i < 356; i++) {
            CHAR_TRANSFER.add(Character.valueOf(i));
        }
        for (char i = 0; i < INTEGER_CAHCE.length; i++) {
            INTEGER_CAHCE[i] = Integer.valueOf(i);
        }
    }


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        transferToTxt();
//        transferToScr();
    }



    public static void output() throws IOException {
        OutputStream output = new FileOutputStream(basePath + System.getProperty("file.separator") + charMapFileName);
        List list = new LinkedList();
        for(char ca:CHAR_CAHCE){
            if(ca < 14 || ca == 32|| ca == 160){
                continue;
            }

            list.add(ca);
        }
        String block = transferBlockToStr(list);
        output.write(block.getBytes("UTF-8"));
        output.flush();
        output.close();
    }


    public static void transferToTxt() throws IOException, ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        createDestDir();
        InputStream input = new FileInputStream(basePath + System.getProperty("file.separator") + inputFileName);
        int n;
        List<Byte> numList = new LinkedList();
        List<Character> charList = new LinkedList();
        NumFequency fequency = new NumFequency();
        StringBuilder trace = new StringBuilder();
        byte[] bytes = new byte[8192];
        while ((n = input.read(bytes)) != -1) {
//            trace.append(n).append(",");
            for (int i = 0; i < n; i++) {
                fequency.happen(bytes[i]);
                numList.add(bytes[i]);
            }
        }
        fequency.assignChar();
        numList.forEach(num -> {
            charList.add(fequency.getChar(num));
        });
        int len = charList.size();
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
                    subList = charList.subList(current, len);
                } else {
                    subList = charList.subList(current, end);
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
            String block = transferBlockToStr(charList);
            writeFile(block, 1);
            System.out.println("处理完:1/1");
        }
        input.close();
//        System.out.println("trace:" + trace.toString());
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
            builder.append(item);
        });
        return builder.toString();
    }

    /**
     * 块写入文件
     *
     * @param block
     * @param index
     * @throws IOException
     */
    private static void writeFile(String block, int index) throws IOException {
        OutputStream output = new FileOutputStream(basePath + System.getProperty("file.separator") + "dest" + System.getProperty("file.separator") + "dest-" + index + ".txt");
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

    public static void transferToScr() throws IOException, ExecutionException, InterruptedException {
        System.out.println("开始处理...");
        long start = System.currentTimeMillis();
        createDestDir();
        NumFequency fequency = new NumFequency();
        fequency.loadCharMap();
        OutputStream output = new FileOutputStream(basePath + System.getProperty("file.separator") + "dest" + System.getProperty("file.separator") + outputFileName);
        String jsonArrayStr = readAsString();
        char[] charArray = new char[jsonArrayStr.length()];
        jsonArrayStr.getChars(0, jsonArrayStr.length(), charArray, 0);
        StringBuilder trace = new StringBuilder();
        byte[] batch = new byte[8192];
        short batchIndex = 0;
        for (int i = 0; i < charArray.length; i++) {
            Byte index = fequency.getNum(charArray[i]);
            batch[batchIndex] = index;
            batchIndex++;
//            trace.append(index).append(",");
            if (batch.length <= batchIndex) {
                output.write(batch);
                batchIndex = 0;
            } else if (i + 1 == charArray.length) {
                output.write(batch, 0, batchIndex);
            }
        }
        output.close();
//        System.out.println("trace write bit:" + trace.toString());
        System.out.println("处理完成...");
        System.out.println("花费时间: " + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    public static String readAsString() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 1; ; i++) {
            File file = new File(basePath + System.getProperty("file.separator") + "dest-" + i + ".txt");
            if (!file.exists()) {
                break;
            }
            int finalI = i;
            futures.add(executor.submit(() -> {
                System.out.println("开始读取文件:dest-" + finalI + ".txt");
                StringBuilder sb1 = new StringBuilder("");
                InputStream input = null;
                InputStreamReader reader = null;
                try {
                    input = new FileInputStream(file);
                    reader = new InputStreamReader(input);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                StringBuilder trace = new StringBuilder();
                int n;
                while (true) {
                    try {
                        if ((n = reader.read()) == -1) break;
                    } catch (IOException e) {
                        input.close();
                        reader.close();
                        e.printStackTrace();
                        return "";
                    }
//                    trace.append((char) n);
                    sb1.append((char) n);
                }
                input.close();
                reader.close();

//                System.out.println("trace read char:"+trace.toString());
                System.out.println("结束读取文件:dest-" + finalI + ".txt");
                return sb1.toString();
            }));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < futures.size(); i++) {
            sb.append(futures.get(i).get());
        }
        executor.shutdown();
        return sb.substring(0, sb.length());
    }


    /**
     * 数字频率处理
     */
    static class NumFequency {
        Map<Byte, Integer> numFeq = new HashMap<>();
        Map<Byte, Character> numToChar = new HashMap<>();
        Map<String, Byte> charToNum = new HashMap<>();
        Set<NumFeqItem> feqSet = new TreeSet<>();

        class NumFeqItem implements Comparable {
            Byte num;
            Integer feq;

            public NumFeqItem(Byte num, Integer feq) {
                this.num = num;
                this.feq = feq;
            }

            @Override
            public int compareTo(Object o) {
                NumFeqItem other = (NumFeqItem) o;

                /**
                 * 不覆盖
                 */
                if (feq.equals(other.feq)) {
                    return 1;
                }

                /**
                 * 逆序
                 */
                if (feq > other.feq) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }


        //增加出现次数
        public void happen(Byte num) {
            Integer feq = numFeq.get(num);
            if (null == feq) {
                feq = 1;
            } else {
                feq++;
            }
            numFeq.put(num, feq);
        }

        public void assignChar() throws IOException {
            numFeq.entrySet().forEach(entry -> {
                feqSet.add(new NumFeqItem(entry.getKey(), entry.getValue()));
            });
            AtomicInteger i = new AtomicInteger();
            StringBuilder trace = new StringBuilder();
            feqSet.forEach(item -> {
                int i1 = i.get();
                Character _char = CHAR_TRANSFER.get(i1);
                numToChar.put(item.num, _char);
                charToNum.put(String.valueOf(_char).intern(), item.num);
                i.getAndIncrement();
                trace.append("(" + item.num + "," + item.feq + "),");
            });
            OutputStream output = new FileOutputStream(basePath + System.getProperty("file.separator") + "dest" + System.getProperty("file.separator") + charMapFileName);
            output.write(JSON.toJSONString(charToNum).getBytes("UTF-8"));
            output.flush();
            output.close();
            System.out.println("trace num feq desc:" + trace.toString());
        }

        public Character getChar(Byte num) {
            return numToChar.get(num);
        }

        public void loadCharMap() throws IOException {
            System.out.println("开始读取charMap文件:" + charMapFileName + ".txt");
            File file = new File(basePath + System.getProperty("file.separator") + charMapFileName);
            StringBuilder sb1 = new StringBuilder();
            InputStream input = null;
            InputStreamReader reader = null;
            try {
                input = new FileInputStream(file);
                reader = new InputStreamReader(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int n = 0;
            while (true) {
                try {
                    if ((n = reader.read()) == -1) break;
                } catch (IOException e) {
                    input.close();
                    reader.close();
                    e.printStackTrace();
                }
                sb1.append((char) n);
            }
            input.close();
            reader.close();
            Map<String, Integer> temp = JSON.parseObject(sb1.toString(), Map.class);

            temp.entrySet().forEach(entry -> {
                charToNum.put(entry.getKey(), entry.getValue().byteValue());
            });
            System.out.println("结束读取开始读取charMap文件文件:" + charMapFileName + ".txt");
        }

        public Byte getNum(Character character) {
            return charToNum.get(character.toString());
        }
    }
}
