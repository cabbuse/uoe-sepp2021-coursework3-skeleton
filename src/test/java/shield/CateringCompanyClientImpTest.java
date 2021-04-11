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

public class CateringCompanyClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private CateringCompanyClientImp client;

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

    client = new CateringCompanyClientImp(clientProps.getProperty("endpoint"),clientProps.getProperty("name"),clientProps.getProperty("postcode"),clientProps.getProperty("orderN"));
  }


  @Test
  public void testCateringCompanyNewRegistration() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = String.valueOf(rand.nextInt(1000));
    String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    char letter = abc.charAt(rand.nextInt(abc.length()));
    char letter2 = abc.charAt(rand.nextInt(abc.length()));
    postCode = "EH" + (String.valueOf(rand.nextInt(17)))+ "%20" + (String.valueOf(rand.nextInt(9)))  + letter + letter2;
    assertTrue(client.registerCateringCompany(name, postCode));
    assertTrue(client.isRegistered());
    assertEquals(client.getName(), name);
  }

  @Test
  public void updateOrder(){
      int ordersize = 5;
      Random rand = new Random();
      String chi = String.valueOf(rand.nextInt(10000));
      testCateringCompanyNewRegistration();
      ShieldingIndividualClientImp test = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"), clientProps.getProperty("dietary_pref"), clientProps.getProperty("boxChoice"), (String) clientProps.get("changes"));

      for (int i = 0 ; i <ordersize ; i++){
          test.registerShieldingIndividual(chi);
          test.placeOrder();
      }
      Collection<Integer> ordNos = test.getOrderNumbers();
      ArrayList<Integer> orders = new ArrayList<>();
      orders = (ArrayList<Integer>) ordNos;

      assertTrue(client.updateOrderStatus(Integer.parseInt(test.getOrderNo()), "packed"));
      assertFalse(client.updateOrderStatus(999999, "packed"));

  }
}
