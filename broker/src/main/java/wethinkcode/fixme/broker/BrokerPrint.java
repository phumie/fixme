package wethinkcode.fixme.broker;

import wethinkcode.fixme.broker.FileHandle.ReadFile;

public class BrokerPrint {

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
                "1. " + items[1] + "\n" +
                "2. " + items[2] + "\n" +
                "1. " + items[3] + "\n\n" +
                "Enter Input: ");
    }

    public static void instrumentQuantity(){
        System.out.println("********** BROKER **********\n\n" +
                "Please enter a valid quantity of the instrument that\n" +
                "you wish to purchase.\n\n" +
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
        String[] items = ReadFile.ReadLine("assets.txt");

        System.out.println("*************** SELL ASSETS ***************\n" +
        "The assets available to sell in your wallet are:\n\n");

        for (String item : items){
            System.out.println("*" + item.split(" ")[0] + " " + item.split(" ")[1] + " units\n");
        }
    }
}
