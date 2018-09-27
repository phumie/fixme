package wethinkcode.fixme.router.server.Validation;

import wethinkcode.fixme.router.routing.RoutingTable;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

public class MessageForwarding implements MessageValidationHandler{


    @Override
    public void setNextHandler(MessageValidationHandler nextHandler) {

    }

    @Override
    public boolean validateMessage(FixMessageValidator validMessage){
        return true;
    }

}
