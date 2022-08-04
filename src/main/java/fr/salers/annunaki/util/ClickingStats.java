package fr.salers.annunaki.util;

import lombok.Getter;

import java.util.List;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.util
 */

@Getter

public class ClickingStats {

    private final double entropy;
    private final double std;
    private final double kurtosis;
    private final double variance;
    private final double skewness;
    private final double cps;
    private final int outliers;
    private final int duplicates;

    public ClickingStats(final List<Integer> delays) {
        entropy = MathUtil.getEntropy(delays);
        std = MathUtil.getStandardDeviation(delays);
        kurtosis = MathUtil.getKurtosis(delays);
        variance = MathUtil.getVariance(delays);
        skewness = MathUtil.getSkewness(delays);
        cps = MathUtil.getCps(delays);
        outliers = MathUtil.getOutliers(delays).getX().size() + MathUtil.getOutliers(delays).getY().size();
        duplicates = MathUtil.getDuplicates(delays);

    }
}
