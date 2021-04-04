/**
 * To implement
 */

package shield;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.time.LocalDateTime;
import java.util.List;

public class ShieldingIndividualClientImp implements ShieldingIndividualClient {

    private String endpoint;
    private String CHI = null;
    private FoodBox foodOrder;
    private String dietary_pref;
    private String Order_id = null;
    private String boxChoice;


    private class Order {

        public Order(Integer id, String name, Integer quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        Integer id;
        String name;
        Integer quantity;

    }

    private class FoodOrder {
        public FoodOrder() {
        }

        ArrayList<Order> contents = new ArrayList<Order>();

        void addOrder(Order o) {
            this.contents.add(o);
        }
    }

    private class FoodBox extends FoodOrder {

        public FoodBox(String delivered_by, String diet, int id, String name) {
            this.delivered_by = delivered_by;
            this.diet = diet;
            this.id = id;
            this.name = name;
        }

        public String getDeliverd_by() {
            return delivered_by;
        }

        public void setDeliverd_by(String delivered_by) {
            this.delivered_by = delivered_by;
        }

        private String delivered_by;
        private String diet;
        private int id;
        private String name;

    }


    public ShieldingIndividualClientImp(String endpoint, String dietary_pref,  String boxChoice) {
        this.endpoint = endpoint;
        this.dietary_pref = dietary_pref;
        this.boxChoice = boxChoice;
    }

    @Override
    public boolean registerShieldingIndividual(String CHI) {
        //construct endpoint request
        if (this.CHI == null) {
            this.CHI = new String(CHI);
            String request = "/registerShieldingIndividual?CHI=" + this.CHI;


            try {
                String response = ClientIO.doGETRequest(endpoint + request);
                System.out.println(response);
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return false;

    }


    private List<FoodBox> getFoodBoxes(String dietaryPreference) {


        String request = "/showFoodBox?orderOption=catering&dietaryPreference=" + dietaryPreference;
        // setup the response recepient
        ArrayList<FoodBox> foodBoxes = new ArrayList<FoodBox>();


        try {
            // perform request
            String response = ClientIO.doGETRequest(endpoint + request);

            // unmarshal response
            //foodBoxes = new Gson().fromJson(response, foodBoxes.getClass().get);

            Type listType = new TypeToken<List<FoodBox>>() {} .getType();
            foodBoxes = new Gson().fromJson(response, listType);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return foodBoxes;

    }

    @Override
    public Collection<String> showFoodBoxes(String dietaryPreference) {

        List<FoodBox> responseBoxes = this.getFoodBoxes(dietaryPreference);
        List<String> boxIds = new ArrayList<String>();

        for (FoodBox x : responseBoxes) {
            boxIds.add(String.valueOf(Math.round(x.id)));
        }

        return boxIds;
    }

  // **UPDATE2** REMOVED PARAMETER
    @Override
  public boolean placeOrder() {

        showFoodBoxes(dietary_pref);
        pickFoodBox(Integer.parseInt(boxChoice));
        String order_made = new Gson().toJson(foodOrder);
        try{
            String request = "/placeOrder?individual_id="+CHI;
            String response = ClientIO.doPOSTRequest(endpoint+request, order_made);
            this.Order_id = response;
            int i  = 0;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public boolean editOrder(int orderNumber) {
        String request = "/editOrder?order_id=" + orderNumber;

        return false;
    }

    @Override
    public boolean cancelOrder(int orderNumber) {
        String request = "/cancelOrder?order_id=" + orderNumber;
        try {
            String response = ClientIO.doGETRequest(endpoint + request);
            if (response.equals("true")) {
                System.out.println("order has been succesfully cancelled");
                return true;
            } else {
                System.out.println("something went wrong when cancelling your order");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean requestOrderStatus(int orderNumber) {
        String response = getStatusForOrder(orderNumber);
        if (response.equals("1") || response.equals("2") || response.equals("3") || response.equals("4")) {
            return true;
        } else {
            return false;
        }
    }

    // **UPDATE**
    @Override
    public Collection<String> getCateringCompanies() {
        String request = "/getCaterers";
        List<String> responses = new ArrayList<String>();
        try {
            String recall = ClientIO.doGETRequest(endpoint + request);
            Type listType = new TypeToken<List>() {
            }.getType();
            responses = new Gson().fromJson(recall, listType);
            return responses;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responses;
    }

    // **UPDATE**
    @Override
    public float getDistance(String postCode1, String postCode2) {

        String request = "/distance?postcode1=" + postCode1 + "postcode2=" + postCode2;
        try {
            String response = ClientIO.doGETRequest(endpoint + request);
            float res = Float.parseFloat(response);
            return res;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean isRegistered() {

        if (this.CHI == null) {
            return false;
        }
        return true;
    }

    @Override
    public String getCHI() {
        return this.CHI;
    }

    @Override
    public int getFoodBoxNumber() {
        return foodOrder.id;
    }

    @Override
    public String getDietaryPreferenceForFoodBox(int foodBoxId) {
        return String.valueOf(foodOrder.diet);
    }

    @Override
    public int getItemsNumberForFoodBox(int foodBoxId) {
        return 0;
    }

    @Override
    public Collection<Integer> getItemIdsForFoodBox(int foodboxId) {
        return null;

    }

    @Override
    public String getItemNameForFoodBox(int itemId, int foodBoxId) {
        String itemName = null;
        for (int i = 0; i<foodOrder.contents.size(); i++){
            if (itemId == foodOrder.contents.get(i).id){
                itemName = foodOrder.contents.get(i).name;
            }
        }
        return itemName;
    }

    @Override
    public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
        int quantity = 0;
        for (int i = 0; i<foodOrder.contents.size(); i++){
            if (itemId == foodOrder.contents.get(i).id){
                quantity = foodOrder.contents.get(i).quantity;
            }
        }
        return quantity;
    }

    @Override
    public boolean pickFoodBox(int foodBoxId) {

        List<FoodBox> foodBoxList = this.getFoodBoxes(dietary_pref);
        FoodBox order_chosen = foodBoxList.get(foodBoxId);
        foodOrder = order_chosen;
        return true;
    }

    @Override
    public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
        //this.foodOrder.conte#.
        return false;
    }

    @Override
    public Collection<Integer> getOrderNumbers() {
        return null;
    }

    @Override
    public String getStatusForOrder(int orderNumber) {
        String request = "/requestStatus?order_id=" + orderNumber;
        try {
            String response = ClientIO.doGETRequest(endpoint + request);
            if (response.equals("1")) {
                System.out.println("order is packed");
                return response;
            } else if (response.equals("2")) {
                System.out.println("order is dispatched");
                return response;
            } else if (response.equals("3")) {
                System.out.println("order is delivered");
                return response;
            } else if (response.equals("4")) {
                System.out.println("order is cancelled");
                return response;
            } else {
                System.out.println("something went wrong");
                return response;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Integer> getItemIdsForOrder(int orderNumber) {
        return null;
    }

    @Override
    public String getItemNameForOrder(int itemId, int orderNumber) {
        return null;
    }

    @Override
    public int getItemQuantityForOrder(int itemId, int orderNumber) {
        return 0;
    }

    @Override
    public boolean setItemQuantityForOrder(int itemId, int orderNumber, int quantity) {
        return false;
    }

  // **UPDATE2** REMOVED METHOD getDeliveryTimeForOrder

  // **UPDATE**

    // **UPDATE**
    @Override
    public String getClosestCateringCompany() {
        return null;
    }
}
