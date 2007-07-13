package net.sf.xfresh.basket.multiusermodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.xfresh.basket.model.Basket;
import net.sf.xfresh.basket.model.MarketItem;


public class MultiUserMarketItemManager {
    
    private final List<Basket> baskets = new ArrayList<Basket>();

    private static final List<MarketItem> items = Arrays.asList(
            new MarketItem(1),
            new MarketItem(2),
            new MarketItem(3),
            new MarketItem(4),
            new MarketItem(5),
            new MarketItem(6),
            new MarketItem(7)
    );
    
    public Basket getBasket(int userID) {
        return baskets.get(userID);
    }

    public List<MarketItem> getMarketItems() {
        return items;
    }
    
    public void addUser() {
        baskets.add(new Basket());
    }
    
    public int getUserID(String userID) {
        try {
            if (userID != null) {
                int id = Integer.parseInt(userID);
                if (id > 0 && id < baskets.size()) {
                    return id;
                }
            }
        } catch (NumberFormatException e) {            
        }
        
        baskets.add(new Basket());
        return baskets.size();
    }
}