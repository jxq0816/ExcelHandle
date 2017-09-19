package com.week7i.share;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

public class Question2 {
    private static int lastRowNum = 749;
    private static String path = "doc/Question2.xlsx";
    private static int sum=0;

    /**
     * 从可供替换的航班集合中选择一个延时最小的航班作为替换
     *
     * @param availableList
     * @param startTimeStamp
     */
    public static JSONObject judge(List availableList, long startTimeStamp, String type) {

        Long min = 0L;
        int index = 0;
        String minAircraftType = "";

        if (availableList != null && availableList.size() != 0) {
            for (int i = 0; i < availableList.size(); i++) {
                JSONObject schedule = (JSONObject) availableList.get(i);//遍历可供替换的航班B
                String aircraftType = schedule.getString("aircraftType");
                Long startTime = schedule.getLong("startTimeLong");//起飞时间
                //Long aircraftId=schedule.getLong("aircraftId");//起飞时间
                Long stayTimeSum = 0L;//总延时
                if (aircraftType.equals(type) == false) {
                    stayTimeSum += 30;//不同机型间调整有30分钟的成本
                    //System.out.println("替换机型:"+aircraftType);
                }
                long diffA = 0L;
                if (Calculate.timestamp2100 > startTimeStamp) {
                    diffA = (Calculate.timestamp2100 - startTimeStamp);
                }
                stayTimeSum += diffA;//A航班的延迟 计入 总延时
                if (startTime < Calculate.timestamp2145) {//B航班有延迟
                    long diff = Calculate.timestamp2145 - startTimeStamp;//B航班的延迟
                    stayTimeSum += diff;
                }
                //System.out.println("航班："+schedule);
                //System.out.println("延时"+stayTimeSum/60+"分钟");
                if (i == 0) {
                    min = stayTimeSum;
                    minAircraftType = aircraftType;
                } else if (stayTimeSum < min) {
                    min = stayTimeSum;
                    index = i;//选择第i个航班作为替换
                    minAircraftType = aircraftType;
                }
            }
            JSONObject replaceSchedule = (JSONObject) availableList.get(index);//选择延时最小的航班作为替换
            String aircraftId = replaceSchedule.getString("aircraftId");
            String rowNum = replaceSchedule.getString("rowNum");
            System.out.print("第" + rowNum + "行，飞机尾号: " + aircraftId + ",机型：" + minAircraftType);
            System.out.println(" 航班延迟为:" + min / 60 + "分钟");
            System.out.println();
            sum+=(min/60);
            return replaceSchedule;
        }
        return null;

    }

    public static void finalResult() throws IOException, ParseException {
        List availableList = Calculate.availableList(path, lastRowNum);
        List saveList = Calculate.saveList(path, lastRowNum);
        for (int i = 0; i < saveList.size(); i++) {
            JSONObject object = (JSONObject) saveList.get(i);
            String rowNum = object.getString("rowNum");
            String aircraftId = object.getString("aircraftId");
            Long scheduleIdLong = object.getLong("scheduleIdLong");
            Long startTimeLong = object.getLong("startTimeLong");
            String aircraftType = object.getString("aircraftType");
            System.out.print("行号：" + rowNum + "，航班：" + scheduleIdLong + "，飞机尾号：" + aircraftId + ",机型:" + aircraftType + " 置换 ");
            if(availableList!=null&&availableList.size()!=0){
                JSONObject index = judge(availableList, startTimeLong, aircraftType);
                if(index!=null){
                    availableList.remove(index);
                }
            }else{
                Long diff=Calculate.timestamp2100-startTimeLong;
                if(diff<0){
                    diff=Calculate.timestamp2145-startTimeLong;
                }
                if(diff>5*24*60*60){
                    System.out.println("失败,且延迟时间大于5个小时，取消航班");
                }else{
                    long wait=diff/60;
                    System.out.println("失败,延迟航班，航班延迟为"+wait+"分钟");
                    sum+=wait;
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Calculate.delayListShow(path,lastRowNum);
        //Calculate.availableListShow(path,lastRowNum);
        Calculate.saveListShow(path,lastRowNum);
        //finalResult();
        //System.out.println("上述延迟累加为："+sum+"分钟");
        //Calculate.fiveMinuteLimit("doc/C10038019.xlsx",lastRowNum);
    }
}

