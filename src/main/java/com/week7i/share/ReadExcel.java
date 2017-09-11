package com.week7i.share;

import com.week7i.share.util.CSVUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadExcel {
    public static List<List<String>> read(String path,int num) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(inputStream);
        List<List<String>> result = new ArrayList<List<String>>();
        //循环每一页，并处理当前循环页
        for (int i = 0; i < hssfWorkbook.getNumberOfSheets(); i++) {
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(i);
            if (hssfSheet == null) {
                continue;
            }
            //处理表头
            XSSFRow titleRow = hssfSheet.getRow(0);
            int minIndex = titleRow.getFirstCellNum();
            int maxIndex = titleRow.getLastCellNum();
            List<String> titleRowList = new ArrayList<String>();
            //遍历该行，获取处理每个cell元素
            for (int j = minIndex; j < maxIndex; j++) {

                XSSFCell cell = titleRow.getCell(j);
                if (cell == null) {
                    continue;
                }
                String cellString=cell.toString();
                if(cellString!=""){
                    titleRowList.add(cellString);
                }
            }
            //System.out.println(titleRowList);
            //result.add(titleRowList);

            //处理当前页，循环读取每一行
            for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                int maxColIndex = hssfRow.getLastCellNum();
                XSSFCell idCell = hssfRow.getCell(0);
                String id=idCell.toString();
                //遍历该行，获取处理每个cell元素
                for (int colIndex = 1; colIndex < maxColIndex; colIndex++) {
                    if ((num==12&&colIndex%12==0) || colIndex % 12 == num) {
                        int titleIndex=(colIndex-1)/12+1;
                        String title=titleRowList.get(titleIndex);
                        List<String> rowList = new ArrayList<String>();
                        XSSFCell cell = hssfRow.getCell(colIndex);
                        if (cell == null) {
                            continue;
                        }
                        rowList.add(id);
                        rowList.add(title);
                        rowList.add(cell.toString());
                        result.add(rowList);
                    }
                }

            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        //String path="Users/jiangxingqi/IdeaProjects/ExcelUtil/doc/huizong.xlsx";
        String path = "doc/huizong.xlsx";
        for(int num=12;num<=12;num++){
            List<List<String>> result = read(path,num);
            for (int i = 0; i < result.size(); i++) {
                System.out.println(result.get(i));
            }
            ArrayList csvList = new ArrayList<String>();
            for (int i = 0; i < result.size(); i++) {
                List<String> csv = result.get(i);
                String s = "";
                for (int j = 0; j < csv.size(); j++) {
                    s += csv.get(j);
                    if (j != csv.size() - 1) {
                        s += ",";
                    }
                }
                csvList.add(s);
            }
            if (csvList.size() > 1) {
                CSVUtils.exportCsv(new File("doc/"+num+".csv"), csvList);
            }
        }

    }
}

