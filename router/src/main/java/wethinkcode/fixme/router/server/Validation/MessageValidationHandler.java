package wethinkcode.fixme.router.server.Validation;

public interface MessageValidationHandler {

    void setNextHandler(MessageValidationHandler nextHandler);
    boolean validateMessage(FixMessageValidator validMessage);
}
