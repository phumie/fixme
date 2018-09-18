package wethinkcode.fixme.router.server;

public class IDGenerator {

    private static IDGenerator idGenerator = new IDGenerator();
    private static int units = 0, tens = 0, hundreds = 0, thousands = 0, tenThousands = 0, hundredThousands = 0;

    private IDGenerator(){

    }

    public static IDGenerator getIdGenerator() {
        return idGenerator;
    }

    //todo: depending on the port number of the server, set the first digit from left either to B(broker) || M(market)

    public static String generateId(int port) {

        String indicator = null;
        if (units < 9) {
            units++;
        }
        else {
            units = 0;
            tens++;
        }
        if (tens > 9) {
            tens = 0;
            hundreds++;
        }
        if (hundreds > 9) {
            hundreds = 0;
            thousands++;
        }
        if (thousands > 9) {
            thousands = 0;
            tenThousands++;
        }
        if (tenThousands > 9) {
            tenThousands = 0;
            hundredThousands++;
        }
        if (hundredThousands > 9) {
            return "error";
        }

        if (port == 5000)
            indicator = "B";
        else if (port == 5001)
            indicator = "M";

        return indicator + "" + tenThousands + "" + thousands + "" + hundreds + "" + tens + "" + units;

    }
}
