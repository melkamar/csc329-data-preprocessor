import java.util.ArrayList;

/**
 * @author Martin Melka /melkamar/
 *         Created on 22.11.14 13:53.
 */
public class Preprocessor {
    public static void main(String[] args) {
        int dataSamples = 10000;

        long time = System.currentTimeMillis();
        ArrayList<TransformSource> sources = new ArrayList<TransformSource>();
        sources.add(new TransformSource("10-year-treasury-daily.csv", 0, 1));
        sources.add(new TransformSource("eur-usd-exchange-rate-daily.csv", 0, 1));
        sources.add(new TransformSource("gas-daily.csv", 0, 1));
        sources.add(new TransformSource("s-and-p-index-daily.csv", 0, 1));
        sources.add(new TransformSource("treasury-future-daily.csv", 0, 1));
        sources.add(new TransformSource("volatility-daily.csv", 0, 1));
        sources.add(new TransformSource("wheat-future-daily.csv", 0, 1));
        sources.add(new TransformSource("gold-daily.csv", 0, 1));

        ArrayList<TransformSource> monthlySources = new ArrayList<TransformSource>();

        /* MONTHLY/QUARTERLY DATA */
        monthlySources.add(new TransformSource("consumer-price-index-monthly.csv", 0, 1));
        monthlySources.add(new TransformSource("houses-sold-monthly.csv", 0, 1));
        monthlySources.add(new TransformSource("iron-ore-monthly.csv", 0, 1));
        monthlySources.add(new TransformSource("retail-and-food-monthly.csv", 0, 1));
        monthlySources.add(new TransformSource("unemployment-monthly.csv", 0, 1));
        monthlySources.add(new TransformSource("usa-inflation-monthly.csv", 0, 1));
        monthlySources.add(new TransformSource("corporate-profits-quarterly.csv", 0, 1));
        monthlySources.add(new TransformSource("gdp-quarterly.csv", 0, 1));
        monthlySources.add(new TransformSource("house-price-quarterly.csv", 0, 1));

        MonthlyDataShifter.shiftToNextMonday(monthlySources);

        for (TransformSource ts: monthlySources){
            ts.filename = ts.filename + ".shifted";
        }

        sources.addAll(monthlySources);
        /* /MONTHLY/QUARTERLY  DATA */


        Transformer.transformFiles(sources, dataSamples);
        Interpolator.interpolateData(sources, 7);

        long thisTime = System.currentTimeMillis() - time;

        System.out.println("\n\n********************************************");
        System.out.println("DONE! Time: "+(thisTime) / 1000 + "." + (thisTime % 1000) + " seconds");
        System.out.println("********************************************");
    }
}
