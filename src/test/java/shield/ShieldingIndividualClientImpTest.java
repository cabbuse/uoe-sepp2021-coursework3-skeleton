/**
 *
 */

package shield;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.time.LocalDateTime;
import java.io.InputStream;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */

public class ShieldingIndividualClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private ShieldingIndividualClientImp client;

  private Properties loadProperties(String propsFilename) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();

    try {
      InputStream propsStream = loader.getResourceAsStream(propsFilename);
      props.load(propsStream);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return props;
  }

  @BeforeEach
  public void setup() {
    clientProps = loadProperties(clientPropsFilename);

    client = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"), clientProps.getProperty("dietary_pref"), clientProps.getProperty("boxChoice"), (String) clientProps.get("changes"));
  }


  @Test
  public void testShieldingIndividualNewRegistration() {
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
  }

  @Test public void placeOrder () {
      Random rand = new Random();
      String chi = String.valueOf(rand.nextInt(10000));

      assertTrue(client.registerShieldingIndividual(chi));
      assertTrue(client.isRegistered());
      assertEquals(client.getCHI(), chi);
      assertEquals(client.showFoodBoxes(client.getDietary_pref()).size(), 3 );
      assertEquals(client.showFoodBoxes("vegan").size(), 1 );
      assertEquals(client.showFoodBoxes("pollotarian").size(), 1);
      assertTrue(client.placeOrder());

  }

  @Test
  public void editOrder(){

      Random rand = new Random();
      String chi = String.valueOf(rand.nextInt(10000));

      assertTrue(client.registerShieldingIndividual(chi));
      assertTrue(client.isRegistered());
      assertEquals(client.getCHI(), chi);
      assertEquals(client.showFoodBoxes(client.getDietary_pref()).size(), 3 );
      assertEquals(client.showFoodBoxes("vegan").size(), 1 );
      assertEquals(client.showFoodBoxes("pollotarian").size(), 1);
      assertTrue(client.placeOrder());
      ArrayList<Integer> orders = new ArrayList<>();
      orders = (ArrayList<Integer>) client.getOrderNumbers();
      assertTrue(client.editOrder(Integer.parseInt(client.getOrderNo())));

  }

  @Test
  public void cancelOrder(){
      Random rand = new Random();
      String chi = String.valueOf(rand.nextInt(10000));

      assertTrue(client.registerShieldingIndividual(chi));
      assertTrue(client.isRegistered());
      assertEquals(client.getCHI(), chi);
      assertEquals(client.showFoodBoxes(client.getDietary_pref()).size(), 3 );
      assertEquals(client.showFoodBoxes("vegan").size(), 1 );
      assertEquals(client.showFoodBoxes("pollotarian").size(), 1);
      assertTrue(client.placeOrder());
      assertTrue(client.cancelOrder(Integer.parseInt(client.getOrderNo())));
  }




  @Test
  public void requestStatus(){
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));

        assertTrue(client.registerShieldingIndividual(chi));
        assertTrue(client.isRegistered());
        assertEquals(client.getCHI(), chi);
        assertEquals(client.showFoodBoxes(client.getDietary_pref()).size(), 3 );
        assertEquals(client.showFoodBoxes("vegan").size(), 1 );
        assertEquals(client.showFoodBoxes("pollotarian").size(), 1);
        assertTrue(client.placeOrder());
        assertTrue(client.requestOrderStatus(Integer.parseInt(client.getOrderNo())));

    }

    @Test
    /**
     *  test requires caterers to be registered
     *  unit test for closest caterer
     */

    public void testGetClosestCat(){
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));

        assertTrue(client.registerShieldingIndividual(chi));
        String closest = client.getClosestCateringCompany();
        assertEquals(closest, client.getClosestCateringCompany());
    }

    @Test
    public void testSetOrderQuant(){
        Random rand = new Random();

        for (int i = 0; i<5; i++){
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }
        Object[] ids = (client.getItemIdsForOrder(Integer.parseInt(client.getOrderNo()))).toArray();
        assertTrue(client.setItemQuantityForOrder(Integer.parseInt(ids[0].toString()),Integer.parseInt(client.getOrderNo()),0));
        assertFalse(client.setItemQuantityForOrder(999,99999,200));


    }

    @Test
    public void testGetItemQuant(){
        Random rand = new Random();

        for (int i = 0; i<5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }
            Object[] ids = (client.getItemIdsForOrder(Integer.parseInt(client.getOrderNo()))).toArray();
            assertEquals(client.getItemQuantityForOrder(9999,999),0);
            assertEquals(client.getItemQuantityForOrder(Integer.parseInt(ids[0].toString()),Integer.parseInt(client.getOrderNo())),1);
        }




    @Test
    public void getItemNameforOrder(){
        Random rand = new Random();

        for (int i = 0; i<5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }
        Object[] ids = (client.getItemIdsForOrder(Integer.parseInt(client.getOrderNo()))).toArray();
        assertEquals(client.getItemNameForOrder(9999,999),null);
        assertEquals(client.getItemNameForOrder(Integer.parseInt(ids[0].toString()),Integer.parseInt(client.getOrderNo())),"onions");

    }



    @Test
    public void getItemIDSforOrder(){
        Random rand = new Random();

        for (int i = 0; i<5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }
        Object[] eq = {3,4,8};
        Object[] comp = client.getItemIdsForOrder(Integer.parseInt(client.getOrderNo())).toArray();
        assertEquals(client.getItemIdsForOrder(Integer.parseInt(client.getOrderNo())).toArray().length, eq.length);
        assertEquals(client.getItemIdsForOrder(999999),null);
        for (int i = 0; i<comp.length; i ++){
            assertEquals(comp[i],eq[i]);
        }
    }

    @Test
    public void getStatusForOrder(){
        Random rand = new Random();

        for (int i = 0; i<5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }

        assertEquals(client.getStatusForOrder(Integer.parseInt(client.getOrderNo())),"0");
        assertEquals(client.getStatusForOrder(99999),"-1");


    }

    @Test
    public void GetOrderNumbers(){
        Random rand = new Random();

        for (int i = 0; i<5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }

        Object[] ordns = client.getOrderNumbers().toArray();
        for (int i = 0; i<ordns.length-1; i++){
            assertEquals(ordns[i+1], Integer.parseInt(String.valueOf(ordns[i]))+1);
        }


    }


    @Test
    public void changeItemQuantityForPickedFoodBox(){
        Random rand = new Random();

        for (int i = 0; i<5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }
        Object testvar = client.getItemIdsForOrder(Integer.parseInt(client.getOrderNo())).toArray()[0];
        assertTrue(client.changeItemQuantityForPickedFoodBox(Integer.parseInt(String.valueOf(testvar)),0));
        assertFalse(client.changeItemQuantityForPickedFoodBox(999999,0));
        assertFalse(client.changeItemQuantityForPickedFoodBox(999999,9999999));
        assertFalse(client.changeItemQuantityForPickedFoodBox(Integer.parseInt(String.valueOf(testvar)),99999));

    }

    @Test
    //unit testing with none dietary preference
    public void pickfoodBox(){
        assertTrue(client.pickFoodBox(1));
        assertTrue(client.pickFoodBox(3));
        assertTrue(client.pickFoodBox(4));
        assertFalse(client.pickFoodBox(2));
        assertFalse(client.pickFoodBox(-111));
        assertFalse(client.pickFoodBox(999999));
    }

    @Test
    public void getItemQuantityForFoodbox() {
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }

        assertEquals(client.getItemQuantityForFoodBox(1,1),1);
        assertEquals(client.getItemQuantityForFoodBox(2,1),2);

        assertEquals(client.getItemQuantityForFoodBox(3,3),1);
        assertEquals(client.getItemQuantityForFoodBox(4,3),2);

        assertEquals(client.getItemQuantityForFoodBox(13,4),1);
        assertEquals(client.getItemQuantityForFoodBox(11,4),1);

        assertEquals(client.getItemQuantityForFoodBox(999,4999),0);
        assertEquals(client.getItemQuantityForFoodBox(-999,-4999),0);


    }

    @Test
    public void getItemNameForFoodBox(){
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }


        assertEquals(client.getItemNameForFoodBox(1,1),"cucumbers");
        assertEquals(client.getItemNameForFoodBox(2,1),"tomatoes");

        assertEquals(client.getItemNameForFoodBox(3,3),"onions");
        assertEquals(client.getItemNameForFoodBox(4,3),"carrots");

        assertEquals(client.getItemNameForFoodBox(13,4),"cabbage");
        assertEquals(client.getItemNameForFoodBox(11,4),"avocado");

        assertEquals(client.getItemNameForFoodBox(999,4999),null);
        assertEquals(client.getItemNameForFoodBox(-999,-4999),null);



    }

    @Test
    public void getItemIdsForFoodBox(){
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }

        Object[] bx1 = client.getItemIdsForFoodBox(1).toArray();
        Object[] bx2 = client.getItemIdsForFoodBox(3).toArray();
        Object[] bx3 = client.getItemIdsForFoodBox(4).toArray();
        Object[] eq1 = {1,2,6};
        Object[] eq2 = {3,4,8};
        Object[] eq3 = {13,11,8,9};
        for (int i = 0; i< bx1.length; i++){
            assertEquals(bx1[i],eq1[i]);

        }
        for (int i = 0; i< bx2.length; i++){
            assertEquals(bx2[i],eq2[i]);

        }
        for (int i = 0; i< bx2.length; i++){
            assertEquals(bx3[i],eq3[i]);

        }

    }

    @Test
    public void getItemsNumberForFoodBox(){
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            String chi = String.valueOf(rand.nextInt(10000));
            client.registerShieldingIndividual(chi);
            client.placeOrder();
        }





    }




}


