/*
Copyright (C) 2000 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS 
(http://www.kclee.com/clemens/java/javancss/).

JavaNCSS is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

JavaNCSS is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with JavaNCSS; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.  */

package javancss;

/**
 * Basic data class to store all metrics attached to a function.
 *
 * @author  Herv� Boutemy
 * @version $Id$
 */
public class FunctionMetric implements Comparable
{
    public String name = ".";
    public int ncss = 0;
    public int ccn = 0;
    public int javadocs = 0;
    public int javadocsLn = 0;
    public int singleLn = 0;
    public int multiLn = 0;

    public FunctionMetric() 
    {
        super();
    }
    
    public void clear()
    {
        name = ".";
        ncss = 0;
        ccn = 0;
        javadocs = 0;
        javadocsLn = 0;
        singleLn = 0;
        multiLn = 0;
    }

    public String toString() {
        return name;
    }

    public int compareTo( Object o )
    {
        return name.compareTo( ((FunctionMetric)o).name );
    }
}
