package wethinkcode.fixme.router.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Client {
    private Selector selector;
    private InetSocketAddress hostAddress;
    private SocketChannel client;
    private ByteBuffer buffer;
    private  String messages;
    private  BufferedReader bufferedReader;
    private String clientID;

    /**
     * Initializing the server
     *
     *
     */

    public Client() {
        try {
            this.selector = Selector.open();
            this.hostAddress = new InetSocketAddress("localhost", 5001);
            this.client = SocketChannel.open(this.hostAddress);
            this.client.configureBlocking(false);
            System.out.println(this.client.getLocalAddress().toString());
            int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
            this.client.register(this.selector, operations);
            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.buffer = ByteBuffer.allocate(1024);
        }
        catch (IOException e) {
            System.out.println("!------->A problem occurred whilst initializing the client<-------!");
            try {
                this.stop();
            }
            catch (IOException i) {
                System.out.println("!------->A problem occurred whilst closing the client<-------!");
            }
        }
    }

    /**
     * Checks through the keys for when the incoming key is
     * Valid, Acceptable, Readable, Writable
     *
     * @throws Exception
     */

    public void startClient() throws Exception {

        System.out.println("Client... started");
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
                {
                    System.out.println("Enter is readable.");
                    this.read();
                }

                if (key.isWritable())
                    this.writeToClient();
            }
        }
    }

    /**
     *
     * @throws Exception
     */

    private void read () throws  Exception {
        client.read(buffer);
        messages = new String(buffer.array()).trim();
        System.out.println("response=" + messages);
        buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE );
    }

    /**
     * The server gets the socket from the key
     * Writes to the client via the buffer
     * Sets the option on the selector to read
     * @throws Exception
     */

    public void writeToClient() throws Exception {
        messages = bufferedReader.readLine();
        messages = "B00001|8=FIX.4.4|9=43|35=1|49=B00001|56=M00002|55=NQS|44=300|38=3";/*bufferedReader.readLine();*/
        messages = messages + "|10=" + checkSumCalculator(messages);
        this.buffer = ByteBuffer.allocate(1024);
        this.buffer.put(messages.getBytes());
        this.buffer.flip();
        client.write(this.buffer);
        System.out.println(messages);
        this.buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ);
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

    public static void main(String[] args) {

        Client client = new Client();
        try {
            client.startClient();
        } catch (IOException e ) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
