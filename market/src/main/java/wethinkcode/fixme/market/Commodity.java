package wethinkcode.fixme.market;

import lombok.Getter;
import wethinkcode.fixme.market.utilities.WriteToFile;

@Getter
public class Commodity {
    private String name;
    private double totalAmount;
    private double price;

    public Commodity(String name, double totalAmount, double price) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.price =  price;
    }

    public boolean buyCommodity (String instrument, double quantity){
        this.totalAmount -= quantity;
        if (this.totalAmount <= 0){
            this.totalAmount += quantity;
            return false;
        }
            return true;
    }

    public boolean sellCommodity (String instrument, double quantity, double price){
        double originalPrice = this.price * quantity;
        double sellPrice = price * quantity;
        if (originalPrice == sellPrice || originalPrice > sellPrice){
            this.totalAmount = this.totalAmount + quantity;
            return true;
        }
        return false;
    }
}
