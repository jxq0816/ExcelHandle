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
import java.util.ArrayList;
import java.util.List;

public class ReadExcel {
    public static List<List<String>> read(String path) throws IOException {
        InputStream inputStream=new FileInputStream(path);
        XSSFWorkbook hssfWorkbook=new XSSFWorkbook(inputStream);
        List<List<String>> result=new ArrayList<List<String>>();
        //循环每一页，并处理当前循环页
        for(int i=0;i<hssfWorkbook.getNumberOfSheets();i++){
            XSSFSheet hssfSheet=hssfWorkbook.getSheetAt(i);
            if(hssfSheet==null){
                continue;
            }
            //处理当前页，循环读取每一行
            for(int rowNum=1;rowNum<=hssfSheet.getLastRowNum();rowNum++){
                XSSFRow hssfRow=hssfSheet.getRow(rowNum);
                int minColIndex=hssfRow.getFirstCellNum();
                int maxColIndex=hssfRow.getLastCellNum();
                List<String> rowList=new ArrayList<String>();
                //遍历该行，获取处理每个cell元素
                for(int colIndex=minColIndex;colIndex<maxColIndex;colIndex++){
                    XSSFCell cell=hssfRow.getCell(colIndex);
                    if(cell==null){
                        continue;
                    }
                    rowList.add(cell.toString());
                }
                result.add(rowList);
            }
        }
        return result;
    }
    public static void main(String[] args) throws IOException {
        //String path="Users/jiangxingqi/IdeaProjects/ExcelUtil/doc/huizong.xlsx";
        String path="doc/huizong.xlsx";
        List<List<String>> result=read(path);
        System.out.println(result);
    }
}

