package wethinkcode.fixme.broker;

import wethinkcode.fixme.broker.Client.BrokerClient;
import wethinkcode.fixme.broker.FileHandle.ReadFile;

import java.io.IOException;
import java.util.Scanner;

public class Broker {

    private static String market;
    private static String instrument = null;
    private static double quantity;
    private static int buyOrSell;
    private static double price;

    public static double setQuantity(){

        try {
            Scanner sc = new Scanner(System.in);
            quantity = sc.nextInt();
        }catch (Exception e){
            System.out.println("Error: Invalid Input, Please enter valid quantity.");
            setQuantity();
        }
        return quantity;
    }

    public static double sellQuantity(String instrument){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()){
                quantity = sc.nextInt();
                double sellQuantity = 0;

                String[] data = ReadFile.ReadLine("assets.txt");
                String[] items = data[0].split(",");

                for (String item : items){
                    if (item.contains(instrument)){
                        sellQuantity = Double.parseDouble(item.split(" ")[1]);
                    }
                }

                if (quantity < sellQuantity || quantity == sellQuantity){
                    break;
                }
                else
                    System.out.println("Error: Invalid Input, Please enter valid quantity.");
            }


        }catch (Exception e){
            System.out.println("Error: Invalid Input, Please enter valid quantity.");
            sellQuantity(instrument);
        }
        return quantity;
    }

    public static String setInstrument(){
        try {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()){
                String option = scanner.nextLine().trim();

                if (option.equals("Gold")) {
                    instrument = "Gold";
                    break;
                }
                else if (option.equals("Silver")) {
                    instrument = "Silver";
                    break;
                }
                else if (option.equals("Platinum")) {
                    instrument = "Platinum";
                    break;
                }
                else
                    System.out.println("Error: Invalid Input. Try again.");
            }

        }catch (Exception ex){
            System.out.println("Error: Invalid Input, Please enter valid Instrument Code.");
            setInstrument();
        }
        return instrument;
    }

    public static String setMarket(){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()){
                int option = sc.nextInt();

                if (option == 1) {
                    market = "YayYay";
                    break;
                }
                else if (option == 2) {
                    System.out.println("----- GOODBYE -----\n");
                    System.exit(0);
                }
                else
                    System.out.println("Error: Invalid Input. Try again.");
            }
        }catch (Exception e){
            System.out.println("Error: Invalid Input, Please enter valid corresponding market Index.");
            setMarket();
        }
        return market;
    }

    public static int setBuyOrSell(){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()) {
                buyOrSell = sc.nextInt();

                if (buyOrSell > 2 || buyOrSell < 1)
                    System.out.println("Error: Invalid Input. Try again.");
                else
                    break;
            }
        }catch (Exception e){
            System.out.println("Error: Invalid Input, Please enter valid corresponding market Index.");
            setBuyOrSell();
        }

        return buyOrSell;
    }

    public static double sellPrice(){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()){
                price = sc.nextDouble();

                if (price > 0)
                    break;
                else
                    System.out.println("Error: Invalid Input. Try again.");
            }
        }catch (Exception e){
            System.out.println("Error: Invalid Input, Please enter valid corresponding market Index.");
            setMarket();
        }
        return price;
    }

}
