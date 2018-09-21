package wethinkcode.fixme.broker.Client;

import wethinkcode.fixme.broker.Broker;
import wethinkcode.fixme.broker.BrokerPrint;
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
    private  String messages;
    private static String fixMessage;
    private BufferedReader bufferedReader;
    private static String clientID;
    private static String market;
    private static String instrument;
    private static double price;
    private static int quantity;
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

            //TODO make sure to add whatever is bought to the wallet/assets
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
                }

                if (key.isWritable())
                    this.writeToClient();
            }
        }
    }


    private void read () throws  Exception {
        client.read(buffer);
        messages = new String(buffer.array()).trim();
        System.out.println(sdf.format(cal.getTime()) + " [BROKER]: BrokerID -> " + messages);
        clientID = messages;
        client.register(selector, SelectionKey.OP_READ);
//        System.out.println(fixMessage);
        buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE );
    }


    public void writeToClient() throws Exception {
        this.buffer = ByteBuffer.allocate(1024);
        this.buffer.put(fixMessage.getBytes());
        this.buffer.flip();
        client.write(this.buffer);
        System.out.println(sdf.format(cal.getTime()) + " [BROKER]: Fix Message Generated -> " + fixMessage);
        this.buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ);
        System.out.println(sdf.format(cal.getTime()) + " [BROKER]: Fix Message sent to Router for Market");
    }

    private String checkSumCalculator(String message){
        String checkSum;
        int total = 0;
        String checkSumMessage = message.replace('|', '\u0001');
        byte[] messageBytes = checkSumMessage.getBytes(StandardCharsets.US_ASCII);

        for (int i = 0; i < message.length(); i++)
            total += messageBytes[i];

        int CalculatedChecksum = total % 256;
        checkSum = Integer.toString(CalculatedChecksum );

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
        fixMessage = FixMessageFactory.fixMessage(clientID, buysell, market, instrument, price, quantity);
        fixMessage = fixMessage + "10=" + checkSumCalculator(fixMessage);
        client.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args){
        BrokerPrint.buyOrSell();

        buysell = Broker.setBuyOrSell();
        if (buysell == 1){
            BrokerPrint.startUpMessage();
            market = Broker.setMarket();
            BrokerPrint.marketContentsMessage();
            instrument = Broker.setInstrument();
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
            BrokerPrint.showAssets();
        }
        System.out.println("\n\nYour purchase request sending as FIX message\n" +
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
