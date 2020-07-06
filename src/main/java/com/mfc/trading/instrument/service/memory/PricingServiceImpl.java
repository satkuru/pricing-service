package com.mfc.trading.instrument.service.memory;

import com.mfc.trading.instrument.pojo.Price;
import com.mfc.trading.instrument.service.PricingService;
import com.mfc.trading.instrument.service.util.PricingConstant;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.mfc.trading.instrument.service.util.PricingConstant.*;
import static com.mfc.trading.instrument.service.util.PricingConstant.DEFAULT_CACHE_AGE;

public class PricingServiceImpl implements PricingService {

    private ReadWriteLock pricingLock           = new ReentrantReadWriteLock(true);
    private final long maxCacheAge;//in number of days

    public PricingServiceImpl() {
        maxCacheAge= DEFAULT_CACHE_AGE;
    }

    /*
        using multi-level map structure store prices by vendor/instrument or vise versa and each inner
        collection will hold multiple live prices based on various date, using treeset to order them by latest.
        Since the update/modification of the minimal compared with read access ( as the prices are only updated one)
        will use an external read-write to reduce the thread contentions. if the rate of update of the maps are high, these map will
        not be suitable. May need to consider single map , like of ConcurrentHashMap type.
         */
    private Map<String,Map<String, TreeSet<Price>>> pricesByVendorMap = new HashMap<>(5); //expects 5 vendors
    private Map<String,Map<String, TreeSet<Price>>> pricesByInstrumentMap = new HashMap<>(1000); //expect 1000 instr as per requirement

    /**
     * Add new price update from vendors to the service
     * @param price
     */
    public void add(Price price) {
        //apply write lock
        try {
            pricingLock.writeLock().lock();
            //Add it to the price view by Vendors
            addOrUpdatePrices(pricesByVendorMap,price,price.getVendor(),price.getInstrument());
            //add it to the price view by Instruments
            addOrUpdatePrices(pricesByInstrumentMap,price,price.getInstrument(),price.getVendor());
        }finally {
            pricingLock.writeLock().unlock();
        }

    }

    /**
     * Method returns latest of prices for a given instrumentID, if a date is specified, then
     * it returns set of prices based on the date  and the instrument specified. If no prices found
     * for the given date, an empty set will be returned.
     * @param instrumentId
     * @param asOfDate
     * @return
     */
    public Set<Price> getByInstrument(String instrumentId, Date asOfDate) {

        Lock readLock = pricingLock.readLock();
        try{
            readLock.lock();
            if(asOfDate==null){// return the latest prices that matches the instrument ids
                return getPricesByGroupName(instrumentId, pricesByInstrumentMap);
            }else {//return prices with instrument, and that matches given date
                return getPricesByGroupNameWithDate(instrumentId, asOfDate, pricesByInstrumentMap);
            }
        }finally {
            readLock.unlock();
        }

    }




    /**
     * Method returns latest of prices for a given vendor, if a date is specified, then
     * it returns set of prices based on the date  and the vendor specified. If no prices found
     * for the given date, an empty set will be returned.
     * @param vendor
     * @param asOfDate
     * @return
     */
    public Set<Price> getByVendor(String vendor, Date asOfDate) {
        Lock readLock = pricingLock.readLock();
        try{
            readLock.lock();
            if(asOfDate==null) {
                return getPricesByGroupName(vendor, pricesByVendorMap);
            }else{
                return getPricesByGroupNameWithDate(vendor, asOfDate, pricesByVendorMap);
            }
        }finally {
            readLock.unlock();
        }
    }

    /**
     * Method return all the stored in the map
     * @return Set<Price>
     */
    @Override
    public Set<Price> getAllPrices() {
        Lock readLock = pricingLock.readLock();
        try{
            readLock.lock();
            return pricesByVendorMap
                    .values()
                    .stream()
                    .flatMap(p->p
                            .values()
                            .stream()
                            .flatMap(pr->pr.stream())
                    ).collect(Collectors.toSet());
        }finally {
            readLock.unlock();
        }

    }

    /**
     * Clean up expired prices based on current system time;
     */
    public void cleanUp() {
        Set<Price> allPrices = getAllPrices();
        Date oldCacheDate =new Date(System.currentTimeMillis() - maxCacheAge * DAY_IN_MIL_SEC);
        Set<Price> expiredPrices = allPrices.stream().filter(price -> price.getAsOfDate().before(oldCacheDate)).collect(Collectors.toSet());
        Lock writeLock = pricingLock.writeLock();
        for(Price expired: expiredPrices){
            try {
                writeLock.lock();
                deletePrice(expired.getVendor(),expired.getInstrument(),expired,pricesByVendorMap);
                deletePrice(expired.getInstrument(),expired.getVendor(),expired,pricesByInstrumentMap);
            }finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * A generic method to handle prices by Vendors and prices by Instruments views
     * @param priceView
     * @param price
     * @param groupByNameFirst
     * @param groupByNameSecond
     */
    private void addOrUpdatePrices(Map<String,Map<String, TreeSet<Price>>> priceView, Price price,String groupByNameFirst,String groupByNameSecond ){
        if(priceView.containsKey(groupByNameFirst)){
            Map<String, TreeSet<Price>> firstLevelGroup = priceView.get(groupByNameFirst);
            if(firstLevelGroup==null){
                firstLevelGroup = new HashMap<String, TreeSet<Price>>();
                firstLevelGroup.put(groupByNameSecond,new TreeSet<Price>());
                priceView.put(groupByNameFirst,firstLevelGroup);
            }
            //create new map if groupByNameSecond doesn't exist already
            firstLevelGroup.computeIfAbsent(groupByNameSecond, key->new TreeSet<>());
            firstLevelGroup.get(groupByNameSecond).add(price);
        }else {
            HashMap<String, TreeSet<Price>> firstLevelGroup = new HashMap<>();
            firstLevelGroup.computeIfAbsent(groupByNameSecond, key->new TreeSet<>()).add(price);
            priceView.put(groupByNameFirst,firstLevelGroup);
        }
    }

    /**
     *  a common method to get prices by instrument or by vendor from the price view
     * @param groupName
     * @param priceView
     * @return
     */
    private Set<Price> getPricesByGroupName(String groupName, Map<String, Map<String, TreeSet<Price>>> priceView) {
        return priceView
                .get(groupName)
                .values()
                .stream()
                .flatMap(prices -> prices.stream().limit(1))
                .collect(Collectors.toSet());
    }

    /**
     * a common method to get prices by instrument or by vendor from the price view for a specified date
     * @param groupName
     * @param asOfDate
     * @param priceView
     * @return
     */
    private Set<Price> getPricesByGroupNameWithDate(String groupName, Date asOfDate, Map<String, Map<String, TreeSet<Price>>> priceView) {
        return priceView
                .get(groupName)
                .values()
                .stream()
                .flatMap(prices -> prices
                        .stream()
                        .filter(pr -> pr.getAsOfDate().compareTo(asOfDate) == 0)
                ).collect(Collectors.toSet());
    }

    /**
     * This delete method must be invoke from the write lock to the price is removed from the map
     * atomically and completely.
     * @param firstGroupName
     * @param secondGroupName
     * @param price
     * @param priceView
     */
    private void deletePrice(String firstGroupName, String secondGroupName,Price price,Map<String, Map<String, TreeSet<Price>>> priceView) {
        if(priceView.containsKey(firstGroupName)){
            priceView
                    .get(firstGroupName)
                    .computeIfPresent(secondGroupName,(key,val)->{
                        val.remove(price);
                        return val;
                    });
        }
    }
}
