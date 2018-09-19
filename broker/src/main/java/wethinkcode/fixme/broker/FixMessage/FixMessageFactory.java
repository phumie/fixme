package wethinkcode.fixme.broker.FixMessage;

public class FixMessageFactory {
    public String fixMessage(){

        String fixMessage = brokerID + "|" + "8=fix.4.4|9=len|";

        fixMessage = fixMessage.concat("35=" + buyOrSell + "|");
        fixMessage = fixMessage.concat("49=" + brokerID + "|");
        fixMessage = fixMessage.concat("56=" + market + "|");
        fixMessage = fixMessage.concat("55=" + instrument + "|");
        fixMessage = fixMessage.concat("44=" + price + "|");
        fixMessage = fixMessage.concat("38=" + quantity + "|");

        return fixMessage;
    }
}
