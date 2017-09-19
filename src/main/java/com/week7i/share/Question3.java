package com.week7i.share;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.objects.NativeMath.abs;

public class Question3 {
    private static int lastRowNum=749;
    private static String path = "doc/Question2.xlsx";
    /**
     * 从可供替换的航班集合中选择一个延时最小的航班作为替换
     * @param availableList
     * @param startTimeStamp
     */
    public static JSONObject judge(List availableList,long startTimeStamp,String type){
        Map map=new HashMap();
        map.put("9",87);
        map.put("73H",158);
        map.put("321",170);
        map.put("3KR",296);
        map.put("320",140);
        map.put("77W",402);
        map.put("32A",158);
        map.put("332",241);
        map.put("333",302);

        Long min=0L;
        int index=0;
        String minAircraftType="";
        for(int i=0;i<availableList.size();i++){
            JSONObject schedule=(JSONObject)availableList.get(i);//遍历可供替换的航班B
            String aircraftType =schedule.getString("aircraftType");
            Long startTime=schedule.getLong("startTimeLong");//起飞时间
            //Long aircraftId=schedule.getLong("aircraftId");//起飞时间

            Long stayTimeSum=0L;//总延时
            Integer siteNumA= (Integer) map.get(type);//获得待优化航班的飞机座位数量
            Integer siteNumB= (Integer) map.get(aircraftType);//获得替换航班的飞机座位数量
            if(aircraftType.equals(type)==false){//不同机型
                //System.out.println("可供替换机型："+aircraftType);
                int diff;
                if(siteNumA>siteNumB){
                    diff=siteNumA-siteNumB;
                }else{
                    diff=siteNumB-siteNumA;
                }
                stayTimeSum+=30*(siteNumA+siteNumB);//不同机型间调整有30分钟的成本
                stayTimeSum+=120*diff;//不能登机的人
                //System.out.println("替换机型:"+aircraftType);

            }

            long diffA=0L;
            if(Calculate.timestamp2100>startTimeStamp){
                diffA=(Calculate.timestamp2100-startTimeStamp);//航班A的延迟
            }
            stayTimeSum+=(diffA*siteNumA);//A航班的延迟 计入 总延时
            if(startTime<Calculate.timestamp2145){//B航班有延迟
                long diffB=Calculate.timestamp2145-startTimeStamp;//B航班的延迟
                stayTimeSum+=diffB*siteNumB;
            }
            if(min==0){
                min=stayTimeSum;
                minAircraftType=aircraftType;
            }else if(stayTimeSum<min){
                min=stayTimeSum;
                index=i;//选择第i个航班作为替换
                minAircraftType=aircraftType;
            }
            JSONObject replaceSchedule=(JSONObject)availableList.get(index);//选择延时最小的航班作为替换
            String aircraftId=replaceSchedule.getString("aircraftId");
            String rowNum=replaceSchedule.getString("rowNum");
            System.out.print("第"+rowNum+"行，飞机尾号: "+aircraftId+",机型："+minAircraftType);
            System.out.println(" 乘客总延迟为:"+min/60+"分钟");

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
            System.out.print("行号：" + rowNum + "，航班：" + scheduleIdLong + "，飞机尾号：" + aircraftId +",机型:"+aircraftType+" 置换 ");
            JSONObject index = judge(availableList, startTimeLong, aircraftType);
            if(index!=null){
                availableList.remove(index);
            }else{
                Long diff=Calculate.timestamp2100-startTimeLong;
                if(diff<0){
                    diff=Calculate.timestamp2145-startTimeLong;
                }
                if(diff>5*60*60){
                    System.out.println("失败,且延迟时间大于5个小时，取消航班");
                }else{
                    Map map=new HashMap();
                    map.put("9",87);
                    map.put("73H",158);
                    map.put("321",170);
                    map.put("3KR",296);
                    map.put("320",140);
                    map.put("77W",402);
                    map.put("32A",158);
                    map.put("332",241);
                    map.put("333",302);
                    Integer num= (Integer) map.get(aircraftType);
                    long wait=diff*num/60;

                    System.out.println("失败,延迟航班，乘客总延迟为"+wait+"分钟");
                }

            }
        }
    }
    public static void main(String[] args) throws IOException, ParseException {
        //Calculate.delayListShow(path,lastRowNum);
        //Calculate.availableListShow(path,lastRowNum);
        //Calculate.saveListShow(path,lastRowNum);
        finalResult();
    }
}

