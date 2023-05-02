package pl.gda.pg.eti.kask.sa.alchemists.ProductList;


import lombok.*;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ShopOfferingPrioritedTuples implements Iterable<ShopOfferTuple> {

    protected List<ShopOfferTuple> tuples = new Vector<ShopOfferTuple>();

    public void add(ShopOfferTuple tuple){
        this.tuples.add(tuple);
    }

    public ShopOfferTuple get(int n){
        return tuples.get(n);
    }
    public ShopOfferTuple get(ShopOfferTuple tuple){

        for (ShopOfferTuple tu : this.tuples
                ) {
                if(tu.equals(tuple)){
                    return tu;
                }
        }

        return null;
    }

    public void remove(ShopOfferTuple tuple){
        this.tuples.remove(tuple);
    }
    public void remove(int index){
        tuples.remove(index);
    }

    public void concat(List<ShopOfferTuple> other){
        tuples.addAll(other);
    }
    public void concat(ShopOfferingPrioritedTuples other){
        tuples.addAll(other.getTuples());
    }

    public boolean contains(ShopOfferTuple tuple){

        return  this.tuples.contains(tuple);
    }

    public int size(){
        return tuples.size();
    }


    /** selects n random free tuples
     * it also sets random priority!
     * */
    public ShopOfferingPrioritedTuples selectFreeTuples(int n){
        ShopOfferingPrioritedTuples freeTuples = getAllFreeTuples();
        ShopOfferingPrioritedTuples otp = new ShopOfferingPrioritedTuples();

        ShopOfferTuple tuple;
        int index;
        Random random = new Random();
        for(int i=0; i<n   && freeTuples.size()>0; i++){
            index = Math.abs(random.nextInt())%freeTuples.size();

            tuple = freeTuples.get(index);
            freeTuples.remove(index);

            tuple.setTaken(true);
            tuple.setPriority(random.nextInt());

            otp.add(tuple);
        }
        return otp;
    }

    /** return all free tuples */
    public ShopOfferingPrioritedTuples getAllFreeTuples(){
        ShopOfferingPrioritedTuples otp = new ShopOfferingPrioritedTuples();

        for (ShopOfferTuple tuple: tuples
        ) {
            if(!tuple.isTaken()){
                otp.add(tuple);
            }
        }
        return otp;
    }

    @Override
    public Iterator<ShopOfferTuple> iterator() {
        return tuples.iterator();
    }



    /** each tuples is separated with # */
    public String convertToJason(){
        boolean once = true;
        String otp="";
        for (ShopOfferTuple tuple: tuples
             ) {
            if(once) {
                once = false;
            }
            else{
                otp += '#';
            }
            otp += tuple.toJason();
        }
        return otp;
    }

    public static ShopOfferingPrioritedTuples convertFromJason(String input){
        ShopOfferingPrioritedTuples offers=new ShopOfferingPrioritedTuples();

        if(!input.equals("")) {
            String[] tuplesJasons = input.split("#");

            for (String tuple : tuplesJasons
            ) {
                offers.add(ShopOfferTuple.fromJason(tuple));
            }
        }
        return offers;
    }



    public ShopOfferings converToShopOfferings(){
        ShopOfferings otp = new ShopOfferings();
        for (ShopOfferTuple tuple: tuples
             ) {
            otp.add(tuple);

        }
        return otp;
    }
}
