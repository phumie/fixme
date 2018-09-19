package wethinkcode.fixme.market;

import lombok.Getter;
import wethinkcode.fixme.market.utilities.HandleFile;
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
import java.util.ArrayList;
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
    private  String messages;
    private BufferedReader bufferedReader;
    private boolean idFlag;
    private String clientID;

    public Market(String name, Commodity stock1, Commodity stock2, Commodity stock3) {
        this.name = name;
        this.commodities = new ArrayList<>();
        this.commodities.add(stock1);
        this.commodities.add(stock2);
        this.commodities.add(stock3);
        this.idFlag = false;
        this.socketSetUp();
    }

    /**
     * Initializing the server
     */
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

    /**
     * Checks through the keys for when the incoming key is
     * Valid, Acceptable, Readable, Writable
     *
     * @throws Exception thrown due to a mishandling of the key
     */

    public void startClient() throws Exception {
    System.out.println("< Market started >");
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

    /**
     * Takes in a key, processes it and establishes a connection
     *
     * @param key (SelectionKey)
     * @return (boolean) connection has been established
     * @throws Exception thrown when an error occurs while trying to establish connection
     */
    private static boolean processConnect(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    /**
     * Reads incoming message for processing
     * @throws Exception thrown reading from for wrong
     */

    private void read () throws  Exception {
        client.read(buffer);
        messages = new String(buffer.array()).trim();

        System.out.println("Market message:" + messages);
        if (!this.idFlag){
            this.clientID = messages;
            this.client.register(this.selector, SelectionKey.OP_READ);
            this.idFlag = true;
        }
        else {
            //TODO: insert messages that will be sent back to the broker
            if (this.processMessage(messages))
                System.out.println("The buy is valid");
            else
                System.out.println("The buy is not valid");
            this.client.register(this.selector, SelectionKey.OP_WRITE );
        }
        buffer.clear();
    }

    /**
     * checks whether the market has sufficient commodities available
     * for the buyers request.
     *
     * @param message (String) to be processed
     * @return (boolean) determined by the result of the process
     */

    private boolean processMessage (String message) {
        String []splitMessage = message.split("\\|");
        String instrument = splitMessage[6].split("=")[1];
        double quantity = Double.parseDouble(splitMessage[8].split("=")[1]);
        boolean quantityCheck = false;
        //TODO: create a sell task as well
        for (Commodity commodity: this.commodities) {
            if (!commodity.getName().equals(instrument))
                continue;
            quantityCheck = commodity.buyCommodity(quantity);
        }
        return quantityCheck;
    }

    /**
     *
     * Writes to the client via the buffer
     * Sets the option on the selector to read
     *
     * @throws Exception thrown when fails to write to buffer
     */

    private void writeToClient() throws Exception {
        messages = bufferedReader.readLine();
        this.buffer = ByteBuffer.allocate(1024);
        this.buffer.put(messages.getBytes());
        this.buffer.flip();
        client.write(this.buffer);
        System.out.println("Checking message: " + messages);
        this.buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ);
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

        WriteToFile.createFile();
        WriteToFile.closeFile();

        try {
            market.startClient();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
