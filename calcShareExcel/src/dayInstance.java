import java.util.Date;

public class dayInstance {
    private static double totalShares;
    private static int day;

    public dayInstance(int d) {
    day = d;
    }

    public static void setStartDate(int d) {
        day = d;
    }

    public static void setTotalShares(double tS){
        totalShares = tS;
    }

    public static double getTotalShare(){
        return totalShares;
    }

    public static int getDay(){
        return day;
    }



}
