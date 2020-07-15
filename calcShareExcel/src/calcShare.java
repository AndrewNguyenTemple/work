import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class calcShare {
    static String sDate = "2020-01-13";
    static Date startDate;
    static int timeInterval = 14;
    static int amt = 414;
    static double avgShare = 0;

    public static void main(String[] args) throws ParseException {
        int maxDay = 0;
        int minDay = 0;
        double maxShare = 0;
        double minShare = 9999;
        double totalShare = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        startDate = format.parse(sDate);

        for (int i = 2; i <= 6; i++) {
            dayInstance dInst = new dayInstance(i);
            calcTotalShare(dInst);
            if(dInst.getTotalShare() > maxShare){
                maxDay = dInst.getDay();
                maxShare = dInst.getTotalShare();
            }
            if(dInst.getTotalShare() < minShare){
                minDay = dInst.getDay();
                minShare = dInst.getTotalShare();
            }
        }
        avgShare = avgShare / 5;
        System.out.println(maxShare);
        System.out.println(maxDay);
    }

    private static void calcTotalShare(dayInstance d1) {
        String excelFilePath = "C:\\Users\\andre\\IdeaProjects\\calcShareExcel\\src\\UNH-buyStrategy.xls";
        boolean startFlag = false;
        double totalShares = 0;
        int day = d1.getDay();

        Date currentDate;
        Date targetDate;

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        targetDate = c.getTime();

        try {
            FileInputStream inputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new HSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.iterator();

            rowIterator.next();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                Cell nextCell = cellIterator.next();
                currentDate = nextCell.getDateCellValue();
                c.setTime(currentDate);
                int currentDay = c.get(Calendar.DAY_OF_WEEK);

                int flag = currentDate.compareTo(startDate);
                if (flag >= 0) {

                    if (currentDay == day && startFlag == false) {
                        startFlag = true;
                        cellIterator.next();
                        cellIterator.next();
                        cellIterator.next();
                        nextCell = cellIterator.next();
                        double shareVal = nextCell.getNumericCellValue();
                        totalShares += (amt / shareVal);
                        c.add(Calendar.DATE, timeInterval);
                        targetDate = c.getTime();
                    }

                    if (targetDate.compareTo(currentDate) == 0 && startFlag == true) {
                        cellIterator.next();
                        cellIterator.next();
                        cellIterator.next();
                        nextCell = cellIterator.next();
                        double shareVal = nextCell.getNumericCellValue();
                        totalShares += (amt / shareVal);
                        c.add(Calendar.DATE, timeInterval);
                        targetDate = c.getTime();

                    } else if (currentDate.compareTo(targetDate) > 0 && startFlag == true) {
                        cellIterator.next();
                        cellIterator.next();
                        cellIterator.next();
                        nextCell = cellIterator.next();
                        double shareVal = nextCell.getNumericCellValue();
                        totalShares += (amt / shareVal);
                        long daysBetween = ChronoUnit.DAYS.between(targetDate.toInstant(), currentDate.toInstant());
                        c.add(Calendar.DATE, timeInterval - (int)daysBetween);
                        targetDate = c.getTime();
                    }
                }
            }
            d1.setTotalShares(totalShares);
            avgShare += totalShares;
            workbook.close();

        } catch (IOException ex1) {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        }

    }
}
