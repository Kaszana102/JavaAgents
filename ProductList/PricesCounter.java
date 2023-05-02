package pl.gda.pg.eti.kask.sa.alchemists.ProductList;

import jade.core.AID;
import lombok.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PricesCounter  implements Iterable<PriceCounterTuple>{
    protected Vector<PriceCounterTuple> priceCounterTuples = new Vector<PriceCounterTuple>();


    @Override
    public Iterator<PriceCounterTuple> iterator() {
        return priceCounterTuples.iterator();
    }

    public void addEntry(PriceCounterTuple tuple){
        for (PriceCounterTuple Tuple: priceCounterTuples
             ) {
            if(Tuple.price == tuple.price){
                Tuple.units+=Tuple.units;
                return;
            }
        }

        //not found existing product
        priceCounterTuples.add(tuple);
    }


    public void deleteEntry(int price){
        for (PriceCounterTuple Tuple: priceCounterTuples
        ) {
            if(Tuple.price == price){
                Tuple.units -= 1;
                if(Tuple.units == 0){
                    priceCounterTuples.remove(Tuple);
                }
                return;
            }
        }
    }

    public void concat(PricesCounter otherPrices){
        for (PriceCounterTuple tuple: otherPrices.priceCounterTuples
        ) {
            addEntry(tuple);
        }
    }

    public int size(){
        return priceCounterTuples.size();
    }

    public int getTotalPrice(){
        int sum=0;

        for (PriceCounterTuple tuple: priceCounterTuples
        ) {
            sum+= tuple.price * tuple.units;
        }


        return sum;
    }



    @Override
    public String toString() {
        String otp="    cena    sztuki\n";
        for (PriceCounterTuple tuple: priceCounterTuples
        ) {
            otp += "    ";
            otp += tuple.price;
            otp += "         ";
            otp += tuple.units;
            otp +='\n';
        }
        return otp;
    }


    public String packToJson(){
        String otp="";

        boolean first = true;
        for (PriceCounterTuple tuple: priceCounterTuples
        ) {
            if(first){
                first = false;
            }
            else{
                otp +=";";
            }
            otp += tuple.price;
            otp += ' ';
            otp += tuple.units;
        }
        return otp;

    }

    public void unpackFromJson(String input){
        String[] tuples = input.split(";");

        for (String tuple: tuples
        ) {
            String[] tuplesParameters = tuple.split(" ");
            int price = Integer.parseInt(tuplesParameters[0]);
            int counter =Integer.parseInt(tuplesParameters[1]);


            priceCounterTuples.add(new PriceCounterTuple(price,counter));
        }

    }
}

