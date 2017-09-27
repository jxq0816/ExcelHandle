package com.week7i.share.math;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aircrafts {
    private static int lastRowNum=151;
    private static String path = "doc/Aircrafts.xlsx";

    public static Map timeInfo(String path,int lastRowNum) throws IOException, ParseException {
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        Map rs=new HashMap();

        //处理当前页，循环读取每一行x
        for (int rowNum = 1; rowNum <=lastRowNum ; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            XSSFCell destination = xssfRow.getCell(4);//起点机场
            String cellData = destination.toString();
            if (cellData.equals("OVS")) {
                XSSFCell startTime = xssfRow.getCell(2);//最早可用时间
                BigDecimal bd = new BigDecimal(startTime.toString());
                Long start = Long.parseLong(bd.toPlainString());

                XSSFCell endTime = xssfRow.getCell(3);//最晚可用时间
                String endTimeString = endTime.toString();
                Long end = Long.parseLong(new BigDecimal(endTimeString).toPlainString());

                Map m=new HashMap();
                m.put("start",start);
                m.put("end",end);

                XSSFCell id = xssfRow.getCell(0);//飞机尾号
                String airId=id.toString();
                //m.put("id",airId);
                System.out.println("飞机尾号："+id+" 最早可用时间："+start+" 最晚可用时间："+end);
                rs.put(airId,m);
            }
        }
        return rs;
    }
    public static void main(String[] args) throws IOException, ParseException {
        //Calculate.delayListShow(path,lastRowNum);
        //Calculate.availableListShow(path,lastRowNum);
        //Calculate.saveListShow(path,lastRowNum);
        Map m=timeInfo(path,lastRowNum);
        System.out.println(m);
    }
}

