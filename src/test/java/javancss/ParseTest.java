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

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.*;
import java.util.function.*;
import org.junit.Test;

public class ParseTest extends AbstractTestCase
{
    public  @Test void  testParse()
    {
        checkParse( 31 ); // java.net.Socket, why?
        checkParse( 33 ); // java.text.Decimalformat, why?
        checkParse( 34 ); // java.text.TextBoundaryData, why?
        
        checkParse( 48 );

        checkParse( 50 );

        checkParse( 71 ); // class declared in method
        checkParse( 133 ); // char.class
        checkParse( 135 ); // annotation 

        checkParse( 142 ); // JAVANCSS-12
        checkParse( 143 ); // JAVANCSS-9
        checkParse( 144 ); // JAVANCSS-13
        checkParse( 145 ); // JAVANCSS-14
        checkParse( 146 ); // JAVANCSS-17
        checkParse( 147 ); // anonymous subcluss 
        checkParse( 148 ); // JAVANCSS-49
        checkParse( 149 ); // JAVANCSS-46
        checkParse( 150 ); // JAVANCSS-53 
        checkParse( 151 ); // JAVANCSS-45 
        checkParse( 152 ); // JAVANCSS-57
        checkParse( 153 ); // JAVANCSS-54
        checkParse( 154 ); // JAVANCSS-52
        checkParse( 155 ); // JAVANCSS-28
        checkParse( 156 ); // hexadecimal floating-point literals
        checkParse( 157 ); // Java 7 literals
        checkParse( 158 ); // JAVANCSS-48
        checkParse( 159 ); // default interface methods
        checkParse( 160 ); // method references
        checkParse( 161 ); // lambda expressions
    }

    void checkParse( int testFile )
    {
        Javancss pJavancss = measureTestFile( testFile );
        assertFalse( "Parsing file Test" + testFile + 
                ".java failed!; Message: " + pJavancss.getLastErrorMessage(),
                pJavancss.getNcss() <= 0 );
    }
    
}