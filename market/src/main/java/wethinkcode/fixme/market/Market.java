package wethinkcode.fixme.market;

import lombok.Getter;
import wethinkcode.fixme.market.utilities.WriteToFile;
//import wethinkcode.fixme.market.utilities.MarketFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

@Getter
public class Market {
    private String name;
    private ArrayList<Commodity> commodities;
    private Selector selector;
    private InetSocketAddress hostAddress;
    private SocketChannel client;
    private ByteBuffer buffer;
    private  String messages, fixMessage;
    private BufferedReader bufferedReader;
    private boolean idFlag;
    private String clientID;
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public Market(String name, Commodity stock1, Commodity stock2, Commodity stock3) {
        this.name = name;
        this.commodities = new ArrayList<>();
        this.commodities.add(stock1);
        this.commodities.add(stock2);
        this.commodities.add(stock3);
        this.idFlag = false;
        this.socketSetUp();
    }

    private void socketSetUp () {
        try {
            this.selector = Selector.open();
            this.hostAddress = new InetSocketAddress("localhost", 5001);
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


        System.out.println("----- MARKET STARTED -----\n");
        System.out.println(sdf.format(cal.getTime()) + " [MARKET]: Connected to Router");

        while (true){
            if (this.selector.select() == 0)
                continue;
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
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
                    this.read();

                if (key.isWritable())
                    this.writeToClient();
            }
        }
    }

    private static boolean processConnect(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    private void read () throws  Exception {
        client.read(buffer);
        messages = new String(buffer.array()).trim();
        if (messages.equals("M00001"))
            System.out.println(sdf.format(cal.getTime()) + " [MARKET]: MarketID -> "+ messages);
        else
            System.out.println(sdf.format(cal.getTime()) + " [MARKET]: FixMessage from Broker -> "+ messages);
        if (!this.idFlag){
            this.clientID = messages;
            this.client.register(this.selector, SelectionKey.OP_READ);
            this.idFlag = true;
        }
        else {
            if (this.processMessage(messages)) {
                System.out.println(sdf.format(cal.getTime()) + " [MARKET]: Buy is valid");
                fixMessage = fixMessageGenerator(true);
                System.out.println(sdf.format(cal.getTime()) + " [MARKET]: Broker message: " + fixMessage);
            }
            else {
                System.out.println(sdf.format(cal.getTime()) + " [MARKET]: Buy is not valid");
                fixMessage = fixMessageGenerator(false);
                System.out.println(sdf.format(cal.getTime()) + " [MARKET]: Broker message: " + fixMessage);
            }
            this.client.register(this.selector, SelectionKey.OP_WRITE );
        }
        buffer.clear();
    }

    private boolean processMessage (String message) {
        String[] splitMessage = message.split("\\|");
        String instrument = splitMessage[6].split("=")[1];
        int buysell = Integer.parseInt(splitMessage[3].split("=")[1]);
        double quantity = Double.parseDouble(splitMessage[8].split("=")[1]);
        double price = Double.parseDouble(splitMessage[7].split("=")[1]);
        boolean quantityCheck = false;
        if (buysell == 1){
            for (Commodity commodity: this.commodities) {
                if (!commodity.getName().equals(instrument))
                    continue;
                quantityCheck = commodity.buyCommodity(instrument, quantity);
                WriteToFile.updateFile(instrument, commodity.getTotalAmount(), 1, commodity.getPrice(), "source.txt");
            }
        }
        else if (buysell == 2){
            for (Commodity commodity: this.commodities) {
                if (!commodity.getName().equals(instrument))
                    continue;
                quantityCheck = commodity.sellCommodity(instrument, quantity, price);
                WriteToFile.updateFile(instrument, commodity.getTotalAmount(), 2, commodity.getPrice(), "source.txt");
            }
        }
        return quantityCheck;
    }

    private String fixMessageGenerator(boolean check){
        String[] tags =  messages.split("\\|");
        String fixMessage = "";

        if (check == true){
            fixMessage = "YayYay|" + tags[1] + "|" + tags[2] + "|" + "35=Executed|" +
                        "49=YayYay" + "|" + "56=" + tags[0] + "|" + tags[6] + "|" + "44=0|" + tags[8] + "|";
            fixMessage = fixMessage + "10=" + checkSumCalculator(fixMessage);
        }
        else if (check == false){
            fixMessage = "YayYay|" + tags[1] + "|" + tags[2] + "|" + "35=Rejected|" +
                    "49=YayYay" + "|" + "56=" + tags[0] + "|" + tags[6] + "|" + "44=0|" + tags[8] + "|";
            fixMessage = fixMessage + "10=" + checkSumCalculator(fixMessage);
        }
        return fixMessage;
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

    /**
     *
     * Writes to the client via the buffer
     * Sets the option on the selector to read
     *
     * @throws Exception thrown when fails to write to buffer
     */

    public void writeToClient() throws Exception {
        this.buffer = ByteBuffer.allocate(1024);
        this.buffer.put(fixMessage.getBytes());
        this.buffer.flip();
        client.write(this.buffer);
//        System.out.println(sdf.format(cal.getTime()) + " [BROKER]: Fix Message Generated -> " + fixMessage);
        this.buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ);
        System.out.println(sdf.format(cal.getTime()) + " [Market]: Fix Message sent to Router for Broker");
    }

    private void stop() throws IOException {
        this.client.close();
        this.buffer = null;
    }

    public static void main(String[] args) {
        Market market = new Market("YayYay ",
                new Commodity("Gold", 78976.0, 1208.0),
                new Commodity("Silver", 78565353.0, 909.0) ,
                new Commodity("Platinum", 74763.0, 1889.0));
        try {
            market.startClient();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
