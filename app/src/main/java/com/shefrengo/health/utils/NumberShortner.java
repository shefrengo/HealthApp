package com.shefrengo.health.utils;

import static com.shefrengo.health.utils.Threshold.thresholdFor;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberShortner {

    public static final int REQUIRED_PRECISION = 2;

    public static BigDecimal toPrecisionWithoutLoss(BigDecimal aBigDecimal,
                                                    int aPrecision, RoundingMode aMode) {

        int previousScale = aBigDecimal.scale();
        int previousPrecision = aBigDecimal.precision();
        int newPrecision = Math.max(previousPrecision - previousScale, aPrecision);
        return aBigDecimal.setScale(previousScale + newPrecision - previousPrecision,
                aMode);
    }

    private static BigDecimal scaledNumber(BigDecimal aNumber, RoundingMode aMode) {
      Threshold threshold = thresholdFor(aNumber);
        BigDecimal adjustedNumber = aNumber.movePointLeft(threshold.getNumberOfZeroes());
        BigDecimal scaledNumber = toPrecisionWithoutLoss(adjustedNumber, REQUIRED_PRECISION,
                aMode).stripTrailingZeros();
        // System.out.println("Number: <" + aNumber + ">, adjusted: <" + adjustedNumber
        // + ">, rounded: <" + scaledNumber + ">");

        return scaledNumber;
    }


    public static String shortenedNumber(long aNumber, RoundingMode aMode) {
        boolean isNegative = aNumber < 0;
        BigDecimal numberAsBigDecimal = new BigDecimal(isNegative ? -aNumber : aNumber);
        Threshold threshold = thresholdFor(numberAsBigDecimal);
        BigDecimal scaledNumber = aNumber == 0 ? numberAsBigDecimal : scaledNumber(
                numberAsBigDecimal, aMode);
        if (scaledNumber.compareTo(new BigDecimal("1000")) >= 0) {
            scaledNumber = scaledNumber(scaledNumber, aMode);
            threshold = threshold.getHigherThreshold();
        }
        String sign = isNegative ? "-" : "";
        String printNumber = sign + scaledNumber.stripTrailingZeros().toPlainString()
                + threshold.getSuffix();
        // System.out.println("Number: <" + sign + numberAsBigDecimal + ">, rounded: <"
        // + sign + scaledNumber + ">, print: <" + printNumber + ">");
        return printNumber;
    }
}