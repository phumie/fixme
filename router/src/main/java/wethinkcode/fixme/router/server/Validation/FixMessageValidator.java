package wethinkcode.fixme.router.server.Validation;

public class FixMessageValidator {

    private String message;

    public FixMessageValidator(String message){

        String[] tags;

        message = message.replace(" ", "");
        tags = message.trim().split("\\|");

        if (tags[1].contains("8=") && tags[tags.length - 1].contains("10=")){

            //Will Validate the message that is sent to the server
            this.message = message;

        }else {
            //TODO:: Implement correct steps if the message is not valid
        }
    }

    public String getMessage() {
        return message;
    }
}
