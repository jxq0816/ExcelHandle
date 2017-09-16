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

public class Timestamp {

    public static List arrive(String path) throws IOException, ParseException {
        List result = new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        //处理当前页，循环读取每一行x
        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow hssfRow = xssfSheet.getRow(rowNum);
            boolean b1 = false;//到达OVS
            XSSFCell destination = hssfRow.getCell(4);
            String cellData = destination.toString();
            if (cellData.equals("OVS")) {
                b1 = true;
            }
            boolean b2 = false;//于18:00之前到达OVS
            XSSFCell endTime = hssfRow.getCell(2);
            String endTimeString = endTime.toString();
            BigDecimal bd = new BigDecimal(endTimeString);
            Long l = Long.parseLong(bd.toPlainString());
            if (l < 1461348000) {
                b2 = true;
            }
            if (b1 && b2) {
                XSSFCell aircraft = hssfRow.getCell(6);
                String aircraftId = aircraft.toString();
                JSONObject obj=new JSONObject();
                obj.put("aircraftId",aircraftId);//飞机尾号
                result.add(obj);
            }
        }
        return result;
    }

    public static List leave(String path) throws IOException, ParseException {
        List rs=new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        List<List<String>> result = new ArrayList<List<String>>();
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        //处理当前页，循环读取每一行
        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow hssfRow = xssfSheet.getRow(rowNum);
            boolean b1 = false;//从OVS出发
            XSSFCell start = hssfRow.getCell(3);
            String cellData = start.toString();
            if (cellData.equals("OVS")) {
                b1 = true;
            }
            boolean b2 = false;//于21:00之后出发
            XSSFCell startTime = hssfRow.getCell(1);
            String startTimeString = startTime.toString();
            BigDecimal bd = new BigDecimal(startTimeString);
            Long l = Long.parseLong(bd.toPlainString());
            if (l > 1461358800) {
                b2 = true;
            }
            if (b1 && b2) {
                XSSFCell aircraft = hssfRow.getCell(6);
                String aircraftId = aircraft.toString();

                XSSFCell schedule = hssfRow.getCell(0);
                String scheduleId = schedule.toString();

                JSONObject obj=new JSONObject();
                obj.put("scheduleId",scheduleId);//航班编号
                obj.put("aircraftId",aircraftId);//飞机尾号
                rs.add(obj);
            }
        }
        return rs;
    }

    public static void main(String[] args) throws IOException, ParseException {
        //String path="Users/jiangxingqi/IdeaProjects/ExcelUtil/doc/huizong.xlsx";
        String path = "doc/calculate.xlsx";
        List rs=new ArrayList();
        List arriveList=arrive(path);
        List leaveList=leave(path);
        for(int i=0;i<arriveList.size();i++){
            JSONObject arriveJson= (JSONObject) arriveList.get(i);
            String arriveId=arriveJson.getString("aircraftId");
            for(int j=0;j<leaveList.size();j++){
                JSONObject leavejson= (JSONObject) leaveList.get(j);
                String leaveId=leavejson.getString("aircraftId");
                if(arriveId.equals(leaveId)){
                    System.out.println(leavejson);
                    rs.add(leavejson);
                    break;
                }
            }
        }
        System.out.println(rs.size());
    }
}

