package org.intraportal.persistence.mappers;

import java.text.DecimalFormat;

public class PersistenceFormatSettings {

    public static DecimalFormat getDecimalFormatInstance() {
        var decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);

        var decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        return decimalFormat;
    }

}
