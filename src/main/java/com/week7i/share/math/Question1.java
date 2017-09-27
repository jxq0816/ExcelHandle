package com.week7i.share.math;

import com.alibaba.fastjson.JSONObject;
import com.week7i.share.math.Calculate;

import java.io.IOException;
import java.text.ParseException;
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
        if(availableList.size()==0){
            return null;
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
    public static void saveListShow() throws IOException, ParseException {
        List availableList = Calculate.saveList(path, lastRowNum);//获得可供替换的航班集合
        Calculate.setByAircraftType(availableList);
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
            JSONObject index = judge(availableList, startTimeLong);
            if(availableList!=null&&availableList.size()!=0){
                availableList.remove(index);
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
                }
                System.out.println();
            }
        }
    }
    public static void main(String[] args) throws IOException, ParseException {
        Calculate.delayListShow(path,lastRowNum);
        //Calculate.saveListShow(path,lastRowNum);
        //Calculate.availableListShow(path,lastRowNum);
        //finalResult();
        //Calculate.fiveMinuteLimit(path,lastRowNum);
    }
}

