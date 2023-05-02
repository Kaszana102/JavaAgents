package pl.gda.pg.eti.kask.sa.alchemists.agents;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferingPrioritedTuples;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopListDiscussion.DiscussBestShopListBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.*;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ProductList;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RegisterServiceBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.*;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopDiscussions.DiscussShopsToCheckBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.GetFellowMagesBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author psysiu
 */
@Getter
@Setter
public class Mage extends BaseAgent {

    protected List<Product> productsToBuy = new ArrayList<Product>();

    /** holds fellow mages with adequate conversation ID*/
    protected Map<AID,String> fellowMages = new HashMap<AID,String>();

    //list of all shops. filled after shop discussion
    protected List<AID> shops = new ArrayList<>();

    //all possible places with all possible products to buy
    protected ShopOfferings shopOffers = new ShopOfferings();

    //otp of ReceiveMenu behaviour
    protected ShopOfferings shopOffersMini = new ShopOfferings();





    //filtered places, only the cheapest
    protected ShopOfferings bestOffers = new ShopOfferings();


    //tuples i will buy
    private ShopOfferingPrioritedTuples myTuples = new ShopOfferingPrioritedTuples();

    protected ProductList productsBought = new ProductList();

    protected int myBudget;
    protected Map<AID,Integer> fellowBudgets;



    public Mage() {
    }

    @Override
    protected void setup() {
        super.setup();

        getArgs();
        addBehaviour(new RegisterServiceBehaviour(this, "mage"));

        try{
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SequentialBehaviour behaviour = new SequentialBehaviour(this);


        //get fellow mages
        behaviour.addSubBehaviour(new GetFellowMagesBehaviour(this));

        behaviour.addSubBehaviour(new DiscussShopsToCheckBehaviour(this));

        behaviour.addSubBehaviour(new DiscussBestShopListBehaviour(this));

        addBehaviour(behaviour);

        addBehaviour(new MageBehaviour(behaviour, this));
    }



    protected void printShopList(){
        System.out.println("Need to buy:");

        for (Product product: productsToBuy
             ) {
            System.out.println(product);
        }

        System.out.println();
    }


    protected void getArgs(){
        Object[] args = getArguments();

        //args[0] - herbalist products
        String[] herbs = args[0].toString().split(" ");
        for (String herb: herbs) {
            if(!herb.equals("")) {
                productsToBuy.add(new Herb(herb));
            }
        }

        //args[1] - alchemist products
        String[] potions = args[1].toString().split(" ");
        for (String potion: potions) {
            if(!potion.equals("")) {
                productsToBuy.add(new Potion(potion));
            }
        }

        //args[2] - blacksmith products
        String[] equipments = args[2].toString().split(" ");
        for (String equipment: equipments) {
            if(!equipment.equals("")) {
                productsToBuy.add(new Equipment(equipment));
            }
        }

        myBudget =Integer.parseInt(args[3].toString());

    }



    public void addShopOffers(ShopOfferings offer){
        shopOffersMini.concat(offer);
    }




    public void addProduct(Product product){
        productsBought.add(product);
    }

    public int pay(int price){
        myBudget -=price;
        return price;
    }



    public void printWhatBought(){
        System.out.println("I bought:");

        //herbs
        for (var product: productsBought
             ) {
            System.out.println(product);
        }
        
    }

}
