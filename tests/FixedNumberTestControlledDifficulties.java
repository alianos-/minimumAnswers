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

import java.util.ArrayList;
import java.util.List;
import minimumAnswers.main.AnswerGenerator;
import minimumAnswers.libraries.OccurrenceSet;
import minimumAnswers.main.OptionServices;


public class FixedNumberTestControlledDifficulties implements Runnable {

    public Thread t;
    private int O; //Number of Options
    private int N; //Number of answers to get
    private int iterations; //How many times to repeat the test    
    /**
     * If set to true it keeps a note of the distribution of difficulties (for verification purposes). Set to
     * false for performance improvement, if you are running multiple tests.
     */
    private static final boolean MEASURE_DIFFICULTIES = false;

    /**
     * Create a test that will attempt to evenly distribute difficulty between 1 and 98. This means that the
     * iterations you pass will be rounded up, to the closest multiple of 98 (or the number of difficulties
     * depending on N).
     *
     * @param iterations
     * @param N
     * @param O
     */
    public FixedNumberTestControlledDifficulties( int iterations, int N, int O ) {
        this.iterations = iterations;
        this.N = N;
        this.O = O;
        t = new Thread( this );
    }

    // This is the entry point for thread.
    @Override
    public void run() {
        //keep a note of the difficulty distributes. 
        OccurrenceSet<Integer> difficultyDistribution;
        if( MEASURE_DIFFICULTIES ) {
            difficultyDistribution = new OccurrenceSet<>();
        }

        //Generate a list of all the difficulties we are going to text
        //because N=2 cannot create the odd difficulties, we need to manually adjust.
        List<Integer> difficulties = new ArrayList<>();
        if( N == 2 ) {
            for( int difficulty = 2; difficulty < 99; difficulty += 2 ) {
                difficulties.add( difficulty );
            }
        }
        else {
            for( int difficulty = 1; difficulty < 99; difficulty += 1 ) {
                difficulties.add( difficulty );
            }
        }

        // divide by the number of difficulties
        // because each itteration will create an instances for each difficulty
        // so if they asked for 1000 itterations, and we produce 50 difficulties
        // we need only 1000/50 itterations internally.
        int totalIterations = (int) Math.ceil( iterations / difficulties.size() );

        int wins = 0; //How many times plurality voting found the right asnwer
        int draws = 0; //How many times pluratity voting could not decide
        for( int iteration = 0; iteration < iterations; iteration++ ) {
            for( Integer difficulty : difficulties ) {
                //require at least 1% difference between the winning option and the next
                //(so as to create a winning option and avoid equalities).
                AnswerGenerator generator = new AnswerGenerator( OptionServices.generate( O, difficulty, difficulty ) );
                OccurrenceSet<String> votes = new OccurrenceSet<>(); //specific number
                for( int n = 0; n < N; n++ ) {
                    votes.add( generator.draw() );
                }
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
        }

        //Print it out if you need it
        System.out.format( "%1d\t%4d\t%.2f\t%.2f",
                O,
                N,
                GenericServices.round( wins / (totalIterations / 100d), 2 ),
                GenericServices.round( draws / (totalIterations / 100d), 2 ) );
        System.out.println();
        //total-wins-draws = the number of wrong results

        if( MEASURE_DIFFICULTIES ) {
            System.out.println( difficultyDistribution );
        }

    }
}