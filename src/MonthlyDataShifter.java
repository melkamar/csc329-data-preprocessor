import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Martin Melka /melkamar/
 *         Created on 23.11.14 0:34.
 */
public class MonthlyDataShifter {
    public static void shiftToNextMonday(ArrayList<TransformSource> sources) {
        String dir = System.getProperty("user.dir") + "/../data/";

        System.out.println("--------------------------------------------");
        System.out.println("     S H I F T I N G   ");
        System.out.println("--------------------------------------------");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < sources.size(); i++) {
            long thisStartTime = System.currentTimeMillis();
            System.out.println("> Handling file " + sources.get(i).filename + "...");

            int res = 0;
            try {
                shiftDates(dir + sources.get(i).filename);
            } catch (IOException e) {
                e.printStackTrace();
            }

            long thisTime = System.currentTimeMillis() - thisStartTime;
            System.out.println("  File [" + sources.get(i).filename + "] handled in " + (thisTime) / 1000 + "." + (thisTime % 1000) + " seconds");
        }
    }

    private static void shiftDates(String filename) throws IOException {
        Scanner sc = new Scanner(new FileInputStream(filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".shifted"));

        while (true) {
            String line;
            try {
                line = sc.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }

            String dateStr = line.split(",")[0];
            String valStr = line.split(",")[1];

            DateTime date;
            try {
                date = DateTime.parse(dateStr, DateTimeFormat.forPattern("yyyy-MM-dd"));
            } catch (IllegalArgumentException e){
                continue;
            }
            System.out.println("Date " + dateStr + " is dow: " + date.getDayOfWeek());


            while (date.getDayOfWeek() != 1){
                date = date.plusDays(1);
            }

            System.out.println("  -> is now "+date.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));

            writer.write(date.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))+","+valStr+"\n");
        }

        writer.close();
    }
}
