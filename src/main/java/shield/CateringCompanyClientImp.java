/**
 *
 */

package shield;

import java.io.IOException;

public class CateringCompanyClientImp implements CateringCompanyClient {

    private String endpoint;
    private String postcode;
    private boolean registered;
    private String Name;

    public CateringCompanyClientImp(String endpoint) {

    }

    @Override
    public boolean registerCateringCompany(String name, String postCode) {
        String request = "/registerCateringCompany?business_name="+name+"&postcode="+postCode;
        this.Name =name;
        this.postcode = postCode;
        this.registered = true;
        try {
            String response = ClientIO.doGETRequest(endpoint + request);
            System.out.println(response);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.registered = false;
        return false;

    }

    @Override
    public boolean updateOrderStatus(int orderNumber, String status) {
        String request = "/updateOrderStatus?order_id=42&newStatus="+status;
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
    public boolean isRegistered() {
    return this.registered;
  }

    @Override
    public String getName() {
    return this.Name;
  }

    @Override
    public String getPostCode() {
    return this.postcode;
  }
    }
