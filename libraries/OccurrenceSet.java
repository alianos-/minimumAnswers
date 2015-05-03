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
package minimumAnswers.libraries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Andreas Lianos
 */
public final class OccurrenceSet<T> implements Set<T> {

    private List<T> elements;
    private List<Integer> occurrences;

    public OccurrenceSet() {
        this.elements = new ArrayList<>();
        this.occurrences = new ArrayList<>();
    }

    public OccurrenceSet( OccurrenceSet<T> initialList ) {
        this();
        for( int i = 0; i < initialList.size(); i++ ) {
            this.setOccurrences( initialList.get( i ), initialList.getOccurrences( i ) );
        }
    }

    public OccurrenceSet( List<T> initialList ) {
        this();
        for( T element : initialList ) {
            this.add( element );
        }
    }

    public List<T> asUniqueList() {
        List<T> list = new ArrayList<>();
        for( T element : elements ) {
            list.add( element );
        }
        return list;
    }

    public List<T> asNonUniqueList() {
        List<T> list = new ArrayList<>();
        for( T element : elements ) {
            for( int i = 0; i < getOccurrences( element ); i++ ) {
                list.add( element );
            }
        }
        return list;
    }

    /**
     * Adds a new element to the list, or increments the counter if the element existed.
     *
     * @param element
     * @return true if the element is just added, false if the counter is incremented
     */
    @Override
    public boolean add( T element ) {

        if( this.contains( element ) ) {
            this.incrementOccurrences( element );
            return false;
        }
        else {
            this.elements.add( element );
            this.occurrences.add( 1 );
            return true;
        }

    }

    /**
     * Adds a new element to the list a number of times. If the element was there, the counter is incremented
     * instead
     *
     * @param element
     * @param occurrences
     * @return true if the element is just added, false if the counter is incremented
     */
    public boolean add( T element, Integer occurrences ) {
        boolean added = this.add( element );
        incrementOccurrences( element, occurrences - 1 );//minus one, because it got one from adding

        return added;
    }

    /**
     * Sets the occurrences of an element to a specific number.
     *
     * @param element
     * @param occurrences
     * @return True if the element is just added, false if it already existed.
     */
    public boolean setOccurrences( T element, Integer occurrences ) {
        boolean newElement = !this.elements.contains( element );
        if( newElement ) {
            this.add( element, occurrences );
        }
        else {
            this.occurrences.set( this.elements.indexOf( element ), occurrences );
        }

        return newElement;
    }

    public int sizeWithOccurrences() {

        int counter = 0;
        for( Integer size : occurrences ) {
            counter += size;
        }

        return counter;
    }

    /**
     * @param i
     * @return the occurrences, of a the searched element in the set. 0 if not found.
     */
    public int getOccurrences( T k ) {
        if( this.elements.contains( k ) ) {
            return this.occurrences.get( indexOf( k ) );
        }
        return 0;
    }

    /**
     * @param i The index of the element
     * @return the occurrences, of a specific element in the set
     */
    public Integer getOccurrences( int i ) {
        return this.occurrences.get( i );
    }

    //@@move to services
    /**
     * Get an ordered copy of the occurrence list
     *
     * @return A new ordered list, Ascending.
     */
    public OccurrenceSet<T> getOrdered() {
        OccurrenceSet<T> ordered = new OccurrenceSet<>();
        for( int i = 0; i < this.size(); i++ ) {
            int max = Integer.MIN_VALUE;
            T minElement = null;
            for( T element : this ) {
                if( getOccurrences( element ) > max && !ordered.contains( element ) ) {
                    max = getOccurrences( element );
                    minElement = element;
                }
            }
            ordered.add( minElement, getOccurrences( minElement ) );
        }
        return ordered;
    }

    //@@move to services
    /**
     * Returns the most common element of the set. If multiple elements share the top, one of them is returned
     * randomly.
     *
     * @return
     */
    public T getTop() {
        for( int i = 0; i < this.size(); i++ ) {
            int max = Integer.MIN_VALUE;
            T maxElement = null;
            for( T element : this ) {
                if( getOccurrences( element ) > max ) {
                    max = getOccurrences( element );
                    maxElement = element;
                }
            }
            return maxElement;
        }
        return null;
    }

    public OccurrenceSet<T> getTop( int num ) {
        OccurrenceSet<T> ordered = this.getOrdered();
        OccurrenceSet<T> result = new OccurrenceSet<>();
        num = (num <= ordered.size() ? num : ordered.size());

        for( int i = 0; i < num; i++ ) {
            result.add( ordered.get( i ), ordered.getOccurrences( i ) );
        }

        return result;
    }

    /**
     * Increment the occurrences of a specific element by 1
     *
     * @param element
     * @return The total amount of occurrences, 0 if element in not in the list
     */
    public Integer incrementOccurrences( T element ) {
        return incrementOccurrences( element, 1 );

    }

    /**
     * Increment the occurrences of a specific element by i
     *
     * @param k the elements who's occurrences you wish to increment
     * @param i the amount by which you want to increment
     * @return The total amount of occurrences, 0 if element in not in the list.
     */
    public Integer incrementOccurrences( T k, int i ) {
        Integer o = this.occurrences.get( indexOf( k ) );
        this.occurrences.set( indexOf( k ), o + i );
        return o + i;
    }

    public T get( int i ) throws ArrayIndexOutOfBoundsException {
        if( i > elements.size() ) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return elements.get( i );
    }

    public int indexOf( Object k ) {
        return elements.indexOf( k );
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    @Override
    public boolean contains( Object o ) {
        return this.elements.contains( o );
    }

    @Override
    public Object[] toArray() {
        return this.elements.toArray();
    }

    @Override
    public <T> T[] toArray( T[] ts ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public boolean remove( Object o ) {
        T removed = this.remove( indexOf( o ) );
        if( removed != null ) {
            return true;
        }
        else {
            return true;
        }
    }

    @Override
    public boolean containsAll( Collection<?> clctn ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * Returns true if at least one new element is added. False if all elements were incremented.
     *
     * @param clctn
     * @return
     */
    @Override
    public boolean addAll( Collection<? extends T> clctn ) {
        boolean added = false;
        for( T element : clctn ) {
            added = (this.add( element ) && added == false ? true : false);
        }
        return added;
    }

    @Override
    public boolean removeAll( Collection<?> clctn ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public boolean retainAll( Collection<?> clctn ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public T set( int i, T e, int occurrences ) {
        this.occurrences.set( i, occurrences );
        return this.elements.set( i, e );
    }

    public T remove( int i ) {
        this.occurrences.remove( i );
        return this.elements.remove( i );
    }

    //@@should be in a services object (as it does not have any direct reference to variables.
    /**
     *
     * @return A deep copy of the List.
     */
    public OccurrenceSet<T> getCopy() {
        OccurrenceSet<T> temp = new OccurrenceSet<T>();
        for( T element : this ) {
            temp.add( element, this.getOccurrences( element ) );
        }
        return temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < elements.size(); i++ ) {
            sb.append( "\n" ).append( occurrences.get( i ) ).append( " x " ).append( elements.get( i ) );
        }
        return sb.toString();
    }

    public int getHighestIndividualOccurrence() {
        int max = 0;
        for( Integer occurrence : occurrences ) {
            if( occurrence > max ) {
                max = occurrence;
            }
        }

        return max;
    }

    public int getLowestIndividualOccurrence() {
        int min = Integer.MAX_VALUE;
        for( Integer occurrence : occurrences ) {
            if( occurrence < min ) {
                min = occurrence;
            }
        }

        return min;
    }

    /**
     * Removed the element from the list.
     *
     * @param valueToRemove The value of the element to be removed
     * @return A Pair with the element and its occurrences at the time of removal.
     */
    public Pair<T, Integer> pop( T valueToRemove ) {
        int toBeRemoved = -1;
        for( int i = 0; i < elements.size(); i++ ) {
            T element = elements.get( i );
            if( element.equals( valueToRemove ) ) {
                toBeRemoved = i;
                break;
            }
        }
        if( toBeRemoved > -1 ) {
            Pair<T, Integer> result = new Pair( this.get( toBeRemoved ), this.getOccurrences( toBeRemoved ) );
            this.remove( toBeRemoved );
            return result;
        }
        else {
            return null;
        }

    }

    public boolean isSameAs( OccurrenceSet<T> that ) {
        if( this.isSubsetOf( that ) && that.isSubsetOf( this ) ) {
            return true;
        }

        return false;
    }

    public boolean isSubsetOf( OccurrenceSet<T> that ) {
        for( T thisT : elements ) {
            if( !that.contains( thisT ) || this.getOccurrences( thisT ) != that.getOccurrences( thisT ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the difference in occurrences between the most common element, and the second most common
     * element
     *
     * @param set
     * @return The difference. If the second contains only element, returns the occurrences of that
     * element<br>0 if two or more elements tie at the top. <br>-1 if the set was empty <br>-2 if the set was
     * null
     */
    public static int topTwoDifference( OccurrenceSet set ) {
        int difference;
        if( set == null ) {
            return -2;
        }
        else if( set.size() == 0 ) {
            return -1;
        }

        Integer firstVotes = set.getOrdered().getOccurrences( 0 );
        if( set.size() > 1 ) {
            Integer secondVotes = set.getOrdered().getOccurrences( 1 );
            difference = firstVotes - secondVotes;
        }
        else {
            return firstVotes; //assume the second option is 0
        }

        return difference;
    }
}
