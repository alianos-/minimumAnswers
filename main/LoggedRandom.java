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

import minimumAnswers.libraries.OccurrenceSet;
import java.util.HashMap;
import java.util.Map;


public class LoggedRandom {

    /**
     * If set to true, will use the singleton from {@link minimumAnswers.Random}. The method that rolls
     * numbers needs to be syncronized for multithreading, so for performance set this flag to false.
     */
    public static final boolean LOGGED_RANDOM = false;
    private java.util.Random random = new java.util.Random();
    private HashMap<Integer, OccurrenceSet<Integer>> history = new HashMap<>();

    /**
     * This Random is a singleton, the same object is always returned.
     *
     * @return
     */
    public static LoggedRandom getInstance() {
        return RandomHolder.INSTANCE;
    }

    private static class RandomHolder {

        private static final LoggedRandom INSTANCE = new LoggedRandom();
    }

    /**
     * Delegates the roll to nextInt from {@link java.util.Random} and logs the result depending on
     * {@link SingletonRandom.GLOBAL_RANDOM}
     *
     * @param n
     * @return
     */
    public int roll( int n ) {
        return LoggedRandom.LOGGED_RANDOM
                ? LoggedRandom.getInstance().rollLogged( n )
                : LoggedRandom.getInstance().rollUnlogged( n );
    }

    /**
     * Delegates the roll to nextInt from {@link java.util.Random} and does not log the roll
     *
     * @param n
     * @return
     */
    private int rollUnlogged( int n ) {
        return random.nextInt( n );
    }

    /**
     * Delegates the roll to nextInt from {@link java.util.Random} and logs the roll
     *
     * @param n
     * @return
     */
    private synchronized int rollLogged( int n ) {
        int nextInt = random.nextInt( n );
        OccurrenceSet<Integer> rolls = history.get( n );
        if( rolls == null ) {
            rolls = new OccurrenceSet<>();
        }
        rolls.add( nextInt );
        history.put( n, rolls );

        return nextInt;
    }

    /**
     * Print all the rolls that took place. There is a header showing the value {@link Random#nextInt(int)}
     * was called with, followed by the distribution of the rolls.
     */
    public void printHistory() {
        for( Map.Entry<Integer, OccurrenceSet<Integer>> entry : history.entrySet() ) {
            System.out.println( "\nMax number to roll: " + entry.getKey() + " Total rolls: " + entry.getValue().sizeWithOccurrences() );
            System.out.println( entry.getValue().getOrdered() );
        }
    }

    public HashMap<Integer, OccurrenceSet<Integer>> getHistory() {
        return history;
    }
}
