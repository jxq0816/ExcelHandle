package com.week7i.share;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Question2 {
    private static int lastRowNum=749;
    private static String path = "doc/Question2.xlsx";

    /**
     * 获取可供替换的航班集合，延迟的航班集合
     * @throws IOException
     * @throws ParseException
     */
    public static void step1() throws IOException, ParseException {
        List availableList=Calculate.available(path,lastRowNum);//获得可供替换的航班集合,共计7个
        for(int i=0;i<availableList.size();i++){
            JSONObject object= (JSONObject) availableList.get(i);
            System.out.println(object);
        }
        System.out.println(availableList.size());

        List delayList=Calculate.delay(path,lastRowNum);
        for(int i=0;i<delayList.size();i++){
            JSONObject object= (JSONObject) delayList.get(i);
            System.out.println(object);
        }
        System.out.println(delayList.size());
    }


    public static void main(String[] args) throws IOException, ParseException {
        List availableList=Calculate.available(path,lastRowNum);//获得可供替换的航班集合,共计7个
        //处理35行数据，即174773460次航班,起飞时间戳为1461358200，飞机尾号14098
        long startTimeStamp=1461353100L;
        System.out.println("174773877航班 飞机尾号CKBPV置换");
        //JSONObject index=Calculate.judge(availableList,startTimeStamp);
        //availableList.remove(index);

    }
}

