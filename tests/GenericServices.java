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
package tests;

import minimumAnswers.main.AnswerGenerator;
import minimumAnswers.libraries.OccurrenceSet;


public class GenericServices {

    /**
     * See if majority voting has found the correct result
     *
     * @param votes An occurrence with the votes we have gathered.
     * @param correctResult The string that identifies the correct result in the votes set
     * @return -1 - Wrong<br> 0 - Cannot decide<br> 1 - Correct
     */
    public static int getResult( String correctResult, OccurrenceSet<String> votes ) {
        //if the first two elements have the same number of votes, we cannot decide
        OccurrenceSet<String> top = votes.getTop( 2 );
        if( top.size() == 2 && top.getOccurrences( 0 ).equals( top.getOccurrences( 1 ) ) ) {
            return 0;
        }
        //See how many times we found the correct result
        else if( correctResult.equals( votes.getTop() ) ) {
            return 1;
        }

        return -1;
    }

    /**
     * Round at the given number of decimals
     *
     * @param value
     * @param places
     * @return
     */
    public static double round( double value, int places ) {
        if( places < 0 ) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow( 10, places );
        value = value * factor;
        long tmp = Math.round( value );
        return (double) tmp / factor;
    }
}
