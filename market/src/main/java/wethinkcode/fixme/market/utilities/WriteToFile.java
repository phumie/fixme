package wethinkcode.fixme.market.utilities;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.charset.Charset;

public class WriteToFile {
    public static File file = null;
    private static FileWriter fWriter;

    public WriteToFile() {

    }

    public static int getLinesCount(String filename){
        try{
            File file = new File(filename);
            FileReader fReader = new FileReader(file);
            LineNumberReader lReader = new LineNumberReader(fReader);
            lReader.skip(Long.MAX_VALUE);
            int count = lReader.getLineNumber();
            lReader.close();
            return count;
        }
        catch (IOException ioe){
            ioe.getMessage();
        }
        return -1;
    }
    public static String[] ReadLine(String filename){
        try{
            File file = new File(filename);
            FileReader fReader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(fReader);
            String line = null;
            String items[] = new String[getLinesCount(filename) + 1];
            int index = 0;

            while ((line = bReader.readLine()) != null){
                items[index] = line;
                index++;
            }
            bReader.close();
            return items;
        }
        catch (IOException ioe){
            ioe.getMessage();
        }
        return null;
    }


    public static void updateFile(String instrument, String filename){
        try{
            String[] items = ReadLine(filename);
            String deleteLine = null;
            String newLine = null;
            File file = new File("assets.txt");
            FileWriter fWriter = new FileWriter(file);

            for (String line : items){
                if (line.contains(instrument.split(" ")[0]))
                    deleteLine = line;
            }

            for (String line : items) {
                if (line.equals(deleteLine))
                    fWriter.write(newLine + "\n");
                else
                    fWriter.write(line + "\n");
            }
            fWriter.close();
        }
        catch (IOException ioe){
            System.out.println("Error updating hero stats in file: " + ioe);
        }

    }

    public static void closeFile(){
        try{
            if (fWriter != null)
                fWriter.close();
            else
                return;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
