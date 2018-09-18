package wethinkcode.fixme.router.routing;

import lombok.Getter;
import wethinkcode.fixme.router.server.Server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Router {

    private List<RoutingTable> routingTable ;
    private Server server;

    public Router(){
        this.routingTable = new ArrayList<>();
        try {
            this.server = new Server();
            this.server.startServer(this.routingTable);
            this.addToRoutingTable("000000", null);// why is this key null?

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToRoutingTable(String id, SocketChannel channel){

        this.routingTable.add(new RoutingTable(id, channel));
    }

    public static void main(String[] args) {
        new Router();
    }
}
