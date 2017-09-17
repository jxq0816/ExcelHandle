package com.week7i.share;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Question1 {
    private static String path = "doc/Question1.xlsx";
    private static int lastRowNum=98;

    public static void main(String[] args) throws IOException, ParseException {
        List availableList=Calculate.available(path,lastRowNum);//获得可供替换的航班集合,共计7个
        //处理35行数据，即174773460次航班,起飞时间戳为1461358200，飞机尾号14098
        long startTimeStamp=1461358200L;
        System.out.println("174773460航班 飞机尾号14098置换");
        JSONObject index=Calculate.judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理42行数据，即174774204次航班,起飞时间戳为1461359100,飞机尾号44098
        System.out.println("174774204航班 飞机尾号44098置换");
        startTimeStamp=1461359100L;
        index=Calculate.judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理51行数据，即174773432次航班,起飞时间戳为1461358200,飞机尾号64098
        System.out.println("174773432航班 飞机尾号64098置换");
        startTimeStamp=1461358200L;
        index=Calculate.judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理59行数据，即174774076次航班,起飞时间戳为1461355500,飞机尾号15098
        System.out.println("174774076航班 飞机尾号15098置换");
        startTimeStamp=1461355500L;
        index=Calculate.judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理69行数据，即174774048次航班,起飞时间戳为1461354300,飞机尾号85098
        System.out.println("174774048航班 飞机尾号85098置换");
        startTimeStamp=1461354300L;
        index=Calculate.judge(availableList,startTimeStamp);
        availableList.remove(index);
    }
}

