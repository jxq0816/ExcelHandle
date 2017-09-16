package com.week7i.share;

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
        List rs=new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(inputStream);
        List<List<String>> result = new ArrayList<List<String>>();
        XSSFSheet xssfSheet = hssfWorkbook.getSheetAt(0);
        //处理当前页，循环读取每一行
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
                rs.add(aircraftId);
                //System.out.println(aircraftId);
            }
        }
        return rs;
    }

    public static List leave(String path) throws IOException, ParseException {
        List rs=new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(inputStream);
        List<List<String>> result = new ArrayList<List<String>>();
        XSSFSheet xssfSheet = hssfWorkbook.getSheetAt(0);
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
                rs.add(aircraftId);
                //System.out.println(aircraftId);
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
            String arriveId=arriveList.get(i).toString();
            for(int j=0;j<leaveList.size();j++){
                String leaveId=leaveList.get(j).toString();
                if(arriveId.equals(leaveId)){
                    rs.add(leaveId);
                    break;
                }
            }
        }
        System.out.println(rs.size());
    }
}

