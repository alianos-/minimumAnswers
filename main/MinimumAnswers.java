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
/*
 * You can contant the author at andreas.lianos@port.ac.uk
 */
package minimumAnswers.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import tests.AnsweringFairnessTest;
import tests.FixedNumberTest;
import tests.FluctuatingNumberTest;

/**
 *
 * @author Andreas Lianos
 */
public class MinimumAnswers {

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        //create the framework for multithreading
        //Any tests we wish to run should be added to the runnables
        ExecutorService executor = Executors.newFixedThreadPool( 16 ); //number of threads
        List<Future> futures = new ArrayList<>();
        List<Runnable> runnables = new ArrayList<>();

        /**
         * ***** CREATE TESTS (ADD NEW TESTS HERE) ******
         * Keep the if(true) blocks to quickly enable or disable tests.
         */
        //Create a random generator, pick 10m votes, see how close we are at the distribution
        if( false ) {
            AnswerGenerator generator = new AnswerGenerator( OptionServices.generate( 3, 1, 100 ) );
            AnsweringFairnessTest answeringFairnessTest = new AnsweringFairnessTest( generator, 10000000 );
            runnables.add( answeringFairnessTest );
        }

        //Perform fixed number tests by adding them to the runnables
        if( false ) {
            int iterations = 100000;
            int minO = 3;
            int maxO = 3;
            int minN = 33;
            int maxN = 33;
            for( int O = minO; O <= maxO; O++ ) {
                for( int N = minN; N <= maxN; N++ ) {
                    FixedNumberTest test = new FixedNumberTest( iterations, N, O );
                    runnables.add( test );
                }
            }
        }      

        //Perform fluctuating number tests by adding them to the runnables
        if( true ) {
            int iterations = 1000000;
            int minO = 6;
            int maxO = 6;
            int minC = 24;
            int maxC = 24;
            for( int O = minO; O <= maxO; O++ ) {
                for( int C = minC; C <= maxC; C++ ) {
                    FluctuatingNumberTest test = new FluctuatingNumberTest( iterations, C, O );
                    runnables.add( test );
                }
            }
        }

        /**
         * ***** RUN THE TESTS (NOTHING TO EDIT) ******
         */
        //start execution of each runnable in a thread
        for( Runnable r : runnables ) {
            futures.add( executor.submit( r ) );
        }
        try {
            //wait until all tasks complete
            executor.shutdown(); //do not accept more tasks
            executor.awaitTermination( Long.MAX_VALUE, TimeUnit.SECONDS );
        } catch( InterruptedException e ) {
        }

        /**
         * ***** CHECK THE ROLLER ******
         */
        //If you want to check the rolls, print everything that ever rolled.
        //This is only usefull if the flag in {@link LoggedRandom} is set to true.
        //This has a huge impact on performance, so it is generally not advices to keep set to true
        //for big tests.
        LoggedRandom.getInstance().printHistory();
    }
}
