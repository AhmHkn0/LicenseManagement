package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.Tab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class utilities {

    public static Alert showInformationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }


    public static int getLicenseID(String queryResult) {
        return Integer.parseInt(queryResult.split("##")[0]);
    }

    public static String getLicenseIP(String queryResult) {
        return queryResult.split("##")[1];
    }

    public static int getLicenseActive(String queryResult) {
        return Integer.parseInt(queryResult.split("##")[2]);
    }

    public static String getLicenseDate(String queryResult) {
        return formatDate(queryResult.split("##")[3]);
    }


    public static int getLicenseBlock(String queryResult) {
        return Integer.parseInt(queryResult.split("##")[4]);
    }

    public static int getCustomerID(String queryResult) {
        return Integer.parseInt(queryResult.split("##")[5]);
    }

    public static String getCustomerIP(String queryResult) {
        return queryResult.split("##")[6];
    }

    public static String getCustomerName(String queryResult) {
        return queryResult.split("##")[7];
    }

    public static long getCustomerDiscordID(String queryResult) {
        return Long.parseLong(queryResult.split("##")[8]);
    }


    private static String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
