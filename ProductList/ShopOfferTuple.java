package pl.gda.pg.eti.kask.sa.alchemists.ProductList;

import jade.core.AID;
import lombok.*;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Equipment;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Herb;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Potion;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;

import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ShopOfferTuple {
    private Product product;
    private AID shop;
    private PriceCounterTuple priceCounterTuple;
    private int priority;
    private boolean taken;


    @Override
    public boolean equals(Object o) {
        if(o instanceof ShopOfferTuple){
            ShopOfferTuple other = (ShopOfferTuple) o;

            if(other.getShop().equals(shop) && other.getProduct().equals(product) && other.getPriceCounterTuple().equals(priceCounterTuple)){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }


    public void setNewPriority(){
        Random random = new Random();
        priority=random.nextInt();
    }

    /** separtor & */
    public String toJason(){
        String otp="";



        otp+=product.getType();
        otp+=' ';
        otp+=product.getName();
        otp+='&';

        otp+=shop.getName();
        otp+=' ';
        otp+=shop.getAddressesArray()[0];
        otp+='&';


        otp+=priceCounterTuple.units;
        otp+=' ';
        otp+=priceCounterTuple.price;
        otp+='&';

        otp+=priority;


        return otp;
    }
    public static ShopOfferTuple fromJason(String input){

        ShopOfferTuple otp =new ShopOfferTuple();
        String[] args = input.split("&");

        //product
        String[] productArgs = args[0].split(" ");
        switch(Integer.parseInt(productArgs[0])){
            case 1: otp.setProduct(new Herb(productArgs[1])); break;
            case 2: otp.setProduct(new Potion(productArgs[1])); break;
            case 3: otp.setProduct(new Equipment(productArgs[1])); break;
            default: otp.setProduct(new Herb(productArgs[1])); break;
        }

        //shop
        String[] aidArgs = args[1].split(" ");
        AID aid = new AID();
        aid.setName(aidArgs[0]);
        aid.addAddresses(aidArgs[1]);
        otp.setShop(aid);

        //priceCounterTuple
        String[] tupleArgs = args[2].split(" ");
        PriceCounterTuple tuple = new PriceCounterTuple();
        tuple.setUnits(Integer.parseInt(tupleArgs[0]));
        tuple.setPrice(Integer.parseInt(tupleArgs[1]));
        otp.setPriceCounterTuple(tuple);

        //priority
        otp.setPriority(Integer.parseInt(args[3]));

        return otp;
    }

    @Override
    public String toString(){
        String otp="";

        otp+=product.getName()+" ";
        otp+=shop.getName()+" ";
        otp+=priceCounterTuple.units+" " +priceCounterTuple.price +" ";
        otp+=priority;
        return otp;
    }

}
