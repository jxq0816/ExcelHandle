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
    private static int lastRowNum=98;
    private static Long timestamp2100=1461358800L;//21：00的时间戳
    private static Long timestamp1800=1461348000L;//18：00的时间戳
    private static Long timestamp2145= timestamp2100+45*60;//21：45的时间戳,前一航班到达时间与后一航班起飞时间之间的最小间隔时间为45分钟
    /**
     * 获得于18:00之前到达OVS的航班集合
     * @param path
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List arrive(String path) throws IOException, ParseException {
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
    public static List leave(String path) throws IOException, ParseException {
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
            if (l > 1461358800) {
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

        XSSFCell schedule = xssfRow.getCell(0);//航班编号
        String scheduleId = schedule.toString();
        BigDecimal bigDecimal = new BigDecimal(scheduleId);
        Long  scheduleIdLong= Long.parseLong(bigDecimal.toPlainString());

        XSSFCell startTime = xssfRow.getCell(1);//起飞时间
        String startTimeStr = startTime.toString();
        BigDecimal startTimeBD = new BigDecimal(startTimeStr);
        Long  startTimeLong= Long.parseLong(startTimeBD.toPlainString());

        JSONObject obj=new JSONObject();
        obj.put("rowNum",rowNum);
        obj.put("scheduleIdLong",scheduleIdLong);//航班编号
        obj.put("aircraftId",aircraftId);//飞机尾号
        obj.put("startTimeLong",startTimeLong);//飞机尾号
        rs.add(obj);
        return rs;
    }

    /**
     * 获得可供替换的航班集合
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List available() throws IOException, ParseException {
        //String path="Users/jiangxingqi/IdeaProjects/ExcelUtil/doc/huizong.xlsx";
        String path = "doc/calculate.xlsx";
        List rs=new ArrayList();
        List arriveList=arrive(path);
        List leaveList=leave(path);
        for(int i=0;i<arriveList.size();i++){
            JSONObject arriveJson= (JSONObject) arriveList.get(i);
            String arriveId=arriveJson.getString("aircraftId");//OVS机场有飞机arriveId停留
            for(int j=0;j<leaveList.size();j++){
                JSONObject leavejson= (JSONObject) leaveList.get(j);
                String leaveId=leavejson.getString("aircraftId");//飞机arriveId有待飞行的任务
                if(arriveId.equals(leaveId)){
                    System.out.println(leavejson);
                    rs.add(leavejson);
                    break;
                }
            }
        }
        return rs;
    }

    /**
     * 从可供替换的航班集合中选择一个延时最小的航班作为替换
     * @param availableList
     * @param startTimeStamp
     */
    public static JSONObject judge(List availableList,long startTimeStamp){

        Long min=0L;
        int index=0;
        for(int i=0;i<availableList.size();i++){
            JSONObject schedule=(JSONObject)availableList.get(i);//遍历可供替换的航班B
            Long startTime=schedule.getLong("startTimeLong");//起飞时间
            //Long aircraftId=schedule.getLong("aircraftId");//起飞时间
            Long stayTimeSum=0L;//总延时
            long diffA=0L;
            if(timestamp2100>startTimeStamp){
                diffA=(timestamp2100-startTimeStamp);
            }
            stayTimeSum+=diffA;//A航班的延迟 计入 总延时
            if(startTime<timestamp2145){//B航班有延迟
                long diff=timestamp2145-startTimeStamp;//B航班的延迟
                stayTimeSum+=diff;
            }
            System.out.println("航班："+schedule);
            System.out.println("延时"+stayTimeSum);
            if(i==0){
                min=stayTimeSum;
            }else if(stayTimeSum<min){
                System.out.println();
                min=stayTimeSum;
                index=i;//选择第i个航班作为替换
            }
        }
        JSONObject replaceSchedule=(JSONObject)availableList.get(index);//选择延时最小的航班作为替换
        String aircraftId=replaceSchedule.getString("aircraftId");
        System.out.println("飞机尾号"+aircraftId+",");
        Long hour= (min/(60*60));
        Long mod=min%(60*60);//余数
        Long minus=mod/60;//分钟
        Long modSecond=mod%(60);
        //System.out.println("延误时间:"+min+"秒");
        System.out.println("最小延误时间:"+hour+"小时"+minus+"分钟"+modSecond+"秒");
        return replaceSchedule;
    }
    public static void main(String[] args) throws IOException, ParseException {

        List availableList=available();//获得可供替换的航班集合,共计7个
        //处理35行数据，即174773460次航班,起飞时间戳为1461358200，飞机尾号14098
        long startTimeStamp=1461358200L;
        System.out.println("174773460航班 飞机尾号14098置换");
        JSONObject index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理42行数据，即174774204次航班,起飞时间戳为1461359100,飞机尾号44098
        System.out.println("174774204航班 飞机尾号44098置换");
        startTimeStamp=1461359100L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理51行数据，即174773432次航班,起飞时间戳为1461358200,飞机尾号64098
        System.out.println("174773432航班 飞机尾号64098置换");
        startTimeStamp=1461358200L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理59行数据，即174774076次航班,起飞时间戳为1461355500,飞机尾号15098
        System.out.println("174774076航班 飞机尾号15098置换");
        startTimeStamp=1461355500L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理69行数据，即174774048次航班,起飞时间戳为1461354300,飞机尾号85098
        System.out.println("174774048航班 飞机尾号85098置换");
        startTimeStamp=1461354300L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
    }
}

