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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andreas Lianos, andreas.lianos@port.ac.uk
 */
public class AnswerGenerator {

    private Map<String, Integer> options = new HashMap<>();
    private String[] rollerArray = new String[100];
    private final int difference;
    private final String topOption;

    /**
     * create a generator with the given options. The options Map contains a key with the name of the options,
     * and the percentage that this option should occur.
     *
     * @param options A ready made map of options and their probability.
     */
    public AnswerGenerator( Map<String, Integer> options ) {
        this.options = options;
        //we'll need that later, we might as well work it out now
        this.difference = OptionServices.getDifference( this.options );
        this.topOption = OptionServices.findMax( this.options );

        initializeRollerArray();
    }

    public int getDifference() {
        return difference;
    }

    public String getTopOption() {
        return topOption;
    }

    /**
     * Creates the roller array based on the options. This the array upon which we later roll the dice to
     * decide what answer to generate.
     */
    private void initializeRollerArray() {
        int totalPercentage = 0;
        for( Map.Entry<String, Integer> entry : options.entrySet() ) {
            String option = entry.getKey();
            Integer percentage = entry.getValue();

            for( int i = totalPercentage; i < totalPercentage + percentage; i++ ) {
                rollerArray[i] = option;
            }
            totalPercentage += percentage;
        }
    }

    public String draw() {
        //we use a custom singleton Random so we can keep a copy of all the rolls,
        //to check that the distribution of rolls is fair.
        //or use the local random for performance
        int nextInt = LoggedRandom.getInstance().roll( 100 );
        return rollerArray[nextInt];
    }

    public int size() {
        return options.size();
    }

    public Map<String, Integer> getOptionsCopy() {
        Map<String, Integer> copy = new HashMap<>();
        for( Map.Entry<String, Integer> entry : options.entrySet() ) {
            copy.put( entry.getKey(), entry.getValue() );
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( Map.Entry<String, Integer> entry : options.entrySet() ) {
            sb.append( entry.getKey() ).append( "\t" ).append( entry.getValue() ).append( "\n" );
        }
        sb.append( "Difference\t" ).append( this.difference ).append( "\n" );
        return sb.toString();
    }
}
