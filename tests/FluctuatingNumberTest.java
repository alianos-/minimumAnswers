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
import minimumAnswers.main.OptionServices;


public class FluctuatingNumberTest implements Runnable {

    public Thread t;
    private final int O; //Number of Options
    private final int iterations; //How many times to repeat the test    
    /**
     * If set to true it keeps a note of the distribution of difficulties (for verification purposes). Set to
     * false for performance improvement, if you are running multiple tests.
     */
    private static final boolean MEASURE_DIFFICULTIES = false;
    private final int C;

    public FluctuatingNumberTest( int iterations, int C, int O ) {
        this.iterations = iterations;
        this.O = O;
        this.C = C;
        t = new Thread( this );
    }

    @Override
    public void run() {
        //keep a note of the difficulty distributes. 
        OccurrenceSet<Integer> difficultyDistribution;
        if( MEASURE_DIFFICULTIES ) {
            difficultyDistribution = new OccurrenceSet<>();
        }

        double averageVotesUsed = 0;
        int wins = 0; //How many times plurality voting found the right asnwer
        
        for( int iteration = 0; iteration < iterations; iteration++ ) {
            OccurrenceSet<String> votes = new OccurrenceSet<>();

            //require at least 1% difference between the winning option and the next
            //(so as to create a winning option and avoid equalities).
            AnswerGenerator generator = new AnswerGenerator( OptionServices.generate( O, 1, 100 ) );

            //while the condition is not met, draw another vote
            while( (OccurrenceSet.topTwoDifference( votes ) < C) ) {
                votes.add( generator.draw() );
            }

            //see how we did
            int result = GenericServices.getResult( generator.getTopOption(), votes );
            if( result == 1 ) {
                wins++;
            }
            //note: we dont expect a draw because the condition guarantees a difference

            averageVotesUsed += votes.sizeWithOccurrences() / (double) iterations;

            if( MEASURE_DIFFICULTIES ) {
                difficultyDistribution.add( generator.getDifference() );
            }
        }
        
        //Printout
        System.out.format( "%1d\t%1d\t%.2f\t%.2f",
                O,
                C,
                GenericServices.round( averageVotesUsed, 2 ),
                GenericServices.round( wins / (iterations / 100d), 2 ) );
        System.out.println();

        if( MEASURE_DIFFICULTIES ) {
            System.out.println( difficultyDistribution );
        }

    }
}
