import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Martin Melka /melkamar/
 *         Created on 21.11.14 22:29.
 */
public class Interpolator {
    private static int linesWritten = 0;

    public static int interpolateData(ArrayList<TransformSource> sources, int dataFrequency) {
        String dir = System.getProperty("user.dir") + "/data/";

        System.out.println("--------------------------------------------");
        System.out.println("     I N T E R P O L A T I O N   ");
        System.out.println("--------------------------------------------");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < sources.size(); i++) {
            long thisStartTime = System.currentTimeMillis();
            System.out.println("> Handling file " + sources.get(i).filename + "...");

            int res = 0;
            try {
                linesWritten = 0;
                fixMissingData(dir + sources.get(i).filename + ".trans", dataFrequency);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long thisTime = System.currentTimeMillis() - thisStartTime;
            System.out.println("  File [" + sources.get(i).filename + "] handled in " + (thisTime) / 1000 + "." + (thisTime % 1000) + " seconds");
            System.out.println("  Added " + linesWritten + " lines of data.");
        }

        return 0;
    }

    private static void fixMissingData(String filename, int dataFreq) throws IOException, ParseException {
        Scanner sc = new Scanner(new FileInputStream(filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".fixed"));

        String upLine = sc.nextLine();
        String downLine = sc.nextLine();

        while (true) {
            writer.write(upLine + "\n");
            linesWritten++;
            int dif = getDayDifference(upLine, downLine);
            if (dif != dataFreq) {
                interpolateData(upLine, downLine, writer, dif, dataFreq);
            }


            upLine = downLine;
            try {
                downLine = sc.nextLine();
            } catch (NoSuchElementException e) {
                writer.write(upLine + "\n");
                linesWritten++;
                writer.close();
                break;
            }
        }
    }

    private static int getDayDifference(String str1, String str2) throws ParseException {
        DateTime date1 = DateTime.parse(str1.split(",")[0], DateTimeFormat.forPattern("yyyy-MM-dd"));
        DateTime date2 = DateTime.parse(str2.split(",")[0], DateTimeFormat.forPattern("yyyy-MM-dd"));

        Days res = Days.daysBetween(date1, date2);
        return Math.abs(res.getDays());
    }

    private static void interpolateData(String str1, String str2, BufferedWriter writer, int difference, int dataFreq) throws ParseException, IOException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(str1.split(",")[0]);
        double val1 = Double.parseDouble(str1.split(",")[1]);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(str2.split(",")[0]);
        double val2 = Double.parseDouble(str2.split(",")[1]);


        int datesToCreate = (difference / dataFreq) - 1;
        int intervals = datesToCreate + 1;

        double delta = (val1 - val2) / intervals;

        double curVal = val1;

        Calendar cal = new GregorianCalendar();
        cal.setTime(date1);

        for (int i = 0; i < datesToCreate; i++) {
            cal.add(Calendar.DATE, -dataFreq);
            curVal -= delta;

            linesWritten++;
            writer.write(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + "," + curVal + "\n");
        }
    }

}
