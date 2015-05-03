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

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Andreas Lianos
 */
public class Pair<A, B>  {
    
    A valueA;
    B valueB;

    public Pair( A valueA, B valueB ) {
        this.valueA = valueA;
        this.valueB = valueB;
    }

    public A getValueA() {
        return valueA;
    }

    public void setValueA( A valueA ) {
        this.valueA = valueA;
    }

    public B getValueB() {
        return valueB;
    }

    public void setValueB( B valueB ) {
        this.valueB = valueB;
    }

    @Override
    public String toString() {
        return valueA + " - " + valueB;
    }

    @Override
    public boolean equals( Object that ) {
        if( !(that instanceof Pair) ) {
            return false;
        }

        Pair thatPair = (Pair) that;
        if( this.getValueA().equals( thatPair.getValueA() ) && this.getValueB().equals( thatPair.getValueB() ) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.valueA != null ? this.valueA.hashCode() : 0);
        hash = 97 * hash + (this.valueB != null ? this.valueB.hashCode() : 0);
        return hash;
    }
}
