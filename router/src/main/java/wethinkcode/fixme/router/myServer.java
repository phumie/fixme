package wethinkcode.fixme.router;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
public class myServer {
    public static void main(String[] argv) throws Exception {
        Selector selector = Selector.open();

        ServerSocketChannel ssChannel1 = ServerSocketChannel.open();
        ssChannel1.configureBlocking(false);
        ssChannel1.socket().bind(new InetSocketAddress(5000));
        ssChannel1.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey selKey = (SelectionKey) it.next();
                it.remove();

                if (selKey.isAcceptable()) {
                    ServerSocketChannel ssChannel = (ServerSocketChannel) selKey.channel();
                    SocketChannel sc = ssChannel.accept();
                    ByteBuffer buf = ByteBuffer.allocate(100);
                    int numBytesRead = sc.read(buf);

                    if (numBytesRead == -1) {
                        sc.close();
                    } else {
                        // Read the bytes from the buffer
                    }
                    int numBytesWritten = sc.write(buf);
                }
            }
        }
    }
}
