package net.sf.xfresh.basket.yalets;

import net.sf.xfresh.basket.model.Basket;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;

/**
 * User: Olga Bolshakova
 * Date: 26.06.2007
 * Time: 18:32:14
 */
public class ShowMarketItemYalet extends AbstractBasketYalet {

    public void process(InternalRequest req, InternalResponse res) {
        Basket basket = getBasket(req);
        res.add(manager.getMarketItems());
        res.add(basket);
        setCookies(basket, res);
    }
}
