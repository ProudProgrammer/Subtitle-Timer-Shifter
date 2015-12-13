package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Scanner;

public class AppUtil {

    static final long hourToMilliseconds = 60 * 60 * 1000;
    static final long minuteToMilliseconds = 60 * 1000;
    
    private AppUtil() {
    }

    public static int offset(File source, File dest, long seconds) {
        
        int returnCode = 0;

        Scanner scanner = null;
        PrintWriter writer = null;
        long offsetToSeconds = seconds;

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);

        try {
            scanner = new Scanner(source, "windows-1250");
            writer = new PrintWriter(dest);
            
            while(scanner.hasNext()) {

                String nextLine = scanner.nextLine();
                String[] timeStamp = nextLine.split(" --> ");
                
                if (timeStamp.length == 2) {

                    String[] startTimeStamp = timeStamp[0].split(":");
                    String[] endTimeStamp = timeStamp[1].split(":");

                    if (startTimeStamp.length == 3 && endTimeStamp.length == 3) {

                        returnCode = 1;

                        String[] startMinutes = startTimeStamp[2].split(",");
                        String[] endMinutes = endTimeStamp[2].split(",");

                        boolean isParsingSuccess = true;
                        long startMilliseconds = 0;
                        long endMilliseconds = 0;
                        try {
                            startMilliseconds = Long.parseLong(startTimeStamp[0]) * hourToMilliseconds + Long.parseLong(startTimeStamp[1]) * minuteToMilliseconds + Long.parseLong(startMinutes[0]) * 1000 + Long.parseLong(startMinutes[1]) + (offsetToSeconds * 1000);
                            endMilliseconds = Long.parseLong(endTimeStamp[0]) * hourToMilliseconds + Long.parseLong(endTimeStamp[1]) * minuteToMilliseconds + Long.parseLong(endMinutes[0]) * 1000 + Long.parseLong(endMinutes[1]) + (offsetToSeconds * 1000);
                        } catch (NumberFormatException e) {
                            isParsingSuccess = false;
                        }

                        if (isParsingSuccess && startMilliseconds >= 0 && endMilliseconds >= 0) {
                            long startTimeHours = startMilliseconds / hourToMilliseconds;
                            long startTimeMinutes = (startMilliseconds - (startTimeHours * hourToMilliseconds)) / minuteToMilliseconds;
                            long startTimeSeconds = (startMilliseconds - (startTimeHours * hourToMilliseconds) - (startTimeMinutes * minuteToMilliseconds)) / 1000;
                            long startTimeMilliseconds = (startMilliseconds - (startTimeHours * hourToMilliseconds) - (startTimeMinutes * minuteToMilliseconds) - startTimeSeconds * 1000);

                            long endTimeHours = endMilliseconds / hourToMilliseconds;
                            long endTimeMinutes = (endMilliseconds - (endTimeHours * hourToMilliseconds)) / minuteToMilliseconds;
                            long endTimeSeconds = (endMilliseconds - (endTimeHours * hourToMilliseconds) - (endTimeMinutes * minuteToMilliseconds)) / 1000;
                            long endTimeMilliseconds = (endMilliseconds - (endTimeHours * hourToMilliseconds) - (endTimeMinutes * minuteToMilliseconds) - endTimeSeconds * 1000);

                            writer.println(numberFormat.format(startTimeHours) + ":" + numberFormat.format(startTimeMinutes) + ":" + numberFormat.format(startTimeSeconds) + "," + (startTimeMilliseconds < 10 ? "00" + startTimeMilliseconds : startTimeMilliseconds < 100 ? "0" + startTimeMilliseconds : startTimeMilliseconds) + " --> "
                                    + numberFormat.format(endTimeHours) + ":" + numberFormat.format(endTimeMinutes) + ":" + numberFormat.format(endTimeSeconds) + "," + (endTimeMilliseconds < 10 ? "00" + endTimeMilliseconds : endTimeMilliseconds < 100 ? "0" + endTimeMilliseconds : endTimeMilliseconds));
                        } else {
                            writer.println(nextLine);
                        }

                    } else {
                        writer.println(nextLine);
                    }

                } else {
                    writer.println(nextLine);
                }
            }
        } catch (FileNotFoundException e) {
            returnCode = -1;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (writer != null) {
                writer.close();
            }
        }

        if(returnCode == 0) {
            dest.delete();
        }

        return returnCode;
    }
}
