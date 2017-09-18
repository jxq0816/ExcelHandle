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

public class Calculate {
    public static Long timestamp2100=1461358800L;//21：00的时间戳
    public static Long timestamp1800=1461348000L;//18：00的时间戳
    public static Long timestamp2145= timestamp2100+45*60;//21：45的时间戳,前一航班到达时间与后一航班起飞时间之间的最小间隔时间为45分钟
    /**
     * 获得于18:00之前到达OVS的航班集合
     * @param path
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List arrive(String path,int lastRowNum) throws IOException, ParseException {
        List result = new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

        //处理当前页，循环读取每一行x
        for (int rowNum = 1; rowNum <=lastRowNum ; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            boolean b1 = false;//到达OVS
            XSSFCell destination = xssfRow.getCell(4);
            String cellData = destination.toString();
            if (cellData.equals("OVS")) {
                b1 = true;
            }
            boolean b2 = false;//于18:00之前到达OVS
            XSSFCell endTime = xssfRow.getCell(2);
            String endTimeString = endTime.toString();
            BigDecimal bd = new BigDecimal(endTimeString);
            Long l = Long.parseLong(bd.toPlainString());
            if (l < timestamp1800) {
                b2 = true;
            }
            if (b1 && b2) {
                result = addToResult(xssfRow, rowNum, result);
            }
        }
        return result;
    }

    /**
     * 获得于21：00之后从OVS出发的航班集合
     * @param path
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List leave(String path,int lastRowNum) throws IOException, ParseException {
        List rs=new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        //处理当前页，循环读取每一行
        for (int rowNum = 1; rowNum <=lastRowNum ; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            boolean b1 = false;//从OVS出发
            XSSFCell start = xssfRow.getCell(3);
            String cellData = start.toString();
            if (start == null) {
                continue;
            }
            if (cellData.equals("OVS")) {
                b1 = true;
            }
            boolean b2 = false;//于21:00之后出发
            XSSFCell startTime = xssfRow.getCell(1);
            String startTimeString = startTime.toString();
            BigDecimal bd = new BigDecimal(startTimeString);
            Long l = Long.parseLong(bd.toPlainString());
            if (l > timestamp2100) {
                b2 = true;
            }
            if (b1 && b2) {
                rs = addToResult(xssfRow, rowNum, rs);
            }
        }
        return rs;
    }

    public static List addToResult(XSSFRow xssfRow,int rowNum,List rs){
        XSSFCell aircraft = xssfRow.getCell(6);//飞机尾号
        String aircraftId = aircraft.toString();

        XSSFCell aircraftTypeCell = xssfRow.getCell(5);//飞机型号
        String aircraftType = aircraftTypeCell.toString();

        XSSFCell schedule = xssfRow.getCell(0);//航班编号
        String scheduleId = schedule.toString();
        BigDecimal bigDecimal = new BigDecimal(scheduleId);
        Long  scheduleIdLong= Long.parseLong(bigDecimal.toPlainString());

        XSSFCell startTime = xssfRow.getCell(1);//起飞时间
        String startTimeStr = startTime.toString();
        BigDecimal startTimeBD = new BigDecimal(startTimeStr);
        Long  startTimeLong= Long.parseLong(startTimeBD.toPlainString());

        JSONObject obj=new JSONObject();
        obj.put("rowNum",rowNum+1);
        obj.put("scheduleIdLong",scheduleIdLong);//航班编号
        obj.put("aircraftId",aircraftId);//飞机尾号
        obj.put("startTimeLong",startTimeLong);//飞机尾号
        obj.put("aircraftType",aircraftType);//飞机尾号
        rs.add(obj);
        return rs;
    }

    /**
     * 获得可供替换的航班集合
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List available(String path,int lastRowNum) throws IOException, ParseException {
        List rs=new ArrayList();
        List arriveList=arrive(path,lastRowNum);
        List leaveList=leave(path,lastRowNum);
        for(int i=0;i<arriveList.size();i++){
            JSONObject arriveJson= (JSONObject) arriveList.get(i);
            String arriveId=arriveJson.getString("aircraftId");//OVS机场有飞机arriveId停留
            for(int j=0;j<leaveList.size();j++){
                JSONObject leavejson= (JSONObject) leaveList.get(j);
                String leaveId=leavejson.getString("aircraftId");//飞机arriveId有待飞行的任务
                if(arriveId.equals(leaveId)){
                    //System.out.println(leavejson);
                    rs.add(leavejson);
                    break;
                }
            }
        }
        return rs;
    }

    /**
     * 受到影响的航班集合
     * @param path
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List delay(String path,int lastRowNum) throws IOException, ParseException {
        List result = new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

        //处理当前页，循环读取每一行
        for (int rowNum = 1; rowNum <=lastRowNum ; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            XSSFCell destination = xssfRow.getCell(4);//到达机场
            String cellData = destination.toString();
            if (cellData.equals("OVS")) {
                XSSFCell endTime = xssfRow.getCell(2);//到达时间
                String endTimeString = endTime.toString();
                BigDecimal endTimeBD = new BigDecimal(endTimeString);
                Long l = Long.parseLong(endTimeBD.toPlainString());
                if (l>timestamp1800 && l<timestamp2100) {
                    result = addToResult(xssfRow, rowNum, result);
                }
                continue;
            }
            XSSFCell leave = xssfRow.getCell(3);//起飞机场
            String leaveCellData = leave.toString();
            if (leaveCellData.equals("OVS")) {
                XSSFCell startTime = xssfRow.getCell(1);//起飞时间
                String startTimeString = startTime.toString();
                BigDecimal bd = new BigDecimal(startTimeString);
                Long leaveTimeLong = Long.parseLong(bd.toPlainString());
                if (leaveTimeLong>timestamp1800 && leaveTimeLong < timestamp2100) {
                    result = addToResult(xssfRow, rowNum, result);
                }
            }
        }
        return result;
    }
}

