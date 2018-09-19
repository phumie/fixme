package wethinkcode.fixme.market.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.*;
import java.nio.charset.Charset;

public class WriteToFile {
    public static File file = null;
    private static FileWriter fWriter;

    public WriteToFile() {

    }

    public static void createFile(){
        try{
            if (file == null){
                file = new File("wallet.txt");
                file.createNewFile();
            }

            fWriter = new FileWriter(file, true);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static void writeToFile(String str){
        try{
            file = new File("source.txt");
            fWriter = new FileWriter(file, true);

            fWriter.append(str);
            fWriter.append('\n');
            fWriter.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
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
