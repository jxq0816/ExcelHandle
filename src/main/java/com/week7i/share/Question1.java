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
            if(Calculate.timestamp2100>startTimeStamp){
                diffA=(Calculate.timestamp2100-startTimeStamp);
            }
            stayTimeSum+=diffA;//A航班的延迟 计入 总延时
            if(startTime<Calculate.timestamp2145){//B航班有延迟
                long diff=Calculate.timestamp2145-startTimeStamp;//B航班的延迟
                stayTimeSum+=diff;
            }
            //System.out.println("航班："+schedule);
            //System.out.println("延时"+stayTimeSum/60+"分钟");
            if(i==0){
                min=stayTimeSum;
            }else if(stayTimeSum<min){
                min=stayTimeSum;
                index=i;//选择第i个航班作为替换
            }
        }
        JSONObject replaceSchedule=(JSONObject)availableList.get(index);//选择延时最小的航班作为替换
        String aircraftId=replaceSchedule.getString("aircraftId");
        System.out.print("飞机尾号"+aircraftId+",");
       /* Long hour= (min/(60*60));
        Long mod=min%(60*60);//余数
        Long minus=mod/60;//分钟
        Long modSecond=mod%(60);
        System.out.println("最小延误时间:"+hour+"小时"+minus+"分钟"+modSecond+"秒");*/

        System.out.println("两架飞机延误时间总和为:"+min/60+"分钟");

        return replaceSchedule;
    }



    public static void main(String[] args) throws IOException, ParseException {
        List delayList=Calculate.delay(path,lastRowNum);
        for(int i=0;i<delayList.size();i++){
            JSONObject object= (JSONObject) delayList.get(i);
            String delayMinute=object.getString("delayMinute");
            Long rowNum=object.getLong("rowNum");
            String aircraftId=object.getString("aircraftId");
            System.out.println("第"+rowNum+"行,"+"飞机编号："+aircraftId+"，延时"+delayMinute+"分钟");
        }
        List availableList=Calculate.available(path,lastRowNum);//获得可供替换的航班集合,共计7个
        //处理35行数据，即174773460次航班,起飞时间戳为1461358200，飞机尾号14098
        long startTimeStamp=1461358200L;
        System.out.print("174773460航班 飞机尾号14098置换");
        JSONObject index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理42行数据，即174774204次航班,起飞时间戳为1461359100,飞机尾号44098
        System.out.print("174774204航班 飞机尾号44098置换");
        startTimeStamp=1461359100L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理51行数据，即174773432次航班,起飞时间戳为1461358200,飞机尾号64098
        System.out.print("174773432航班 飞机尾号64098置换");
        startTimeStamp=1461358200L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理59行数据，即174774076次航班,起飞时间戳为1461355500,飞机尾号15098
        System.out.print("174774076航班 飞机尾号15098置换");
        startTimeStamp=1461355500L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
        //处理69行数据，即174774048次航班,起飞时间戳为1461354300,飞机尾号85098
        System.out.print("174774048航班 飞机尾号85098置换");
        startTimeStamp=1461354300L;
        index=judge(availableList,startTimeStamp);
        availableList.remove(index);
    }
}

