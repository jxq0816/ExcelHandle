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
     * 获取可供替换的航班集合，延迟的航班集合,并计算延迟时间
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
            String delayMinute=object.getString("delayMinute");
            Long rowNum=object.getLong("rowNum");
            String aircraftId=object.getString("aircraftId");
            System.out.println("第"+rowNum+"行,"+"飞机编号："+aircraftId+"，延时"+delayMinute+"分钟");
        }
        System.out.println(delayList.size());
    }
    /**
     * 从可供替换的航班集合中选择一个延时最小的航班作为替换
     * @param availableList
     * @param startTimeStamp
     */
    public static JSONObject judge(List availableList,long startTimeStamp,String type){

        Long min=0L;
        int index=0;
        for(int i=0;i<availableList.size();i++){
            JSONObject schedule=(JSONObject)availableList.get(i);//遍历可供替换的航班B
            String aircraftType=schedule.getString("aircraftType");
            Long startTime=schedule.getLong("startTimeLong");//起飞时间
            //Long aircraftId=schedule.getLong("aircraftId");//起飞时间
            Long stayTimeSum=0L;//总延时
            if(aircraftType.equals(type)==false){
                stayTimeSum+=30;//不同机型间调整有30分钟的成本
                //System.out.println("替换机型:"+aircraftType);
            }
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
        System.out.println("两架飞机延误时间总和为:"+min/60+"分钟");

        return replaceSchedule;
    }

    public static void main(String[] args) throws IOException, ParseException {
        step1();
        List availableList=Calculate.available(path,lastRowNum);//获得可供替换的航班集合,共计7个
        //处理35行数据，即174773460次航班,起飞时间戳为1461358200，飞机尾号14098
        System.out.print("174773460航班 飞机尾号14098置换");
        JSONObject index=judge(availableList,1461358200,"9");
        availableList.remove(index);

        //处理42行数据，即174774204次航班,起飞时间戳为1461359100,飞机尾号44098
        System.out.print("174774204航班 飞机尾号44098置换");
        index=judge(availableList,1461359100L,"9");
        availableList.remove(index);

        //处理51行数据，即174773432次航班,起飞时间戳为1461358200,飞机尾号64098
        System.out.print("174773432航班 飞机尾号64098置换");
        index=judge(availableList,1461358200L,"9");
        availableList.remove(index);
        //处理59行数据，即174774076次航班,起飞时间戳为1461355500,飞机尾号15098
        System.out.print("174774076航班 飞机尾号15098置换");

        index=judge(availableList,1461355500L,"9");
        availableList.remove(index);
        //处理69行数据，即174774048次航班,起飞时间戳为1461354300,飞机尾号85098
        System.out.print("174774048航班 飞机尾号85098置换");

        index=judge(availableList,1461354300L,"9");
        availableList.remove(index);

    }
}

