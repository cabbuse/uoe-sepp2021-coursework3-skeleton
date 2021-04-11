/**
 *
 */

package shield;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.time.LocalDateTime;
import java.io.InputStream;

import java.util.Random;

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

}
