package com.week7i.share.toutiao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;

import java.io.File;

import java.io.FileInputStream;

import java.io.InputStreamReader;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReadTxt {
    public static void readTxtFile(String filePath,String fileName) {
        try {

            String encoding = "utf-8";

            File file = new File(filePath);

            Map cntMap=new HashMap();

            if (file.isFile() && file.exists()) { //判断文件是否存在

                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式

                BufferedReader bufferedReader = new BufferedReader(read);

                String lineTxt = null;

                while ((lineTxt=bufferedReader.readLine()) != null) {
                    //System.out.println(lineTxt);
                    lineTxt=lineTxt.substring(1,lineTxt.length()-1);
                    String[] info = lineTxt.split(",");
                    for(int i=0;i<info.length;i++){
                        String a=info[i];
                        if(cntMap.containsKey(a)){
                            Integer count = (Integer)cntMap.get(a);
                            count++;
                            cntMap.put(a,count);
                        }else{
                            cntMap.put(a,1);
                        }
                    }
                }
                read.close();
                List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(cntMap.entrySet());
                //然后通过比较器来实现排序
                Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
                    //降序排序
                    public int compare(Map.Entry<String, Integer> o1,
                                       Map.Entry<String, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }

                });

                for(Map.Entry<String,Integer> mapping:list){
                    System.out.println(mapping.getKey()+":"+mapping.getValue());
                }

            } else {

                System.out.println("找不到指定的文件");
            }


        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static void main(String argv[]) {

        String filePath = "doc/toutiao/体育/2016-01.txt";
        readTxtFile(filePath,"2016-01");
    }
}
