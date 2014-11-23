import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Transformer {

//    public static int dataSamples = 2200;

    public static int transformFiles(ArrayList<TransformSource> sources, int dataSamples){
        long startTime = System.currentTimeMillis();


        System.out.println("--------------------------------------------");
        System.out.println("     T R A N S F O R M A T I O N   ");
        System.out.println("--------------------------------------------");
        for (int i = 0; i < sources.size(); i++) {
            int res = 0;
            try {
                long thisStartTime = System.currentTimeMillis();
                System.out.println("> Handling file " + sources.get(i).filename + "...");

                res = transformDatasource(sources.get(i).filename, dataSamples, Calendar.MONDAY, sources.get(i));

                if (res < 0) {
                    throw new Exception("  Returned code: " + res);
                }

                long thisTime = System.currentTimeMillis() - thisStartTime;
                System.out.println("  File [" + sources.get(i).filename + "] handled in " + (thisTime) / 1000 + "." + (thisTime % 1000) + " seconds");
                System.out.println("  " + res + " lines of data.");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long time = System.currentTimeMillis() - startTime;
        System.out.println();
        System.out.println("----------------------------");
        System.out.println("Transformation completed in " + (time) / 1000 + "." + (time % 1000) + " seconds.");
        System.out.println("\n\n\n");

        return 0;
    }

    private static int transformDatasource(String filename, int numberOfSamples, int dayOfWeek, TransformSource source) throws IOException {
        String fn = System.getProperty("user.dir") + "/../data/" + filename;

        FileInputStream fileInputStream = new FileInputStream(fn);
        String[] lines = splitDataSource(fileInputStream, numberOfSamples, dayOfWeek, source);

        // Writing to file
        int writtenLines = 0;
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fn + ".trans"));
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null || lines[i].isEmpty()) {
                writtenLines = i;
                break;
            }
            bufferedWriter.write(lines[i] + "\n");
        }
        if (writtenLines == 0) writtenLines = lines.length;
        bufferedWriter.close();

        return writtenLines;
    }

    private static String[] splitDataSource(FileInputStream fs, int numberOfSamples, int dayOfWeek, TransformSource source) {
        Scanner sc = new Scanner(fs);

        String[] out = new String[numberOfSamples];
        int samples = 0;
        boolean eof = false;
        Calendar cal = new GregorianCalendar();

        String line = null;
        while (!eof && (samples < numberOfSamples || numberOfSamples == -1)) {
            try {
                line = sc.nextLine();

                String[] fields = line.split(",");
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fields[0]));

                if (cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                    out[samples++] = buildLine(fields, source);
                }
            } catch (NoSuchElementException e) {
                eof = true;
                break;
            } catch (ParseException e) {
                System.out.println("    W: Could not parse line [" + line + "].");
            }
        }

        return out;
    }

    private static String buildLine(String[] fields, TransformSource source) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.includeColumns.length; i++) {
            builder.append(fields[source.includeColumns[i]]);
            if (i < source.includeColumns.length - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }
}


