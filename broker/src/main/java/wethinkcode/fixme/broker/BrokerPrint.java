package wethinkcode.fixme.broker;

import wethinkcode.fixme.broker.FileHandle.ReadFile;

public class BrokerPrint {

    public static void clearScreen(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void startUpMessage(){
        System.out.println("********** BROKER **********\n\n" +
                "The market available to trade in is YayYay. Select option to continue.\n\n" +
                "1. Continue\n" +
                "2. Exit\n\n" +
                "Enter Input: ");
    }

    public static void marketContentsMessage(){
        String[] markets = ReadFile.ReadLine("source.txt");
        String line = markets[0];
        String[] items = line.split(",");

        System.out.println("********** BROKER **********\n\n" +
                "YayYay currently contains the following Instruments.\n" +
                "Select the one you would like to buy.\n\n" +
                "1. " + items[0].split(" ")[0] + " " + items[0].split(" ")[1] + " units / R" + items[0].split(" ")[2] + " price each\n" +
                "2. " + items[1].split(" ")[0] + " " + items[1].split(" ")[1] + " units / R" + items[1].split(" ")[2] + " price each\n" +
                "3. " + items[2].split(" ")[0] + " " + items[2].split(" ")[1] + " units / R" + items[2].split(" ")[2] + " price each\n\n"+
                "Enter Input: ");
    }

    public static void instrumentQuantity(){
        System.out.println("********** BROKER **********\n\n" +
                "Please enter a valid quantity of the instrument that\n" +
                "you wish to purchase/sell.\n\n" +
                "Enter Input: ");
    }

    public static void buyOrSell(){
        System.out.println("********** WELCOME TO BROKER **********\n\n" +
                "Would you like to purchase an instrument or purchase\n" +
                "an instrument that is already in your assets wallet?\n\n" +
                "1. BUY\n" +
                "2. SELL\n\n" +
                "Enter Input: ");
    }

    public static void showAssets(){
        String[] data = ReadFile.ReadLine("assets.txt");
        String[] items = data[0].split(",");

        System.out.println("*************** SELL ASSETS ***************\n" +
        "The assets available to sell in your wallet are:\n\n");
        int i = 1;
        for (String item : items){
            System.out.println(i++ + ". " + item.split(" ")[0] + " " + item.split(" ")[1] + " units\n");
        }
        System.out.println("Select one you want to sell\n"+
                "Enter input: ");
    }


    public static void showMarketAssets(){
        String[] markets = ReadFile.ReadLine("source.txt");
        String line = markets[0];
        String[] items = line.split(",");

        System.out.println("********** BROKER **********\n\n" +
                "YayYay sells instruments for the following prices the following Instruments.\n" +
                "Enter price you want to sell your instrument.\n\n" +
                "1. " + items[0].split(" ")[0] + " R" + items[0].split(" ")[2] + " price for each unit\n" +
                "2. " + items[1].split(" ")[0] + " R" + items[1].split(" ")[2] + " price for each unit\n" +
                "3. " + items[2].split(" ")[0] + " R" + items[2].split(" ")[2] + " price for each unit\n\n"+
                "Enter Input: ");
    }

}
