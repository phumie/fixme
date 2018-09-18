package wethinkcode.fixme.router.server.Validation;

import java.nio.charset.StandardCharsets;

public class CheckSumValidator implements MessageValidationHandler {

    private MessageValidationHandler nextChain;

    @Override
    public void setNextHandler(MessageValidationHandler nextHandler) {
        this.nextChain = nextHandler;
    }

    @Override
    public boolean validateMessage(FixMessageValidator validMessage) {

       String message = validMessage.getMessage();

       String[] tags = message.split("\\|");
       String checksum = tags[tags.length - 1].split("=")[1];
       message = "";
        for (String item: tags) {
            if (!item.contains("10="))
                message = message.concat(item + "|");
        }

        String calculatedCheckSum = checkSumCalculator(message);
        if (checksum.contentEquals(calculatedCheckSum)) {
            nextChain.validateMessage(validMessage);
              return true;
       }
       else {
           return false;
       }
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
}
