package net.sf.xfresh.basket.yalets;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import net.sf.xfresh.basket.model.Basket;
import net.sf.xfresh.basket.model.MarketItem;
import net.sf.xfresh.basket.model.MarketItemManager;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.Yalet;

/**
 * User: Olga Bolshakova
 * Date: 27.06.2007
 * Time: 12:11:55
 */
public abstract class AbstractBasketYalet implements Yalet {    
    protected MarketItemManager manager;
    private final static String PREFIX = "BMI";

    @Required
    public void setManager(MarketItemManager manager) {
        this.manager = manager;
    }
    
    private int getValue(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -10000;
        }
    }
    
    private void addItem(Map.Entry<String, String> entry, Basket basket) {
        if (!entry.getKey().startsWith(PREFIX)) {
            return;
        }
        int uid = getValue(entry.getKey().substring(PREFIX.length()));
        int count = getValue(entry.getValue());
        
        if (uid >= 0 && count > 0) {
            MarketItem item = manager.getItem(uid);
            basket.addItem(item);
            basket.setItemCount(item, count);
        }
    }
    
    protected Basket getBasket(InternalRequest req) {
        Basket basket = new Basket();
        try {
            for (Map.Entry<String, String> cookie : req.getCookies().entrySet()) {
                addItem(cookie, basket);
            }
        } catch (NullPointerException e) {
            //TODO FIXME
        }
        return basket;
    }
    
    protected void setCookies(Basket basket, InternalResponse res) {
        Map<String, String> cookies = new HashMap<String, String>();
        for (MarketItem item : manager.getMarketItems()) {
            cookies.put(PREFIX + new Integer(item.getUid()).toString(), new Integer(basket.getItemCount(item)).toString());
            //System.out.println("id "+ item.getUid() + "count " + basket.getItemCount(item));
        }
        res.setCookies(cookies);
    }
}
