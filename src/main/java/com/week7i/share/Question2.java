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


    public static void main(String[] args) throws IOException, ParseException {
        List availableList=Calculate.available(path,lastRowNum);//获得可供替换的航班集合,共计7个
    }
}
