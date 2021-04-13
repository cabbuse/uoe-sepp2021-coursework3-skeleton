/**
 *
 */

package shield;

import org.junit.jupiter.api.*;

import java.util.Properties;
import java.io.InputStream;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */

public class SupermarketClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private SupermarketClientImp client;

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

    client = new SupermarketClientImp(clientProps.getProperty("endpoint"),clientProps.getProperty("name"),clientProps.getProperty("postcode"),clientProps.getProperty("orderN"));
  }


  @Test
  public void testSupermarketNewRegistration() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerSupermarket(name, postCode));
    assertTrue(client.isRegistered());
    assertEquals(client.getName(), name);
  }
  @Test
  public void testSupermarketOrder(){
      Random rand = new Random();
      String chi = String.valueOf(rand.nextInt(10000));
      int orderNo = rand.nextInt(10000);
      ShieldingIndividualClientImp test_shield = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"), clientProps.getProperty("dietary_pref"), clientProps.getProperty("boxChoice"), (String) clientProps.get("changes"), (String) clientProps.getProperty("changes2"));
      test_shield.registerShieldingIndividual(chi);
      assertTrue(client.registerSupermarket(client.getName(), client.getPostCode()));
      assertTrue(client.isRegistered());
      assertTrue(client.recordSupermarketOrder(chi,orderNo));
      assertFalse(client.recordSupermarketOrder("99999",99999));
  }


  @Test
  public void testUpdateOrder(){
      Random rand = new Random();
      String chi = String.valueOf(rand.nextInt(10000));
      int orderNo = rand.nextInt(10000);
      ShieldingIndividualClientImp test_shield = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"), clientProps.getProperty("dietary_pref"), clientProps.getProperty("boxChoice"), (String) clientProps.get("changes"), (String) clientProps.getProperty("changes2"));
      test_shield.registerShieldingIndividual(chi);
      assertTrue(client.registerSupermarket(client.getName(), client.getPostCode()));
      assertTrue(client.isRegistered());
      assertTrue(client.recordSupermarketOrder(chi,orderNo));
      client.updateOrderStatus(Integer.parseInt(client.getOrderN()), "dispatched");
      assertFalse(client.updateOrderStatus(Integer.parseInt(client.getOrderN()),"packed"));
      assertTrue(client.updateOrderStatus(Integer.parseInt(client.getOrderN()),"delivered"));
      assertFalse(client.updateOrderStatus(Integer.parseInt(client.getOrderN()),"abc123"));

  }

}


