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

import java.util.Map;
import minimumAnswers.main.AnswerGenerator;
import minimumAnswers.libraries.OccurrenceSet;

/**
 * Given a generator and a number of answers to draw, prints out how close the generated results are to the
 * distribution. We do this to see if the random generator consistently favours a roll or not.
 */
public class AnsweringFairnessTest implements Runnable {

    private final Integer N;
    private final AnswerGenerator generator;

    public AnsweringFairnessTest( AnswerGenerator generator, Integer N ) {
        this.generator = generator;
        this.N = N;
    }

    @Override
    public void run() {
        OccurrenceSet<String> votes = new OccurrenceSet<>(); //specific number
        for( int n = 0; n < N; n++ ) {
            votes.add( generator.draw() );
        }
        Map<String, Integer> options = generator.getOptionsCopy();

        for( String option : votes ) {
            Double estimated = votes.getOccurrences( option ) * 100 / (N + 0.0);
            System.out.format( "%5s|%3d| %2f\n", option, options.get( option ), estimated );
        }
    }
}
