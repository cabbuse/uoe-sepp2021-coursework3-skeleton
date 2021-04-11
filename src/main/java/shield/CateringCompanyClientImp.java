/**
 *
 */

package shield;

import java.io.IOException;
import java.util.ArrayList;

public class CateringCompanyClientImp implements CateringCompanyClient {

    private final String orderN;
    private final String name;
    private String endpoint;
    private String postcode;
    private boolean registered;
    private String Name;
    private ArrayList<String> registeredPost = new ArrayList<String>();
    private ArrayList<String> registeredNames = new ArrayList<String>();

    public CateringCompanyClientImp(String endpoint ,String name, String postcode, String orderN) {
        this.endpoint =endpoint;
        this.name = name;
        this.postcode = postcode;
        this.orderN = orderN;

    }

    @Override
    /**
     * method to reqister new catering company on server and assign instance variables upon success
     * @param name Name of catering company to be registered
     * @param postCode passed postcode of format "EHnn_n[A-Z][A_Z]"
     * @return True if get request is successful and variables are assigned/ False if get request fails
     */
    public boolean registerCateringCompany(String name, String postCode) {
        String request = "/registerCateringCompany?business_name="+name+"&postcode="+postCode;
        try {
            String response = ClientIO.doGETRequest(endpoint + request);
            System.out.println(response);
            //if (response.equals("registered new")){
                this.Name =name;
                this.postcode = postCode;
                this.registered = true;
                this.registeredPost.add(postCode);
                this.registeredNames.add(name);
            //}
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.registered = false;
        return false;

    }

    @Override
    /**
     * method to update the order status on the client and server on unique shielding individuals order
     * @param orderNumber shielding user unique order identifier of user instance
     * @param status string of new status order is to be changed to
     * @return True if get request is successful and order has been updater/ False if get request is unsuccessful
     */
    public boolean updateOrderStatus(int orderNumber, String status) {
        String request = "/updateOrderStatus?order_id="+orderNumber+"&newStatus="+status;
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
     * returns boolean of success state of registration of catering company instance
     * @return True if instance is succefully registered/ False is registration is incomplete or failed
     */
    public boolean isRegistered() {
    return this.registered;
  }

    @Override
    /**
     * method to return instance variable of instance caterer name
     * @return String of current instance caterer name
     */
    public String getName() {
    return this.Name;
  }

    @Override
    /**
     * method to return instance variable of caterer postcode
     * @return String of current instance postcode
     */
    public String getPostCode() {
    return this.postcode;
  }
    }
