package wethinkcode.fixme.market;

import lombok.Getter;

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

    public boolean buyCommodity (double quantity){
        this.totalAmount -= quantity;
        if (this.totalAmount <= 0){
            this.totalAmount += quantity;
            return false;
        }
            return true;
    }

    public boolean sellCommodity (double quantity){
        if (totalAmount - quantity < 0)
            return false;
        else
            totalAmount -= quantity;
        return false;
    }
    //TODO: you need to create a sell function. This will be used by broker.
}
