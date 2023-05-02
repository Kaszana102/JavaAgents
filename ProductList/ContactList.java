package pl.gda.pg.eti.kask.sa.alchemists.ProductList;

import jade.core.AID;
import lombok.*;

import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ContactList implements  Iterable<AID>{


    //     agent ID, cena, sztuki
    protected Map<AID,PricesCounter> products = new HashMap<AID,PricesCounter>();

    @Override
    public Iterator<AID> iterator() {
        return products.keySet().iterator();
    }

    public void add(AID aid, PricesCounter pricesCounter) {
        if(products.containsKey(aid)){
            products.get(aid).concat(pricesCounter);
        }
        else{
            products.put(aid,pricesCounter);
        }
    }


    public void add(AID aid, PriceCounterTuple tuple){
            if(products.containsKey(aid)){
                products.get(aid).addEntry(tuple);
            }
            else{
                PricesCounter priceCounter = new PricesCounter();
                priceCounter.addEntry(tuple);
                products.put(aid,priceCounter);
            }
    }


    public int size(){
        return products.size();
    }


    public PricesCounter getPricesCounter(AID aid){
        return products.get(aid);
    }

    public void delete(AID aid, int price){
        if(products.containsKey(aid)){
            products.get(aid).deleteEntry(price);
            if(products.get(aid).size()==0){
                products.remove(aid);
            }
        }
    }

    public void concat(ContactList otherList){
        for (AID aid: otherList.products.keySet()
        ) {
            this.add(aid,otherList.products.get(aid));
        }
    }

    public boolean contains(AID aid, int price){
        PricesCounter pricesCounter = products.get(aid);

        for (PriceCounterTuple tuple: pricesCounter.getPriceCounterTuples()
             ) {
            if(tuple.price == price){
                return true;
            }
        }

        //not found matching price
        return false;
    }


    public int getTotalPrice(){
        int sum=0;

        for (AID shop: products.keySet()
             ) {
            sum+=products.get(shop).getTotalPrice();
        }


        return sum;
    }

    @Override
    public String toString() {
        String otp="";
        for (AID aid: products.keySet()
        ) {
            otp += "  ";
            otp += aid;
            otp +='\n';
            otp += products.get(aid);
        }
        return otp;
    }

    public String packToJson(){
        String otp="";

        boolean first = true;
        for (AID aid: products.keySet()
        ) {
            if(first){
                first = false;
            }
            else{
                otp +="^";
            }
            otp += aid.getName();
            otp += "*";
            otp += aid.getAddressesArray()[0];
            otp += "*";
            otp+= products.get(aid).packToJson();
        }
        return otp;
    }

    public void unpackFromJson(String input){
        String[] contacts = input.split("\\^");

        for (String contact: contacts
        ) {
            String[] contactPars = contact.split("\\*");
            String aidName = contactPars[0];
            String aidAddress = contactPars[1];
            PricesCounter pricesCounter = new PricesCounter();
            pricesCounter.unpackFromJson(contactPars[2]); //should work XD

            AID aid = new AID();
            aid.setName(aidName);
            aid.addAddresses(aidAddress);
            products.put(aid,pricesCounter);
        }

    }
}
