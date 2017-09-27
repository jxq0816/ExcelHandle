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

            if (file.isFile() && file.exists()) { //判断文件是否存在

                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式

                BufferedReader bufferedReader = new BufferedReader(read);

                String lineTxt = null;

                while ((lineTxt=bufferedReader.readLine()) != null) {
                    System.out.println(lineTxt);

                }
                read.close();
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
