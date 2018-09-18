package wethinkcode.fixme.router.tests;

import java.nio.charset.StandardCharsets;

public class test {

    public static void main(String[] args) {
        String message = "8=FIX.4.4|9=43|35=1|49=B00001|56=M000001|55=NQS|44=300|38=3";
        String[] tags;

        message = message.replace(" ", "");

        tags = message.trim().split("\\|");

        String chekSumTag = tags[tags.length - 1];
        int checksum = Integer.parseInt(chekSumTag.split("=")[1]);

        message = message.replace('|', '\u0001');
        byte[] messageBytes = message.getBytes(StandardCharsets.US_ASCII);
        int total = 0;

        for (int i = 0; i < message.length(); i++)
            total += messageBytes[i];

        int CalculatedChecksum = total % 256;


        System.out.print(CalculatedChecksum + "0");
//        for (String item: tags) {
//            System.out.println(item);
//        }
//
//        if (tags[0].contains("8=") && tags[tags.length - 1].contains("10=")){
//            System.out.print("YES");
//        }
    }
}
