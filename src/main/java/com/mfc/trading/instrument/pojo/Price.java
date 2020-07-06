package com.mfc.trading.instrument.pojo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class Price implements Comparable<Price> {
    private final Date asOfDate;
    private final String assetClass;//for the exercises, will default to equity
    private final String instrument;
    private final String vendor;
    private final String market;
    private final BigDecimal bid;
    private final BigDecimal ask;

    public Price(Date asOfDate, String assetClass, String instrument, String vendor, String market, BigDecimal bid, BigDecimal ask) {
        this.asOfDate = asOfDate;
        this.assetClass = assetClass;
        this.instrument = instrument;
        this.vendor = vendor;
        this.market = market;
        this.bid = bid;
        this.ask = ask;
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public String getInstrument() {
        return instrument;
    }

    public String getVendor() {
        return vendor;
    }

    public String getMarket() {
        return market;
    }
    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    @Override
    public int compareTo(Price o) {
        if(o.asOfDate!=null && asOfDate!=null){
            return o.asOfDate.compareTo(this.asOfDate);
        }else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        return Optional.ofNullable(o)
                .filter(test->test instanceof Price)
                .map(test->(Price)test)
                .filter(that->that.asOfDate.compareTo(this.asOfDate)==0)
                .filter(that->Objects.equals(this.assetClass,that.assetClass))
                .filter(that->Objects.equals(this.instrument,that.instrument))
                .filter(that->Objects.equals(this.market,that.market))
                .filter(that->Objects.equals(this.vendor,that.vendor))
                .isPresent();
    }

    @Override
    public int hashCode() {
        return Objects.hash(asOfDate, assetClass, instrument, vendor, market, bid, ask);
    }
}
