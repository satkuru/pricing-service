package com.mfc.trading.instrument.service;

import com.mfc.trading.instrument.pojo.Price;

import java.util.Date;
import java.util.Set;

public interface PricingService {
    void add(Price price);
    Set<Price> getByInstrument(String instrumentId, Date asOfDate);
    Set<Price> getByVendor(String vendor, Date date_03_07_2020);
    Set<Price> getAllPrices();
    void cleanUp();
}
