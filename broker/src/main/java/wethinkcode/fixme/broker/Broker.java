package wethinkcode.fixme.broker;

import wethinkcode.fixme.broker.Client.BrokerClient;

import java.io.IOException;
import java.util.Scanner;

public class Broker {

    private static String market;
    private static String instrument = null;
    private static int quantity;
    private static int buyOrSell;

//    public static void main(String[] args) {

//        BrokerPrint.buyOrSell();
//        setBuyOrSell();
//
//        if (buyOrSell == 1){
//            BrokerPrint.startUpMessage();
//            setMarket();
//            BrokerPrint.marketContentsMessage();
//            setInstrument();
//            BrokerPrint.priceEnquiry();
//            setPrice();
//        }
//        else if (buyOrSell == 2){
//            BrokerPrint.showAssets();
//        }


//        BrokerClient client = new BrokerClient();
//        try {
//            client.startClient();
//        } catch (IOException e ) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static int setQuantity(){

        try {
            Scanner sc = new Scanner(System.in);
            quantity = sc.nextInt();
        }catch (Exception e){
            System.out.println("Error: Invalid Input, Please enter valid price.");
            setQuantity();
        }
        return quantity;
    }

    public static String setInstrument(){

        try {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()){
                int option = scanner.nextInt();

                if (option == 1) {
                    instrument = "Gold";
                    break;
                }
                else if (option == 2) {
                    instrument = "Silver";
                    break;
                }
                else if (option == 3) {
                    instrument = "Platinum";
                    break;
                }
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

        if (buyOrSell == 2){
            //TODO: CREATE A WALLET AND SEND THROUGH DETAILS SO WE CAN CONSTRUCT A SELL FIX MESSAGE.
        }
        return buyOrSell;
    }

}
