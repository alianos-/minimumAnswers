 /* 
 * Estimate the minimum amount of answers needed for crowdsourcing systems
 * Copyright 2014, 2015 Andreas Lianos
 * 
 * This file is part of minimumAnswers.
 *
 * minimumAnswers is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 * minimumAnswers is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with minimumAnswers.  If not, see <http://www.gnu.org/licenses/>.
 */
package minimumAnswers.main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * A class to provide static methods with various ways to create the option tables
 *
 * @author Andreas Lianos, andreas.lianos@port.ac.uk
 */
public class OptionServices {

    /**
     * Create a distribution with the given number of options, where the difference between the first and
     * second most common options is always at least minDifference, and not more than maxDifference. <br>For
     * example {@code OptionsGenerator(2,10,90)} will create a generator with 2 options (A,B), who's
     * distribution cannot be closer than 55 to 45, and cannot be further apart than 95 to 5 (regardless of
     * which option get what). The options are named opt1, opt2 etc.<br><br><br>To generate the options we
     * roll a number between 0 and 100 for the first option. We then roll a number between 0 and whatever is
     * left from the previous roll (so the sum cannot exceed 100). The last option gets the remaining
     * percentage (if any)
     *
     *
     * @param numOfOptions How many possible options should the generator have. Must be between [2-100]
     * @param minDifference The minimum difference between the most common option, and the second most common
     * option. Must be between [0-100]
     * @param maxDifference The maximum difference between the most common option, and the second most common
     * option. Must be between [0-100]
     */
    public static Map<String, Integer> generate( int numOfOptions, int minDifference, int maxDifference ) throws IllegalArgumentException {
        //sanity checks
        if( numOfOptions < 2 || numOfOptions > 100 ) {
            throw new IllegalArgumentException( "The possible number of options must be between [2-100] (" + minDifference + " given)" );
        }
        if( minDifference < 0 || minDifference > 100 ) {
            throw new IllegalArgumentException( "The minimum difference must be between [0-100] (" + minDifference + " given)" );
        }
        if( maxDifference < 0 || maxDifference > 100 ) {
            throw new IllegalArgumentException( "The maximum difference must be between [0-100] (" + minDifference + " given)" );
        }
        if( minDifference > maxDifference ) {
            throw new IllegalArgumentException( "The minimum difference cannot be more than the maximum difference" );
        }
        if( minDifference == maxDifference && minDifference == 99 ) {
            throw new IllegalArgumentException( "You can never get a difference of 99. 100-0=100, 99-1=98" );
        }


        Map<String, Integer> options; //the resulting options

        //create options sets until you bump in one that is within the required boundaries
        int difference;
        do {
            options = new HashMap<>();
            int totalPercentage = 0;
            //assign percentages to all intermediate options
            for( int i = 0; i < numOfOptions - 1; i++ ) {
                //see how much percentage we have left, plus one
                //because roll() will generate numbers BELOW the number it is given.
                int maxPercentage = (totalPercentage < 100 ? 101 - totalPercentage : 0);
                // As a result, it also cannot be called with 0. So if we have 0 left, we dont roll at all.
                int percentage = (maxPercentage > 0
                        ? LoggedRandom.getInstance().roll( maxPercentage )
                        : 0);
                totalPercentage += percentage;
                options.put( "opt" + i, percentage );
            }
            //Assign the remaining percentage to the last option    
            int percentage = 100 - totalPercentage;
            options.put( "opt" + (numOfOptions - 1), percentage );

            //see if the generated options are withing the required boundaries
            difference = getDifference( options );
        } while( difference < minDifference || difference > maxDifference );

        return options;
    }

    /**
     * Calculates the difference between the elements with the 2 highest values.
     *
     * @param map
     * @return
     */
    public static int getDifference( Map<String, Integer> map ) {
        Integer max = Collections.max( map.values() );

        int secondMax = 0;
        boolean oneMaxFound = false;
        for( Integer val : map.values() ) {
            if( val == max && !oneMaxFound ) { //if you find max once, ignore it
                oneMaxFound = true;
            }
            else if( val > secondMax || (val == max && oneMaxFound) ) {  //but if you find it again, then we have an equality
                secondMax = val;
            }
        }

        return max - secondMax;
    }

    /**
     * Returns a string with all the keys, who's values are the max value. The resulting List has a size of 1
     * if the maximum value is unique.
     *
     * @param map
     * @return
     */
    public static String findMax( Map<String, Integer> map ) {
        //find the max percentage
        int maxPercentage = Collections.max( map.values() );

        //find which key has the max percentage
        for( Map.Entry<String, Integer> entry : map.entrySet() ) {
            String option = entry.getKey();
            Integer percentage = entry.getValue();

            if( percentage == maxPercentage ) {
                return option;
            }
        }

        return null;
    }

    /**
     * Gets a string representation of the map.
     *
     * @param options
     * @return
     */
    public static String getString( Map<String, Integer> options ) {
        StringBuilder sb = new StringBuilder();
        for( Entry<String, Integer> entry : options.entrySet() ) {
            sb.append( entry.getKey() ).append( ":" ).append( entry.getValue() ).append( "\n" );
        }
        return sb.toString();
    }
}
