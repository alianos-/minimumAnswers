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


public class FixedNumberTest implements Runnable {
    // Using join() to wait for threads to finish.

    public Thread t;
    private final int O; //Number of Options
    private final int N; //Number of answers to get
    private final int iterations; //How many times to repeat the test    
    /**
     * If set to true it keeps a note of the distribution of difficulties (for verification purposes). Set to
     * false for performance improvement, if you are running multiple tests.
     */
    private static final boolean MEASURE_DIFFICULTIES = false;

    public FixedNumberTest( int iterations, int N, int O ) {
        this.iterations = iterations;
        this.N = N;
        this.O = O;
        t = new Thread( this );
    }

    @Override
    public void run() {try{
        //keep a note of the difficulty distributes. 
        OccurrenceSet<Integer> difficultyDistribution;
        if( MEASURE_DIFFICULTIES ) {
            difficultyDistribution = new OccurrenceSet<>();
        }

        int wins = 0; //How many times plurality voting found the right asnwer
        int draws = 0; //How many times pluratity voting could not decide
        
        for( int iteration = 0; iteration < iterations; iteration++ ) {
            //require at least 1% difference between the winning option and the next
            //(so as to create a winning option and avoid equalities).
            AnswerGenerator generator = new AnswerGenerator( OptionServices.generate( O, 1, 100 ) );
            
            OccurrenceSet<String> votes = new OccurrenceSet<>();
            //draw N votes
            for( int n = 0; n < N; n++ ) {
                votes.add( generator.draw() );
            }
            
            //see how we did
            int result = GenericServices.getResult( generator.getTopOption(), votes );
            if( result == 1 ) {
                wins++;
            }
            else if( result == 0 ) {
                draws++;
            }

            if( MEASURE_DIFFICULTIES ) {
                difficultyDistribution.add( generator.getDifference() );
            }
        }

        //Print it out if you need it
        System.out.format( "%1d\t%4d\t%.2f\t%.2f",
                O,
                N,
                GenericServices.round( wins / (iterations / 100d), 2 ),
                GenericServices.round( draws / (iterations / 100d), 2 ) );
        System.out.println();
        //note: iterations-wins-draws = the number of absolute wrong results

        if( MEASURE_DIFFICULTIES ) {
            System.out.println( difficultyDistribution );
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    }
}