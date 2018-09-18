package wethinkcode.fixme.router.server.Validation;

import lombok.Getter;
import wethinkcode.fixme.router.routing.RoutingTable;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;


@Getter
public class DestinationVerification implements MessageValidationHandler {

    private MessageValidationHandler nextChain;
    private List<RoutingTable> routingTables;
    private SocketChannel channel;

    public DestinationVerification(List<RoutingTable> routingTables) {
        this.routingTables = routingTables;
    }

    @Override
    public void setNextHandler(MessageValidationHandler nextHandler) {
        this.nextChain = nextHandler;
    }

    @Override
    public boolean validateMessage(FixMessageValidator validMessage) {

        String market;
        boolean flag = false;
        String validFixMessage = validMessage.getMessage();
        String [] tags = validFixMessage.split("\\|");
        market = tags[5].split("=")[1];

        for (RoutingTable item : this.routingTables){
            if (item.getId().equals(market)){
                flag = true;
                this.channel = item.getChannel();
                break;
            }
        }
        if (!flag) {
            return false;
        } else {
            this.nextChain.validateMessage(validMessage);
            return true;
        }
    }
}
