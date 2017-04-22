package com.udacity.stockhawk.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Misceanelous tools to format numbers string or dates
 */

public class FormatterUtils {

    public static final DecimalFormat DOLLAR_FORMAT = createDollarFormatter();
    public static final DecimalFormat DOLLAR_FORMAT_WITHPLUS = createDollarWithPlusFormatter();
    public static final DecimalFormat PERCENTAGE_FORMAT = createPercentFormatter();
    public static final DecimalFormat DECIMAL_FORMAT_WITHPLUS = createDecimalWithPlusFormatter();
    public static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) DecimalFormat.getInstance();

    private static DecimalFormat createDecimalWithPlusFormatter() {
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setPositivePrefix("+");

        return formatter;
    }


    private static final DecimalFormat createDollarFormatter() {
        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

        return dollarFormat;
    }

    private static final DecimalFormat createDollarWithPlusFormatter() {
        DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");

        return dollarFormatWithPlus;
    }

    private static final DecimalFormat createPercentFormatter() {
        DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        return percentageFormat;
    }

}
