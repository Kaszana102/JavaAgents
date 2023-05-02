package pl.gda.pg.eti.kask.sa.alchemists.ProductList;

import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.ProductBought;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;

import java.util.*;

public class ProductList implements Iterable<Map.Entry<Product, Integer>> {
    protected Map<Product, Integer> products = new HashMap<Product,Integer>();

    public void add(Product product){
        if(products.containsKey(product)){ //if already on the list
            Integer count =  products.get(product);
            count++;
            products.replace(product,count);
        }
        else{
            products.put(product,1);
        }
    }

    public void add(Product product, int units){
        if(products.containsKey(product)){ //if already on the list
            Integer count =  products.get(product);
            count+=units;
            products.replace(product,count);
        }
        else{
            products.put(product,units);
        }
    }


    public void add(ProductList products){
        for (Product  product:  products.keySet()
             ) {
             for(int i=0; i<products.get(product); i++){
                 this.add(product);
             }
        }
    }

    public int get(Product product){
        return products.get(product);
    }


    public Set<Product> keySet(){
        return products.keySet();
    }


    public boolean contains(Product product){
        return products.containsKey(product);
    }


    public Product popProduct(Product product){
        if(products.containsKey(product)) { //if  on the list
            Integer count =  products.get(product);
            count--;
            if(count == 0) {
                //delete entry
                products.remove(product);
            }
            else {
                products.replace(product, count);
            }
            return  product;
        }
        else{
            return null;
        }
    }

    @Override
    public Iterator<Map.Entry<Product, Integer>> iterator() {
        return products.entrySet().iterator();
    }



    public String convertToJason(){
        String otp="";
        boolean notFirst=false;

        for (Product product: products.keySet()
             ) {
            if(notFirst){
                otp+="@";
            }
            else{
                notFirst=true;
            }
            otp+=product.getName()+ " " + product.getType() + " " + products.get(product);
        }

        return otp;
    }

    public static ProductList convertFromJason(String src){
        ProductList otp = new ProductList();

        if(!src.equals("")) {
            String[] tuples = src.split("@");

            for (String tuple : tuples
            ) {
                String[] args = tuple.split(" ");
                String name = args[0];
                int type = Integer.parseInt(args[1]);
                int counter = Integer.parseInt(args[2]);

                otp.add(Product.createProduct(name, type), counter);

            }
        }


        return otp;
    }


    @Override
    public String toString(){
        return products.toString();
    }

}
