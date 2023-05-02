package pl.gda.pg.eti.kask.sa.alchemists.ProductList;

import jade.core.AID;
import lombok.*;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Equipment;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Herb;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Potion;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopOfferings{

    protected Map<Product, ContactList> products = new HashMap<Product,ContactList>();


    public ContactList getContactList(Product product){
        return products.get(product);
    }

    public void addProduct(Product product, ContactList list){
        if(contains(product)) {
            products.get(product).concat(list);
        }
        else{
            products.put(product, list);
        }
    }

    public void addProduct(Product product, AID aid, PriceCounterTuple tuple){
        if(contains(product)){
            products.get(product).add(aid,tuple);
        }
        else{
            ContactList list = new ContactList();
            list.add(aid,tuple);
            products.put(product,list);
        }
    }

    public void add(ShopOfferTuple tuple){
        this.addProduct(tuple.getProduct(), tuple.getShop(), tuple.getPriceCounterTuple());
    }

    public void deleteProduct(Product product, AID aid, int price){
        if(contains(product)){
            products.get(product).delete(aid,price);
            if(products.get(product).size() == 0){
                products.remove(product);
            }
        }
    }

    public boolean contains(Product product){
        return products.containsKey(product);
    }
    public boolean contains(Product product, AID aid, int price){
        if(products.containsKey(product)){
            ContactList list = products.get(product);
            if(list.contains(aid,price)){
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


    public void concat(ShopOfferings otherOfferings){
        for (Product product: otherOfferings.products.keySet()
             ) {
            this.addProduct(product,otherOfferings.products.get(product));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ShopOfferings){
            ShopOfferings other = (ShopOfferings) obj;


            if(products.keySet().equals(other.products.keySet())){

                for (Product product : products.keySet()
                ) {
                    if(!products.get(product).equals(other.getContactList(product))){
                        return false;
                    }

                }
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

    public int getTotalPrice(){
        int sum=0;
        for (Product product: products.keySet()
             ) {
            sum+=products.get(product).getTotalPrice();
        }
        return sum;
    }


    @Override
    public String toString() {
        String otp="";
        for (Product product: products.keySet()
             ) {
            otp += product;
            otp +='\n';
            otp += products.get(product);


        }
        return otp;
    }


    public String packToJson(){
        String otp ="";
        boolean first = true;
        for (Product product: products.keySet()
             ) {
            if(first){
                first = false;
            }
            else{
                otp +="&";
            }
            otp += product.getName()+ " " + product.getType() + "!";

            otp+= products.get(product).packToJson();
        }
        return otp;
    }

    /**
     *
     * @param input*
     */
    public void unpackFromJson(String input){
        String[] products = input.split("&");

        for (String product: products
             ) {
            String[] productParams =product.split("!");

            String[] singleProduct = productParams[0].split(" ");

            String productName = singleProduct[0];

            ContactList contactList = new ContactList();


            contactList.unpackFromJson( productParams[1]);

            Product prod;
            switch (Integer.parseInt(singleProduct[1])) {
                case 1: prod=new Herb(productName);
                    break;
                case 2: prod=new Potion(productName);
                    break;
                case 3: prod=new Equipment(productName);
                    break;
                default: prod=new Herb(productName);
                    break;
            }
            this.products.put( prod, contactList);
        }
    }


    public ShopOfferingPrioritedTuples convertToProritiedTuples(){
        ShopOfferingPrioritedTuples otp = new ShopOfferingPrioritedTuples();

        for (Product product: products.keySet()
             ) {
            ContactList contactList = products.get(product);
            for(AID shop: contactList.products.keySet()){
                for(PriceCounterTuple tuple: contactList.getPricesCounter(shop)){
                    otp.tuples.add(
                            new ShopOfferTuple(product,shop,tuple,0,false)
                    );
                }
            }
        }

        return otp;
    }

}
