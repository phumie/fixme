package wethinkcode.fixme.router.routing;

import lombok.Getter;
import wethinkcode.fixme.router.server.Server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

@Getter
public class RoutingTable {

    private String    id;
    private SocketChannel channel;

    public RoutingTable(String id, SocketChannel channel){
        this.id = id;
        this.channel = channel;
    }


}
