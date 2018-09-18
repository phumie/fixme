package wethinkcode.fixme.market.utilities;

import wethinkcode.fixme.market.Commodity;
import wethinkcode.fixme.market.Market;

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class HandleFile {

    private ArrayList<Market> markets;
    private static HandleFile handleFile;

    public HandleFile () {

    }

    public static HandleFile getInstance() {
        if (handleFile == null)
            handleFile = new HandleFile();
        return handleFile;
    }

    public ArrayList<Market> GetMarketsFromTextFile () {

        this.markets = new ArrayList<>();
        BufferedReader readFile = null;
        String line;
        try {
            readFile = new BufferedReader(new FileReader("source.txt"));
            while ((line = readFile.readLine()) != null) {
                String []marketProperties = line.split(" ");

                Market newMarket = new Market(marketProperties[0],
                        new Commodity(marketProperties[1], Double.parseDouble(marketProperties[2]), Double.parseDouble(marketProperties[3])),
                        new Commodity(marketProperties[4], Double.parseDouble(marketProperties[5]), Double.parseDouble(marketProperties[6])),
                        new Commodity(marketProperties[7], Double.parseDouble(marketProperties[8]), Double.parseDouble(marketProperties[9])));
                this.markets.add(newMarket);
            }

        } catch (IOException e) {
            System.out.println("Unable to open the file.");
            System.exit(0);
        } catch (NoSuchElementException e) {
            System.out.println("Please ensure the file is in the correct format.");
            System.exit(0);
        } catch (NumberFormatException e) {
            System.out.println("Please ensure that all numbers are in the correct format.");
            System.exit(0);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("There appears to be an incomplete line in the file. Make sure that all fields are filled in");
            System.exit(0);
        }

        finally {
            try {
                readFile.close();
            } catch (IOException e) {
                System.out.println("File did not close.");
            }
        }
        return this.markets;
    }

    public ArrayList<Market> getMarkets(){
        return markets;
    }
}
