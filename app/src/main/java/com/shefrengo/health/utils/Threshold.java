package com.shefrengo.health.utils;

import java.math.BigDecimal;

public enum Threshold {
    TRILLION("1000000000000", 12, 't', null),
    BILLION("1000000000", 9, 'b', TRILLION),
    MILLION("1000000", 6, 'm', BILLION),
    THOUSAND("1000", 3, 'k', MILLION),
    ZERO("0", 0, null, THOUSAND);

    protected Character suffix;
    private BigDecimal value;
    private int zeroes;
    private Threshold higherThreshold;

    Threshold(String aValueString, int aNumberOfZeroes, Character aSuffix,
              Threshold aThreshold) {
        value = new BigDecimal(aValueString);
        zeroes = aNumberOfZeroes;
        suffix = aSuffix;
        higherThreshold = aThreshold;


    }

    public static Threshold thresholdFor(long aValue) {
        return thresholdFor(new BigDecimal(aValue));
    }

    public static Threshold thresholdFor(BigDecimal aValue) {
        for (Threshold eachThreshold : Threshold.values()) {
            if (eachThreshold.value.compareTo(aValue) <= 0) {
                return eachThreshold;
            }
        }
        return TRILLION; // shouldn't be needed, but you might have to extend the enum
    }

    public int getNumberOfZeroes() {
        return zeroes;
    }

    public String getSuffix() {
        return suffix == null ? "" : "" + suffix;
    }

    public Threshold getHigherThreshold() {
        return higherThreshold;
    }
}