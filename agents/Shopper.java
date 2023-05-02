package pl.gda.pg.eti.kask.sa.alchemists.agents;

import lombok.Getter;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PriceCounterTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RegisterServiceBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.Shopper.ShopperBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Equipment;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Herb;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Potion;

public class Shopper extends BaseAgent {

    @Getter
    protected final ShopOfferings offers = new ShopOfferings();


    public Shopper() {
    }

    @Override
    protected void setup() {
        super.setup();

        getArgs();

        registerService();
        addBehaviour(new ShopperBehaviour(this));
    }

    protected void registerService(){
        addBehaviour(new RegisterServiceBehaviour(this, "Shop"));
    }


    protected void getArgs() {

        Object[] args = getArguments();


        if(!args[0].toString().equals("")) {
            //herbs
            String[] herbsArray = args[0].toString().split(" ");
            for (int i = 0; i * 2 < herbsArray.length; i++) {
                String name = herbsArray[2 * i];
                int price = Integer.parseInt(herbsArray[2 * i + 1]);
                PriceCounterTuple tuple = new PriceCounterTuple(
                        price,
                        1
                );
                offers.addProduct(new Herb(name), this.getAID(), tuple);
            }
        }

        if(!args[1].toString().equals("")) {
            //potion
            String[] potionsArray = args[1].toString().split(" ");
            for (int i = 0; i * 2 < potionsArray.length; i++) {
                String name = potionsArray[2 * i];
                int price = Integer.parseInt(potionsArray[2 * i + 1]);
                PriceCounterTuple tuple = new PriceCounterTuple(
                        price,
                        1
                );
                offers.addProduct(new Potion(name), this.getAID(), tuple);
            }
        }


        if(!args[2].toString().equals("")) {
            //equipment
            String[] equipmentsArray = args[2].toString().split(" ");
            for (int i = 0; i * 2 < equipmentsArray.length; i++) {
                String name = equipmentsArray[2 * i];
                int price = Integer.parseInt(equipmentsArray[2 * i + 1]);
                PriceCounterTuple tuple = new PriceCounterTuple(
                        price,
                        1
                );
                offers.addProduct(new Equipment(name), this.getAID(), tuple);
            }
        }
    }
}