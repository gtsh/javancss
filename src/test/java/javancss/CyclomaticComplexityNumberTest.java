/*
Copyright (C) 2000 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS
(http://www.kclee.de/clemens/java/javancss/).

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

import java.util.List;

public class CyclomaticComplexityNumberTest extends AbstractTestCase
{
    public void testCyclomaticComplexityNumber40()
    {
        // CCN for return and throw
        Javancss javancss = measureTestFile( 40 );
        List<FunctionMetric> metrics = javancss.getFunctionMetrics();
        assertEquals( 1, metrics.size() );
        assertEquals( "CCN", metrics.get( 0 ).ccn, 3 );
    }

    public void testCyclomaticComplexityNumber41()
    {
        Javancss javancss = measureTestFile( 41 );
        List<FunctionMetric> metrics = javancss.getFunctionMetrics();
        assertEquals( "CCN", metrics.get( 0 ).ccn, 3 );
        assertEquals( "CCN", metrics.get( 1 ).ccn, 1 );
        assertEquals( "CCN", metrics.get( 2 ).ccn, 3 );
        assertEquals( "CCN", metrics.get( 3 ).ccn, 3 );
        assertEquals( "CCN", metrics.get( 4 ).ccn, 1 );
    }

    public void testCyclomaticComplexityNumber72()
    {
        Javancss javancss = measureTestFile( 72 );
        assertNotNull(javancss);
        List<FunctionMetric> metrics = javancss.getFunctionMetrics();
        assertNotNull(metrics);
        assertEquals(5, metrics.size());
        assertEquals( "CCN", metrics.get( 0 ).ccn, 4 );
        assertEquals( "CCN", metrics.get( 1 ).ccn, 5 );
        assertEquals( "CCN", metrics.get( 2 ).ccn, 4 );
        assertEquals( "CCN", metrics.get( 3 ).ccn, 4 );
        assertEquals( "CCN", metrics.get( 4 ).ccn, 2 );
    }
}
