package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay20Test {
    
    @Test
    public void testGiftsCount() {
        var giftDelivery = new GiftDelivery(100, 10, 10, false);
        Assertions.assertEquals(10, giftDelivery.countGifts(1));
        Assertions.assertEquals(30, giftDelivery.countGifts(2));
        Assertions.assertEquals(40, giftDelivery.countGifts(3));
        Assertions.assertEquals(70, giftDelivery.countGifts(4));
        Assertions.assertEquals(60, giftDelivery.countGifts(5));
        Assertions.assertEquals(120, giftDelivery.countGifts(6));
        Assertions.assertEquals(80, giftDelivery.countGifts(7));
        Assertions.assertEquals(150, giftDelivery.countGifts(8));
        Assertions.assertEquals(130, giftDelivery.countGifts(9));
    }

}
