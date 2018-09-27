package wethinkcode.fixme.broker.Client;

import wethinkcode.fixme.broker.Broker;
import wethinkcode.fixme.broker.BrokerPrint;
import wethinkcode.fixme.broker.FileHandle.ReadFile;
import wethinkcode.fixme.broker.FixMessage.FixMessageFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class BrokerClient {

    private static Selector selector;
    private InetSocketAddress hostAddress;
    private static SocketChannel client;
    private ByteBuffer buffer;
    private  String messages, returnMessage;
    private static String fixMessage;
    private BufferedReader bufferedReader;
    private static boolean idFlag;
    private boolean sentFlag;
    private static String clientID;
    private static String market;
    private static String instrument;
    private static double price;
    private static double quantity;
    private static int buysell;
    private static int flag;
    private static Calendar cal = Calendar.getInstance();
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


    public BrokerClient() {
        try {
            this.selector = Selector.open();
            this.hostAddress = new InetSocketAddress("localhost", 5000);
            this.client = SocketChannel.open(this.hostAddress);
            this.client.configureBlocking(false);
            int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
            this.client.register(this.selector, operations);
            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.buffer = ByteBuffer.allocate(1024);

        }
        catch (IOException e) {
            System.out.println("Error: A problem occurred whilst initializing the client");
            try {
                this.stop();
            }
            catch (IOException i) {
                System.out.println("Error: A problem occurred whilst closing the client");
            }
        }
    }

    public void startClient() throws Exception {
        System.out.println("----- BROKER STARTED -----\n");
        System.out.println(sdf.format(cal.getTime()) + " [BROKER]: Connected to Router");

        while (true){
            if (this.selector.select() == 0)
                continue;
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            fixMessageGen();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (!key.isValid())
                    continue;
                if (key.isConnectable()) {
                    boolean connected = processConnect(key);
                    if (!connected)
                        stop();
                }
                if (key.isReadable())
                {
                    this.read();
                    break;
                }

                if (key.isWritable())
                    this.writeToClient();
            }
        }
    }


    private void read () throws  Exception {
        client.read(buffer);
        messages = new String(buffer.array()).trim();

        if (!idFlag) {
            System.out.println(sdf.format(cal.getTime()) + " [BROKER]: BrokerID -> " + messages);
            clientID = messages;
            idFlag = true;
            client.register(selector, SelectionKey.OP_READ);
        }
        else{
            System.out.println(sdf.format(cal.getTime()) + " [BROKER]: Message from Market -> " + messages);
            checkMarketMessage(messages);
            client.register(selector, SelectionKey.OP_READ);
//            returnMessage =
        }
        buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE );
    }

    private void checkMarketMessage(String messages) {
        String[] tags =  messages.split("\\|");

        for (String item : tags){
            if (item.equals("35=Executed")){
                if (buysell == 1)
                    System.out.println(sdf.format(cal.getTime()) + " [BROKER]: BUY EXECUTED");
                else if (buysell == 2)
                    System.out.println(sdf.format(cal.getTime()) + " [BROKER]: SELL EXECUTED");
                String a = tags[6].split("=")[1];
                double b = Double.parseDouble(tags[8].split("=")[1]);
                ReadFile.updateFile(a, b, buysell, "assets.txt");
                break;
            }
            else if (item.equals("35=Rejected")){
                if (buysell == 1)
                    System.out.println(sdf.format(cal.getTime()) + " [BROKER]: BUY REJECTED");
                else if (buysell == 2)
                    System.out.println(sdf.format(cal.getTime()) + " [BROKER]: SELL REJECTED");
                break;
            }
        }
    }


    public void writeToClient() throws Exception {
        if (!sentFlag){
            this.buffer = ByteBuffer.allocate(1024);
            this.buffer.put(fixMessage.getBytes());
            this.buffer.flip();
            client.write(this.buffer);
            System.out.println(sdf.format(cal.getTime()) + " [BROKER]: Fix Message Generated -> " + fixMessage);
            this.buffer.clear();
            this.client.register(this.selector, SelectionKey.OP_READ);
            System.out.println(sdf.format(cal.getTime()) + " [BROKER]: Fix Message sent to Router for Market");
            sentFlag = true;
        }

    }

    private String checkSumCalculator(String message){
        String checkSum;
        String checkSumMessage = message.replace('|', '\u0001');
        byte[] messageBytes = checkSumMessage.getBytes(StandardCharsets.US_ASCII);
        int total = 0;

        for (int i = 0; i < message.length(); i++)
            total += messageBytes[i];

        int CalculatedChecksum = total % 256;
        checkSum = Integer.toString(CalculatedChecksum - 1) ;

        return checkSum;
    }

    public void stop() throws IOException {
        this.client.close();
        this.buffer = null;
    }

    public static boolean processConnect(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    public void fixMessageGen() throws Exception {
        fixMessage = FixMessageFactory.fixMessage(clientID, buysell, market, instrument, price,  quantity);
        fixMessage = fixMessage + "10=" + checkSumCalculator(fixMessage);
        client.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args){
        BrokerPrint.buyOrSell();
        buysell = Broker.setBuyOrSell();
        if (buysell == 1){
            BrokerPrint.clearScreen();
            BrokerPrint.startUpMessage();
            market = Broker.setMarket();
            BrokerPrint.clearScreen();
            BrokerPrint.marketContentsMessage();
            instrument = Broker.setInstrument();
            BrokerPrint.clearScreen();
            BrokerPrint.instrumentQuantity();
            quantity = Broker.setQuantity();

            if (instrument.equals("Gold"))
                price = 1208.0 * quantity;
            else if (instrument.equals("Silver"))
                price = 909.0 * quantity;
            else if (instrument.equals("Platinum"))
                price = 1889.0 * quantity;
        }
        else if (buysell == 2){

            BrokerPrint.clearScreen();
            BrokerPrint.startUpMessage();
            market = Broker.setMarket();
            BrokerPrint.showAssets();
            instrument = Broker.setInstrument();
            BrokerPrint.clearScreen();
            BrokerPrint.instrumentQuantity();
            quantity = Broker.sellQuantity(instrument);
            BrokerPrint.clearScreen();
            BrokerPrint.showMarketAssets();
            price = Broker.sellPrice();
        }
        System.out.println("\n\n********** WELCOME TO BROKER **********\n\n" +
                "Your purchase/sell request sending as FIX message\n" +
                "1. Continue purchase\n" +
                "2. Quit purchasing\n\n");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            int option = scanner.nextInt();
            if (option == 1) {
                BrokerClient client = new BrokerClient();

                try {
                    client.startClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (option == 2){
                System.out.println("----- GOODBYE -----\n");
                System.exit(0);
            }
        }
        scanner.close();
    }
}
