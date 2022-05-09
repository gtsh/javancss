/*
Copyright (C) 2014 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS
(http://javancss.codehaus.org/).

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA*/

package javancss;

import junit.framework.TestCase;

import java.io.File;
import java.util.logging.Logger;

/**
 * Base JavaNCSS unit-tests class.
 *
 * @author Hervé Boutemy
 * @version $Id$
 */
public abstract class AbstractTestCase extends TestCase
{
    private File testDir = new File( "target/test-classes/" );
    Logger logger = Logger.getLogger(Javancss.class.getName());

    public File getTestDir()
    {
        return testDir;
    }

    protected File getTestFile( String filename )
    {
        return new File( testDir, filename );
    }

    protected File getTestFile( int testFileId )
    {
        return getTestFile( "Test" + testFileId + ".java" );
    }

    protected Javancss measureTestFile( int testFileId )
    {
        logger.config("Start test " + testFileId);
        return new Javancss( getTestFile( testFileId ) );
    }
}
