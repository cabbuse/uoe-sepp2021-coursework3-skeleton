/**
 *
 */

package shield;

import java.io.IOException;
import java.util.ArrayList;

public class SupermarketClientImp implements SupermarketClient {

    private final String name;
    private final String postcode;
    private final String orderN;
    private String endpoint;
    private boolean registered;
    private ArrayList<String> ordersMade = new ArrayList<String>();
    private String order_id;

    public SupermarketClientImp(String endpoint, String name, String postcode, String orderN) {
        this.endpoint = endpoint;
        this.name = name;
        this.postcode = postcode;
        this.orderN = orderN;
    }

    @Override
    /**
     * method to reqister new supermarket on server and assign instance variables upon success
     * @param name Name of catering company to be registered
     * @param postCode passed postcode of format "EHnn_n[A-Z][A_Z]"
     * @return True if get request is successful and variables are assigned/ False if get request fails
     */
    public boolean registerSupermarket(String name, String postCode) {
        String request = "/registerSupermarket?business_name="+name+"&postcode="+postCode;
        try {
            String response = ClientIO.doGETRequest(endpoint + request);
            System.out.println(response);
            this.registered = true;
            this.order_id = response;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.registered = false;
        return false;
  }

    // **UPDATE**
  // **UPDATE2** ADDED METHOD
  @Override
  /**
   * method to record an order made to a supermarket by a HTTP GET request to server
   * @param CHI unique instance of shielding users identifier for orders
   * @param orderNumber shielding user unique order identifier of user instance
   * @return True if get request returns response successfully and instance variable are created/
   * False is request fails for HTTP GET
   */
  public boolean recordSupermarketOrder(String CHI, int orderNumber) {

        String request = "/recordSupermarketOrder?individual_id="+CHI+"&order_number="+orderNumber+"&supermarket_business_name="+ this.name +"&supermarket_postcode="+this.postcode;
        try{
            String response = ClientIO.doGETRequest(endpoint+request);
            this.order_id = response;
            this.ordersMade.add(response);
            return true;


        } catch (IOException e) {
            e.printStackTrace();
        }
      return false;
  }

  // **UPDATE**

    @Override
    /**
     * method to update the order status on the client and server on unique shielding individuals order
     * @param orderNumber shielding user unique order identifier of user instance
     * @param status string of new status order is to be changed to
     * @return True if get request is successful and order has been updater/ False if get request is unsuccessful
     */
    public boolean updateOrderStatus(int orderNumber, String status) {
        String request = "/updateSupermarketOrderStatus?order_id="+orderNumber+"&newStatus="+status;
        try{
            String response = ClientIO.doGETRequest(endpoint+request);
            if (response.equals("True")){
                System.out.println("order status has been updated to "+status);
                return true;
            }else{
                System.out.println("something went wrong updating your order");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    /**
     * returns boolean of success state of registration of supermarket instance
     * @return True if instance is succefully registered/ False is registration is incomplete or failed
     */
    public boolean isRegistered() {
    return this.registered;
  }

    @Override
    /**
     * method to return instance variable of instance supermarket name
     * @return String of current instance supermarket name
     */
    public String getName() {
    return this.name;
  }

    @Override
    /**
     * method to return instance variable of supermarket postcode
     * @return String of current instance postcode
     */
    public String getPostCode() {
    return this.postcode;
  }

  public String getOrder_id(){
        return this.order_id;
  }
}
