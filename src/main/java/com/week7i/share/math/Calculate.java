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
import java.util.*;

public class Calculate {
    public static Long timestamp2100=1461358800L;//21：00的时间戳
    public static Long timestamp1800=1461348000L;//18：00的时间戳
    public static Long timestamp2145= timestamp2100+45*60;//21：45的时间戳,前一航班到达时间与后一航班起飞时间之间的最小间隔时间为45分钟

    public static List availableList(String path,int lastRowNum) throws IOException, ParseException {
        List result = new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

        //处理当前页，循环读取每一行
        for (int rowNum = 2; rowNum <=lastRowNum ; rowNum++) {
            XSSFRow xssfRowBefore = xssfSheet.getRow(rowNum-1);
            XSSFCell arrive = xssfRowBefore.getCell(4);//上一行到达机场必须是OVS
            String arriveCellData = arrive.toString();

            XSSFCell arriveTimeCell = xssfRowBefore.getCell(2);//降落时间必须是18点之前
            String arriveTimeCellData = arriveTimeCell.toString();
            BigDecimal arriveTimeBd = new BigDecimal(arriveTimeCellData);
            Long arriveTimeLong = Long.parseLong(arriveTimeBd.toPlainString());


            XSSFRow xssfRow = xssfSheet.getRow(rowNum);

            XSSFCell leaveCell = xssfRow.getCell(3);//起飞机场必须是OVS
            String leaveCellData = leaveCell.toString();

            XSSFCell leaveTimeCell = xssfRow.getCell(1);//起飞时间必须是21：45之后
            String leaveTimeCellData = leaveTimeCell.toString();
            BigDecimal leaveTimeCellBd = new BigDecimal(leaveTimeCellData);
            Long leaveTimeCellLong = Long.parseLong(leaveTimeCellBd.toPlainString());


            if("OVS".equals(arriveCellData)&&"OVS".equals(leaveCellData)){
                if((arriveTimeLong<=timestamp1800)){
                    if(leaveTimeCellLong>=timestamp2100){
                        result = addAccessToResult(xssfRow, rowNum, result);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 添加航班进入可供替换的集合
     * @param xssfRow
     * @param rowNum
     * @param rs
     * @return
     */
    public static List addAccessToResult(XSSFRow xssfRow,int rowNum,List rs){
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
            XSSFCell destination = xssfRow.getCell(4);//到达机场，只能延迟
            String cellData = destination.toString();
            if (cellData.equals("OVS")) {
                XSSFCell endTime = xssfRow.getCell(2);//到达时间
                String endTimeString = endTime.toString();
                BigDecimal endTimeBD = new BigDecimal(endTimeString);
                Long l = Long.parseLong(endTimeBD.toPlainString());
                if (l>timestamp1800 && l<timestamp2100) {
                    Long delayMinute=(timestamp2100-l)/60;//延迟时间,以分钟为单位
                    result = addDelayToResult(xssfRow, rowNum, delayMinute,result);
                }
                continue;
            }
            XSSFCell leave = xssfRow.getCell(3);//起飞机场,对于从OVS起飞的航班才有替换的情况
            String leaveCellData = leave.toString();
            if (leaveCellData.equals("OVS")) {
                XSSFCell startTime = xssfRow.getCell(1);//起飞时间
                String startTimeString = startTime.toString();
                BigDecimal bd = new BigDecimal(startTimeString);
                Long leaveTimeLong = Long.parseLong(bd.toPlainString());
                if (leaveTimeLong>timestamp1800 && leaveTimeLong < timestamp2100) {
                    Long delayMinute=(timestamp2100-leaveTimeLong)/60;//延迟时间,以分钟为单位
                    result = addDelayToResult(xssfRow, rowNum, delayMinute,result);
                }
            }
        }
        return result;
    }

    /**
     * 查询可以替换，可以节约时间的航班
     * @param path
     * @param lastRowNum
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List saveList(String path,int lastRowNum) throws IOException, ParseException {
        List result = new ArrayList();
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

        //处理当前页，循环读取每一行
        for (int rowNum = 2; rowNum <=lastRowNum ; rowNum++) {
            XSSFRow xssfRowBefore = xssfSheet.getRow(rowNum-1);
            XSSFCell arrive = xssfRowBefore.getCell(4);//上一行到达机场必须是OVS
            String arriveCellData = arrive.toString();

            XSSFCell arriveTimeCell = xssfRowBefore.getCell(2);//上一行到达机场必须是OVS
            String arriveTimeCellData = arriveTimeCell.toString();
            BigDecimal arriveTimeBd = new BigDecimal(arriveTimeCellData);
            Long arriveTimeLong = Long.parseLong(arriveTimeBd.toPlainString());

            if("OVS".equals(arriveCellData)&&(arriveTimeLong>timestamp1800)&&(arriveTimeLong<timestamp2100)){
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                XSSFCell leave = xssfRow.getCell(3);//起飞机场,对于从OVS起飞的航班才有替换的情况
                String leaveCellData = leave.toString();

                if (leaveCellData.equals("OVS")) {
                    XSSFCell startTime = xssfRow.getCell(1);//起飞时间
                    String startTimeString = startTime.toString();
                    BigDecimal bd = new BigDecimal(startTimeString);
                    Long leaveTimeLong = Long.parseLong(bd.toPlainString());
                    if (leaveTimeLong > timestamp1800 && leaveTimeLong < timestamp2145) {
                        Long saveMinute;
                        if(leaveTimeLong>timestamp2100){
                            saveMinute=(timestamp2145-leaveTimeLong)/60;//可节约时间,以分钟为单位
                        }else{
                            saveMinute=(timestamp2145-timestamp2100)/60;
                        }
                        result = addSaveToResult(xssfRow, rowNum, saveMinute,result);//可节约时间的集合
                        //System.out.println(result);
                    }
                }
            }
        }
        return result;
    }
    /**
     * 添加受到暴风雪影响的航班
     * @param xssfRow
     * @param rowNum
     * @param delayMinute
     * @param rs
     * @return
     */
    public static List addDelayToResult(XSSFRow xssfRow,int rowNum,Long delayMinute,List rs){
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
        obj.put("startTimeLong",startTimeLong);//起飞时间
        obj.put("aircraftType",aircraftType);//飞机机型
        obj.put("delayMinute",delayMinute);//延时
        rs.add(obj);
        return rs;
    }

    /**
     * 添加可以节约时间的航班
     * @param xssfRow
     * @param rowNum
     * @param saveMinute
     * @param rs
     * @return
     */
    public static List addSaveToResult(XSSFRow xssfRow,int rowNum,Long saveMinute,List rs){
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
        obj.put("startTimeLong",startTimeLong);//起飞时间
        obj.put("aircraftType",aircraftType);//飞机机型
        obj.put("saveMinute",saveMinute);//可以节约的时间
        rs.add(obj);
        return rs;
    }

    /**
     * 集合分类
     * @param availableList
     */
    public static void setByAircraftType(List availableList){
        Map rs=new HashMap();
        for(int i=0;i<availableList.size();i++){
            JSONObject object= (JSONObject) availableList.get(i);
            String aircraftType=object.getString("aircraftType");
            if(rs.containsKey(aircraftType)){
                List list= (List) rs.get(aircraftType);
                list.add(object);
            }else{
                ArrayList list=new ArrayList();
                list.add(object);
                rs.put(aircraftType,list);
            }
        }
        Iterator<Map.Entry<String, ArrayList>> entries = rs.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, ArrayList> entry = entries.next();
            ArrayList list=entry.getValue();
            System.out.println("飞机型号" + entry.getKey());

            for(int i=0;i<list.size();i++){
                JSONObject object= (JSONObject) list.get(i);
                String rowNum=object.getString("rowNum");
                String aircraftId=object.getString("aircraftId");
                String scheduleIdLong=object.getString("scheduleIdLong");
                Long saveTime=object.getLong("saveMinute");

                System.out.print("行号:"+rowNum+"；航班号："+scheduleIdLong+"；飞机尾号:"+aircraftId+" ");
                if(saveTime!=null){
                    System.out.print(";最大可优化="+saveTime+"分钟");
                }
                System.out.println();
            }
            System.out.println();

        }
    }
    /**
     * 延迟的航班集合,并计算延迟时间，将求出延迟时间填入EXCEL
     * @throws IOException
     * @throws ParseException
     */
    public static void delayListShow(String path,int lastRowNum) throws IOException, ParseException {
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
     * 展示可以通过航班替换，减少时间延迟的航班
     * @throws IOException
     * @throws ParseException
     */
    public static void saveListShow(String path,int lastRowNum) throws IOException, ParseException {
        List saveList=Calculate.saveList(path,lastRowNum);
        Calculate.setByAircraftType(saveList);
    }
    /**
     * 获得可以作为替换的航班集合
     * @throws IOException
     * @throws ParseException
     */
    public static void availableListShow(String path,int lastRowNum) throws IOException, ParseException {
        List availableList=Calculate.availableList(path,lastRowNum);//获得可供替换的航班集合,共计7个
        Calculate.setByAircraftType(availableList);
    }

    /**
     *5分钟起停5辆的限制
     * @param path
     * @param lastRowNum
     */
    public static void fiveMinuteLimit(String path,int lastRowNum) throws IOException {
        Long start=timestamp2100;
        Long end=timestamp2145+24*60*60;
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(1);

        for(long i=start;i<=end;i+=(5*60)){
            //处理当前页，循环读取每一行
            int cnt=0;
            for (int rowNum = 1; rowNum <=lastRowNum ; rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                XSSFCell destination = xssfRow.getCell(4);//到达机场，只能延迟
                String cellData = destination.toString();
                if (cellData.equals("OVS")) {
                    XSSFCell endTime = xssfRow.getCell(11);//到达时间
                    String endTimeString = endTime.toString();
                    Date date=new Date(endTimeString);
                    long time=date.getTime()/1000;
                    if (time>i && time<(i+(5*60))) {
                        cnt++;
                        if(cnt>5){
                            System.out.println("超出限制");
                            break;
                        }
                    }
                }
            }
        }
    }

}

