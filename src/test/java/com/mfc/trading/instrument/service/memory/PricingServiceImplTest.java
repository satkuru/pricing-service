package com.mfc.trading.instrument.service.memory;

import com.mfc.trading.instrument.pojo.Price;
import com.mfc.trading.instrument.service.PricingService;
import com.mfc.trading.instrument.service.util.PricingConstant;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PricingServiceImplTest {

    private PricingService serviceUnderTest;
    Date date_04_07_2020  = null;
    Date date_03_07_2020  = null;
    Date date_02_07_2020  = null;
    Date date_01_07_2020  = null;
    Date date_01_06_2020  = null;

    String VENDOR_REUTERS   = "Reuters";
    String VENDOR_BLOOMBERG = "Bloomberg";
    String VENDOR_MARKIT    = "Markit";

    String ASSET_EQUITY     = "Equity";

    String MARKET_XLON      = "XLON";//London

    String ISIN_GB0006640972    = "GB0006640972";
    String ISIN_GB00BCDBXK43    = "GB00BCDBXK43";
    String ISIN_GB00BMH46555    = "GB00BMH46555";

    Price PR_0407_REU_XK43;
    Price PR_0307_REU_XK43;
    Price PR_0307_REU_0972;
    Price PR_0207_REU_0972;
    Price PR_0407_BLM_XK43;
    Price PR_0307_BLM_XK43;
    Price PR_0307_BLM_0972;
    Price PR_0207_BLM_0972;
    Price PR_0207_MKT_6555;
    Price PR_0107_MKT_6555;
    Price PR_0106_REU_0972;
    Price PR_0307_MKT_6555;

    @Before
    public void setUp() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        date_04_07_2020 = new Date(currentTimeMillis + 4* PricingConstant.DAY_IN_MIL_SEC);
        date_03_07_2020 = new Date(currentTimeMillis + 3* PricingConstant.DAY_IN_MIL_SEC);
        date_02_07_2020 = new Date(currentTimeMillis + 2* PricingConstant.DAY_IN_MIL_SEC);
        date_01_07_2020 = new Date(currentTimeMillis + 1* PricingConstant.DAY_IN_MIL_SEC);
        date_01_06_2020 = new Date(currentTimeMillis - 31* PricingConstant.DAY_IN_MIL_SEC);

        PR_0407_REU_XK43 = new Price(date_04_07_2020, ASSET_EQUITY, ISIN_GB00BCDBXK43, VENDOR_REUTERS, MARKET_XLON, BigDecimal.TEN, BigDecimal.TEN);
        PR_0307_REU_XK43 = new Price(date_03_07_2020, ASSET_EQUITY, ISIN_GB00BCDBXK43, VENDOR_REUTERS, MARKET_XLON, BigDecimal.TEN, BigDecimal.ONE);
        PR_0307_REU_0972 = new Price(date_03_07_2020, ASSET_EQUITY, ISIN_GB0006640972, VENDOR_REUTERS, MARKET_XLON, BigDecimal.TEN, BigDecimal.TEN);
        PR_0207_REU_0972 = new Price(date_02_07_2020, ASSET_EQUITY, ISIN_GB0006640972, VENDOR_REUTERS,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        PR_0106_REU_0972 = new Price(date_01_06_2020, ASSET_EQUITY, ISIN_GB0006640972, VENDOR_REUTERS,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        PR_0407_BLM_XK43 = new Price(date_04_07_2020, ASSET_EQUITY, ISIN_GB00BCDBXK43,VENDOR_BLOOMBERG,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        PR_0307_BLM_XK43 = new Price(date_03_07_2020, ASSET_EQUITY, ISIN_GB00BCDBXK43,VENDOR_BLOOMBERG,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        PR_0307_BLM_0972 = new Price(date_03_07_2020, ASSET_EQUITY, ISIN_GB0006640972,VENDOR_BLOOMBERG,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        PR_0207_BLM_0972 = new Price(date_02_07_2020, ASSET_EQUITY, ISIN_GB0006640972,VENDOR_BLOOMBERG,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);

        PR_0307_MKT_6555 = new Price(date_03_07_2020, ASSET_EQUITY, ISIN_GB00BMH46555,VENDOR_MARKIT,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        PR_0207_MKT_6555 = new Price(date_02_07_2020, ASSET_EQUITY, ISIN_GB00BMH46555,VENDOR_MARKIT,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        PR_0107_MKT_6555 = new Price(date_01_07_2020, ASSET_EQUITY, ISIN_GB00BMH46555,VENDOR_MARKIT,MARKET_XLON,BigDecimal.TEN,BigDecimal.TEN);
        serviceUnderTest = new PricingServiceImpl();
    }

    @Test
    public void add() {

        serviceUnderTest.add(PR_0407_REU_XK43);
        serviceUnderTest.add(PR_0307_REU_XK43);
        serviceUnderTest.add(PR_0307_REU_0972);
        Set<Price> allPrices = serviceUnderTest.getAllPrices();
        assertNotNull(allPrices);
        assertThat(allPrices.size(), is(3));
        assertTrue(allPrices.contains(PR_0407_REU_XK43));
        assertTrue(allPrices.contains(PR_0307_REU_XK43));
        assertTrue(allPrices.contains(PR_0307_REU_0972));
    }

    @Test
    public void getByInstrument() {
        //populate the service with the prices
        serviceUnderTest.add(PR_0407_REU_XK43);
        serviceUnderTest.add(PR_0307_REU_XK43);
        serviceUnderTest.add(PR_0307_REU_0972);
        serviceUnderTest.add(PR_0207_REU_0972);
        serviceUnderTest.add(PR_0407_BLM_XK43);
        serviceUnderTest.add(PR_0307_BLM_XK43);
        serviceUnderTest.add(PR_0207_MKT_6555);
        serviceUnderTest.add(PR_0107_MKT_6555);
        Set<Price> byInstrument = serviceUnderTest.getByInstrument(ISIN_GB00BCDBXK43, null);
        assertNotNull(byInstrument);
        assertFalse(byInstrument.isEmpty());
        assertThat(byInstrument.size(),is(2));
        assertTrue(byInstrument.contains(PR_0407_REU_XK43));
        assertTrue(byInstrument.contains(PR_0407_BLM_XK43));

        //Now specify valid date but latest
        Set<Price> byInstrumentByLatestDate = serviceUnderTest.getByInstrument(ISIN_GB0006640972, date_03_07_2020);
        assertNotNull(byInstrumentByLatestDate);
        assertThat(byInstrumentByLatestDate.size(),is(1));
        assertTrue(byInstrumentByLatestDate.contains(PR_0307_REU_0972));
        assertThat(byInstrumentByLatestDate.toArray(new Price[]{})[0].getAsOfDate(),is(date_03_07_2020));

        //now specify older date
        Set<Price> byInstrumentByOldDate = serviceUnderTest.getByInstrument(ISIN_GB0006640972, date_02_07_2020);
        assertNotNull(byInstrumentByOldDate);
        assertThat(byInstrumentByOldDate.size(),is(1));
        assertTrue(byInstrumentByOldDate.contains(PR_0207_REU_0972));
        assertThat(byInstrumentByOldDate.toArray(new Price[]{})[0].getAsOfDate(),is(date_02_07_2020));

        //now specify invalid date
        Set<Price> byInstrumentEmpty = serviceUnderTest.getByInstrument(ISIN_GB0006640972, date_04_07_2020);
        assertNotNull(byInstrumentEmpty);
        assertTrue(byInstrumentEmpty.isEmpty());

        //now Add latest price to the service and see if it return
        Set<Price> byInstrument1 = serviceUnderTest.getByInstrument(ISIN_GB00BMH46555,null);
        assertNotNull(byInstrument1);
        assertThat(byInstrument1.size(),is(1));
        assertTrue(byInstrument1.contains(PR_0207_MKT_6555));

        //now Add a latest price to the service and expect to return it
        serviceUnderTest.add(PR_0307_MKT_6555);
        byInstrument1 = serviceUnderTest.getByInstrument(ISIN_GB00BMH46555,null);
        assertNotNull(byInstrument1);
        assertThat(byInstrument1.size(),is(1));
        assertTrue(byInstrument1.contains(PR_0307_MKT_6555));


    }

    @Test
    public void getByVendor() {
        //populate the service with the prices
        serviceUnderTest.add(PR_0407_REU_XK43);
        serviceUnderTest.add(PR_0307_REU_XK43);
        serviceUnderTest.add(PR_0307_REU_0972);
        serviceUnderTest.add(PR_0207_REU_0972);
        serviceUnderTest.add(PR_0407_BLM_XK43);
        serviceUnderTest.add(PR_0307_BLM_XK43);
        Set<Price> pricesByVendor = serviceUnderTest.getByVendor(VENDOR_REUTERS, null);
        assertNotNull(pricesByVendor);
        assertThat(pricesByVendor.size(),is(2));
        assertTrue(pricesByVendor.contains(PR_0407_REU_XK43));
        assertTrue(pricesByVendor.contains(PR_0307_REU_0972));

        //get with date specified;
        Set<Price> pricesByVendorForADate = serviceUnderTest.getByVendor(VENDOR_REUTERS, date_03_07_2020);
        assertNotNull(pricesByVendorForADate);
        assertThat(pricesByVendorForADate.size(),is(2));

        //specify a date that does not have a price
        Set<Price> pricesByVendorWithDate = serviceUnderTest.getByVendor(VENDOR_REUTERS, date_01_06_2020);
        assertNotNull(pricesByVendorWithDate);
        assertTrue(pricesByVendorWithDate.isEmpty());

    }

    @Test
    public void clean() {
        //add some prices
        serviceUnderTest.add(PR_0307_REU_0972);
        serviceUnderTest.add(PR_0207_REU_0972);
        serviceUnderTest.add(PR_0106_REU_0972);
        Set<Price> allPrices = serviceUnderTest.getAllPrices();
        assertNotNull(allPrices);
        assertThat(allPrices.size(),is(3));
        assertTrue(allPrices.contains(PR_0307_REU_0972));
        assertTrue(allPrices.contains(PR_0207_REU_0972));
        assertTrue(allPrices.contains(PR_0106_REU_0972));
        //now call cleanup on the service
        serviceUnderTest.cleanUp();
        Set<Price> allPricesCleaned = serviceUnderTest.getAllPrices();
        assertNotNull(allPricesCleaned);
        assertThat(allPricesCleaned.size(),is(2));
        assertTrue(allPricesCleaned.contains(PR_0307_REU_0972));
        assertTrue(allPricesCleaned.contains(PR_0207_REU_0972));



    }
}