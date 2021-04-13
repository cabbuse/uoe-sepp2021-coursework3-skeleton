/**
 * To implement
 */

package shield;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class ShieldingIndividualClientImp implements ShieldingIndividualClient {


    // instance variables which are used throughout multiple methods that are
    // individualised to each shielding individual instance
    private String endpoint;
    private String CHI = null;
    private FoodBox foodOrder;
    private String dietary_pref;
    private String boxChoice;
    private List<FoodBox> BoxesShown = new ArrayList<FoodBox>();

    public String getOrderNo() {
        return OrderNo;
    }

    private String OrderNo = null;
    private String orderStatus = null;
    private String ClosestCaterer = null;

    // hash maps using strings a key identifiers
    private Map<String, FoodBox> placedOrders = new HashMap<String, FoodBox>();
    private Map<String, ArrayList<String>> CHI_info = new HashMap<>();
    private List<update> changes;


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

    private class update {
        Integer item_id;

        Integer quantity;

        public update(Integer id, Integer quantity) {
            this.item_id = id;
            this.quantity = quantity;
        }

        public Integer getId() {
            return item_id;
        }

        public void setId(Integer id) {
            this.item_id = id;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

    }

    public ShieldingIndividualClientImp(String endpoint, String dietary_pref, String boxChoice, String changes, String changes2) {
        this.endpoint = endpoint;
        this.dietary_pref = dietary_pref;
        this.boxChoice = boxChoice;

        List<update> responses = new ArrayList<update>();
        Type listType = new TypeToken<List<update>>() {
        }.getType();

        this.changes = new Gson().fromJson(changes, listType);


    }

    @Override
    /**
     * performs a GET HTTP request that reqisters a individual with the server if they dont already have a registration
     * and returns true if successful and false upon failure. function will throw an exception if request if of incorrect format
     * @param CHI the shielding individuals unique identifier
     * @return Boolean of true upon registration success / false upon invalid input or already exists
     */
    public boolean registerShieldingIndividual(String CHI) {
        //construct endpoint request
        if (this.CHI == null) {
            this.CHI = new String(CHI);
            String request = "/registerShieldingIndividual?CHI=" + this.CHI;
            ArrayList<String> info = new ArrayList<String>();

            try {
                String response = ClientIO.doGETRequest(endpoint + request);
                if (response.equals("already registered")){
                    return false;
                }
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                info = new Gson().fromJson(response, listType);
                CHI_info.put(this.CHI, info);
                System.out.println(response);

                return true;
            } catch (IOException ex) {
                return false;
            }

        }
        return false;

    }

    /**
     * Performs a http GET request and returns a list with all the boxes available vith
     * variable passed dietaryPreference
     *
     * @param dietaryPreference the ShieldingIndividuals chosen diet requirement
     * @return list of food boxes available based on diet passed
     */
    private List<FoodBox> getFoodBoxes(String dietaryPreference) {


        String request = "/showFoodBox?orderOption=catering&dietaryPreference=" + dietaryPreference;
        ArrayList<FoodBox> foodBoxes = new ArrayList<FoodBox>();


        try {
            // perform request
            String response = ClientIO.doGETRequest(endpoint + request);
            Type listType = new TypeToken<List<FoodBox>>() {
            }.getType();
            foodBoxes = new Gson().fromJson(response, listType);

        } catch (Exception e) {
            return  foodBoxes;
        }


        return foodBoxes;

    }

    @Override
    /**
     * performs methods to get food boxes available and show what they contain.
     * This method too updates all the boxes that have been shown over the entire instance of the users order
     * @param dietaryPreference the data from the sheilding individual of which boxes based on diet they wish to see
     * @return collection of boxes individual ID values
     */
    public Collection<String> showFoodBoxes(String dietaryPreference) {

        List<FoodBox> responseBoxes = this.getFoodBoxes(dietaryPreference);
        List<String> boxIds = new ArrayList<String>();
        boolean found = false;
        if (BoxesShown.size() == 0) {
            BoxesShown.addAll(responseBoxes);
        } else {
            for (FoodBox x : responseBoxes) {
                if (found == true) {
                    break;
                }
                found = false;
                for (int i = 0; i < BoxesShown.size(); i++) {
                    if (BoxesShown.get(i).id == x.id) {
                        found = true;
                        break;
                    } else {
                        BoxesShown.add(x);
                    }


                }
            }
        }

        for (FoodBox x : responseBoxes) {
            boxIds.add(String.valueOf(Math.round(x.id)));
        }
        return boxIds;
    }

    // **UPDATE2** REMOVED PARAMETER
    @Override
    /**
     * performs a post request of users order. this is based on inputs provided by locating the nearest caterer,
     * the users box choice post being shown food boxes and any amendments made to the food quantities.
     * this method has no return as post request provides data passage for this method
     */
    public boolean placeOrder() {

        if (isRegistered() == true) {

            ClosestCaterer = getClosestCateringCompany();


            List<String> seperated = Arrays.asList(ClosestCaterer.split(","));
            String catererName = seperated.get(1);
            String catererPostcode = seperated.get(2);
            String[] catererPostcodeL = catererPostcode.split(" ");
            catererPostcode = catererPostcodeL[0] + "%20" + catererPostcodeL[1];

            showFoodBoxes(dietary_pref);
            pickFoodBox(Integer.parseInt(boxChoice));
            for (int i = 0; i < changes.size(); i++) {
                changeItemQuantityForPickedFoodBox(changes.get(i).getId(), changes.get(i).getQuantity());
            }
            String order_made = new Gson().toJson(foodOrder);

            try {
                String request = "/placeOrder?individual_id=" + CHI + "&catering_business_name=" + catererName + "&catering_postcode=" + catererPostcode;
                String response = ClientIO.doPOSTRequest(endpoint + request, order_made);
                this.placedOrders.put(response, foodOrder);
                this.OrderNo = response;
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }



    }

    @Override
    /**
     * performs a HTTP POST request with the edited food bog and returns the endpoint response;
     * this method uses helper function to ensure all edits made are of the correct format to ensure the post endpoint request is successful
     * @param orderNumber Shielding individuals unique order identifier to allow correct order to be edited
     * @returns True if the order was successfully change; False is the order edit was unsuccessful
     */
    public boolean editOrder(int orderNumber) {
        Collection<Integer> Items = getItemIdsForOrder(orderNumber);
        ArrayList<Integer> Item = new ArrayList<>(Items);
        ArrayList<String> itemNames = new ArrayList<>();
        for (int i = 0; i < Item.size(); i++) {
            itemNames.add(getItemNameForOrder(Item.get(i),orderNumber));

        }
        for(int i = 0; i < changes.size(); i++) {
            changeItemQuantityForPickedFoodBox(changes.get(i).item_id,changes.get(i).quantity);
        }

        try {
            String request = "/editOrder?order_id=" + this.OrderNo;
            String order_made = new Gson().toJson(foodOrder);
            String response = ClientIO.doPOSTRequest(endpoint + request, order_made);
            if (response.equals("True")){
                return  true;
            }else{
                return false;
            }
        } catch (IOException e) {
            return false;
        }



    }

    @Override
    /**
     * performs a HTTP GET request using the shielding individuals unique instance order number to cancel an order on the server
     * if it is not already dispatched/delivered or cancelled
     * @param orderNumber Shielding individuals unique order identifier for there instance order
     * @return True if order was cancelled successfully based on status/ False if status did not allow for cancelling or request was invalid
     */
    public boolean cancelOrder(int orderNumber) {
        String request = "/cancelOrder?order_id=" + orderNumber;
        String status = getStatusForOrder(orderNumber);
        if (status.equals("0") || status.equals("1")) {
            try {
                String response = ClientIO.doGETRequest(endpoint + request);
                if (response.equals("True")) {
                    System.out.println("order has been succesfully cancelled");
                    return true;
                } else {
                    System.out.println("something went wrong when cancelling your order");
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }


    }

    @Override
    /**
     * calls a method to return the server request on unique order states and then assigns instance variable based
     * on the method return
     * @param orderNumber shielding individuals unique order identification
     * @returns True if method return is of right format and order status can be updated/ False if method return is unknown kind
     */
    public boolean requestOrderStatus(int orderNumber) {
        String response = getStatusForOrder(orderNumber);
        if (response.equals("0") ||response.equals("1") || response.equals("2") || response.equals("3") || response.equals("4")) {
            if (response.equals("0")) {
                orderStatus = "placed";
                return true;
            } else if (response.equals("1")) {
                orderStatus = "packed";
                return true;
            } else if (response.equals("2")) {
                orderStatus = "dispatched";
                return true;
            } else if (response.equals("3")) {
                orderStatus = "delivered";
                return true;
            } else if (response.equals("4")) {
                orderStatus = "cancelled";
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    // **UPDATE**
    @Override
    /**
     * performs HTTP GET request to return all of the currently
     * registered catering companies as a collection
     * @return String collection of all the currently available catering companies
     */
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
            return responses;
        }

    }

    // **UPDATE**
    @Override
    /**
     * Performs a HTTP GET request to return the distance between 2 individual postcodes
     * and return the response as a float
     * @param postCode1 individual postcode in correct syntax format
     * @param postCode2 individual postcode in correct syntax format
     * @return float value of the calculated distance upon server get request
     */
    public float getDistance(String postCode1, String postCode2) {
        String[] seperated1 = postCode1.split(" ");
        String[] seperated2 = postCode2.split(" ");
        postCode1 = seperated1[0] + "_" + seperated1[1];
        postCode2 = seperated2[0] + "_" + seperated2[1];
        String request = "/distance?postcode1=" + postCode1 + "&postcode2=" + postCode2;
        try {
            String response = ClientIO.doGETRequest(endpoint + request);
            float res = Float.parseFloat(response);
            return res;

        } catch (IOException e) {
            return 0;
        }


    }

    @Override
    /**
     * checks whether an individual shielding member has registered their chi
     * @returns true if individual is registered and false on all other situations
     */
    public boolean isRegistered() {

        if (this.CHI == null) {
            return false;
        }
        return true;
    }

    @Override
    /**
     * gets the current instance CHI of the shielding individual
     * @returns shielding individual instance CHI
     */
    public String getCHI() {
        return this.CHI;
    }

    @Override
    /**
     * gets the shielding individuals current unique order id
     * @returns shielding individual instance order id
     */
    public int getFoodBoxNumber() {
        return foodOrder.id;
    }

    public String getDietary_pref() {
        return this.dietary_pref;
    }

    @Override
    /**
     * iterates through all the boxes previously shown to the shielding individual
     * and returns the diet based on the box id passed
     * @param foodBoxId unique identifier for each for box in system
     * @returns null if id does not match available boxes or returns the corresponding diet to food box requested
     */
    public String getDietaryPreferenceForFoodBox(int foodBoxId) {
        String diet = null;
        for (int i = 0; i < BoxesShown.size(); i++) {
            if (BoxesShown.get(i).id == foodBoxId) {
                diet = BoxesShown.get(i).diet;
            }
        }
        return diet;
    }

    @Override
    /**
     * iterates through all shown food boxes of users request
     * and returns the number of items in box from the unique box id
     * @param foodBoxId unique identifier for each for box in system
     * @return number of items in box that has been specified
     */
    public int getItemsNumberForFoodBox(int foodBoxId) {
        int NoItems = 0;
        for (int i = 0; i < BoxesShown.size(); i++) {
            for (int j = 0; j < BoxesShown.get(i).contents.size(); j++) {
                if ((BoxesShown.get(i).id) == foodBoxId) {
                    NoItems += 1;
                }
            }
        }
        return NoItems;
    }

    @Override
    /**
     * iterates through all boxes shown to shielding individual upon other method request
     * and returns all the unique IDs for food item upon the food box identification
     * @param foodBoxId unique identifier for each for box in system
     * @return all itemIds for supplied foodbox id or null if id does not match any food box
     */
    public Collection<Integer> getItemIdsForFoodBox(int foodboxId) {
        Collection<Integer> itemIds = new ArrayList<>();
        for (int i = 0; i < BoxesShown.size(); i++) {
            for (int j = 0; j < BoxesShown.get(i).contents.size(); j++) {
                if ((BoxesShown.get(i).id) == foodboxId ) {
                    itemIds.add(BoxesShown.get(i).contents.get(j).id.intValue());
                }
            }
        }
        if (itemIds.size() == 0){
            Collection<Integer> itemIdsF = new ArrayList<>();
            return itemIdsF;
        }
        return itemIds;


    }

    @Override
    /**
     * iterates through all boxes shown to unique shielding individual
     * and returns the item name upon the specific id for item and food box supplied
     * @param  foodBoxId unique identifier for each for box in system
     * @param  itemId unique identifier for item in contents of food box
     * @return Name of the item requested or null if it does not exist
     */
    public String getItemNameForFoodBox(int itemId, int foodBoxId) {
        String itemName = null;
        for (int i = 0; i < BoxesShown.size(); i++) {
            for (int j = 0; j < BoxesShown.get(i).contents.size(); j++) {
                if ((BoxesShown.get(i).id) == foodBoxId & BoxesShown.get(i).contents.get(j).id == itemId) {
                    itemName = BoxesShown.get(i).contents.get(j).name;
                    return itemName;
                }
            }
        }
        return itemName;
    }

    @Override
    /**
     * iterates through all boxes shown to shielding individual instance
     * and returns the quantity for item upon specific id for food box and item
     * @param  foodBoxId unique identifier for each for box in system
     * @param  itemId unique identifier for item in contents of food box
     * @return quantity of the item requested or 0 if it does not exist
     */
    public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
        int quantity = 0;
        for (int i = 0; i < BoxesShown.size(); i++) {
            for (int j = 0; j < BoxesShown.get(i).contents.size(); j++) {
                if ((BoxesShown.get(i).id) == foodBoxId & BoxesShown.get(i).contents.get(j).id == itemId) {
                    quantity = BoxesShown.get(i).contents.get(j).quantity;
                    return quantity;
                }
            }
        }
        return quantity;
    }

    @Override
    /**
     * method to pick food box based on the current boxes shown to the individual shielding user instance
     * and saves this to instance variable
     * @param foodBoxId unique identifier for each for box in system
     * @return True if food box exists and is not null/ False otherwise
     */
    public boolean pickFoodBox(int foodBoxId) {


        List<FoodBox> foodBoxList = this.getFoodBoxes(dietary_pref);
        for (int i = 0 ; i < foodBoxList.size(); i++){
            if (foodBoxId == foodBoxList.get(i).id){
                FoodBox order_chosen = foodBoxList.get(i);
                foodOrder = order_chosen;
                return true;
            }
        }
        return false;
    }

    @Override
    /**
     *  method to support edit order use case.
     *  located food item according to its unique id and changes it quantity to passed parameter
     *  as long as its a decrease in quantity
     * @param  itemId unique identifier for item in contents of food box
     * @param  quantity amount of items ordered in by unique id
     * @return True if quantity is decreased and unique id exists in order, False if the quantity is greater
     * or item does not exist
     */
    public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
        for (int i = 0; i < foodOrder.contents.size(); i++) {
            if (itemId == foodOrder.contents.get(i).id) {
                if (this.foodOrder.contents.get(i).quantity >= quantity && quantity >= 0) {
                    this.foodOrder.contents.get(i).quantity = quantity;
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    /**
     * method to return all the values of the currently placed orders in the system
     * @return collection of integers of all order Ids created
     */
    public Collection<Integer> getOrderNumbers() {

        String[] OrderNumbers = this.placedOrders.keySet().stream().toArray(String[]::new);
        Collection<Integer> ORD = new ArrayList<Integer>();
        for (int i = 0; i < OrderNumbers.length; i++) {
            ORD.add(Integer.parseInt(OrderNumbers[i]));

        }
        return ORD;
    }

    @Override
    /**
     * GET HTTP request method to return the order status based on the order instance unique id
     * @param orderNumber shielding user unique order identifier of user instance
     * @return return string of order status corresponding to current point in delivery food box is in
     */
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
            return null;
        }

    }

    @Override
    /**
     * method to iterate through current placed orders to find order based on unique order id
     * and return collection of all the item ID's corresponding to that order
     * @param orderNumber shielding user unique order identifier of user instance
     * @return collection of all IDs as integers
     */
    public Collection<Integer> getItemIdsForOrder(int orderNumber) {
        FoodBox current = this.placedOrders.get(String.valueOf(orderNumber));
        if (current == null){
            return null;
        }
        Collection<Integer> itemIDs = new ArrayList<Integer>();
        for (int i = 0; i < current.contents.size(); i++) {
            itemIDs.add(current.contents.get(i).id);
        }
        return itemIDs;
    }

    @Override
    /**
     * method to iterate over unique placed order to find corresponding item name based
     * off unique item ID
     * @param itemId unique identifier for item in contents of food box
     * @param orderNumber shielding user unique order identifier of user instance
     * @return item name corresponding to unique item id
     */
    public String getItemNameForOrder(int itemId, int orderNumber) {
        FoodBox current = this.placedOrders.get(String.valueOf(orderNumber));
        String itemName = null;
        if (current == null){
            return null;
        }
        for (int i = 0; i < current.contents.size(); i++) {
            if (current.contents.get(i).id.equals(itemId)) {
                itemName = current.contents.get(i).name;
            }
        }
        return itemName;
    }

    @Override
    /**
     * method to iterate over current placed orders to get the quantities on the placed order based on
     * the unique instance id and unique item id
     * @param itemId unique identifier for item in contents of food box
     * @param orderNumber shielding user unique order identifier of user instance
     * @return integer of quantity of unique item type requested
     */
    public int getItemQuantityForOrder(int itemId, int orderNumber) {
        FoodBox current = this.placedOrders.get(String.valueOf(orderNumber));
        if (current == null){
            return 0;
        }
        int itemQuant = 0;
        for (int i = 0; i < current.contents.size(); i++) {
            if (current.contents.get(i).id.equals(itemId)) {
                itemQuant = current.contents.get(i).quantity;
            }
        }
        return itemQuant;
    }

    @Override
    /**
     * method to set the new quantity of an order upon order editing order given a unique order number
     * and unique item id and quantity
     */
    public boolean setItemQuantityForOrder(int itemId, int orderNumber, int quantity) {
        FoodBox current = this.placedOrders.get(String.valueOf(orderNumber));
        if (current == null){
            return false;
        }
        for (int i = 0; i < current.contents.size(); i++) {
            if (current.contents.get(i).id.equals(itemId)) {
                if (quantity <= this.placedOrders.get(String.valueOf(orderNumber)).contents.get(i).quantity) {
                    this.placedOrders.get(String.valueOf(orderNumber)).contents.get(i).quantity = quantity;
                    return true;
                }
            }
        }
        return false;
    }

    // **UPDATE2** REMOVED METHOD getDeliveryTimeForOrder

    // **UPDATE**

    // **UPDATE**
    @Override
    /**
     * method to get get closest catering company in currently registered caterers by calling sub methods to get
     * smallest distance metric for all current catering companies
     * @return string of caterer data
     */
    public String getClosestCateringCompany() {
        Collection<String> caterers = new ArrayList<String>();
        caterers = getCateringCompanies();
        ArrayList<String> caterersAR = new ArrayList<>(caterers);
        ArrayList<String> caterer_postcode = new ArrayList<String>();
        ArrayList<String> caterer_number = new ArrayList<String>();
        ArrayList<String> caterer_name = new ArrayList<String>();
        for (int i = 0; i < caterers.size(); i++) {
            List<String> seperated = Arrays.asList(caterersAR.get(i).split(","));
            caterer_number.add(seperated.get(0));
            caterer_name.add(seperated.get(1));
            caterer_postcode.add(seperated.get(2));
        }
        if (CHI_info.get(getCHI()).size() == 0){
            return CHI_info.get(this.CHI).get(0);
        }
        float closestN = getDistance(CHI_info.get(this.CHI).get(0), caterer_postcode.get(0));
        String closest = caterersAR.get(0);

        for (int i = 0; i < caterer_postcode.size(); i++) {
            if (closestN >= getDistance(CHI_info.get(this.CHI).get(0), caterer_postcode.get(i))) {
                closestN = getDistance(CHI_info.get(this.CHI).get(0), caterer_postcode.get(i));
                closest = caterersAR.get(i);
            }

        }
        return closest;
    }
}
