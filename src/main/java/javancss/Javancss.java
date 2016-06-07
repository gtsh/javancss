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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ccl.util.Exitable;
import ccl.util.Init;

import javancss.parser.JavaParser;
import javancss.parser.JavaParserDebug;
import javancss.parser.JavaParserInterface;
import javancss.parser.JavaParserTokenManager;
import javancss.parser.TokenMgrError;

/**
 * While the Java parser class might be the heart of JavaNCSS,
 * this class is the brain. This class controls input and output and
 * invokes the Java parser.
 *
 * @author    Chr. Clemens Lee <clemens@kclee.com>
 *            , recursive feature by P��k� Hannu
 *            , additional javadoc metrics by Emilio Gongora <emilio@sms.nl>
 *            , and Guillermo Rodriguez <guille@sms.nl>.
 * @version   $Id$
 */
public class Javancss
    implements Exitable
{
    private static final String S_INIT__FILE_CONTENT =
        "[Help]\n"+
        "; Please do not edit the Help section\n"+
        "HelpUsage=@srcfiles.txt | *.java | <stdin>\n" +
        "Options=ncss,package,object,function,all,gui,xml,out,recursive,encoding\n" +
        "ncss=b,o,Counts the program NCSS (default).\n" +
        "package=b,o,Assembles a statistic on package level.\n" +
        "object=b,o,Counts the object NCSS.\n" +
        "function=b,o,Counts the function NCSS.\n" +
        "all=b,o,The same as '-function -object -package'.\n" +
        "gui=b,o,Opens a gui to present the '-all' output in tabbed panels.\n" +
        "xml=b,o,Output in xml format.\n" +
        "out=s,o,Output file name. By default output goes to standard out.\n"+
        "recursive=b,o,Recurse to subdirs.\n" +
        "encoding=s,o,Encoding used while reading source files (default: platform encoding).\n" +
        "\n" +
        "[Colors]\n" +
        "UseSystemColors=true\n";

    private Logger log = Logger.getLogger( getClass().getName() );

    private static final String DEFAULT_ENCODING = null;
    
    private boolean _bExit = false;

    private List<File> _vJavaSourceFiles = null;
    private String encoding = DEFAULT_ENCODING;

    private String _sErrorMessage = null;
    private Throwable _thrwError = null;

    private JavaParserInterface _pJavaParser = null;
    private int _ncss = 0;
    private int _loc = 0;
    private List<FunctionMetric> _vFunctionMetrics = new ArrayList<FunctionMetric>();
    private List<ObjectMetric> _vObjectMetrics = new ArrayList<ObjectMetric>();
    private List<PackageMetric> _vPackageMetrics = null;
    private List<Object[]> _vImports = null;
    private Map<String,PackageMetric> _htPackages = null;
    private Object[] _aoPackage = null;

    /**
     * Just used for parseImports.
     */
    private File _sJavaSourceFile = null;

    private Reader createSourceReader( File sSourceFile_ )
    {
        try
        {
            return newReader( sSourceFile_ );
        }
        catch ( IOException pIOException )
        {
            if ( _sErrorMessage == null || _sErrorMessage.trim().length() == 0 )
            {
                _sErrorMessage = "";
            }
            else
            {
                _sErrorMessage += "\n";
            }
            _sErrorMessage += "File not found: " + sSourceFile_.getAbsolutePath();
            _thrwError = pIOException;

            return null;
        }
    }

    private void _measureSource( File sSourceFile_ )
        throws Exception, Error
    {
        Reader reader;

        // opens the file
        try
        {
            reader = newReader( sSourceFile_ );
        }
        catch ( IOException pIOException )
        {
            if ( _sErrorMessage == null || _sErrorMessage.trim().length() == 0 )
            {
                _sErrorMessage = "";
            }
            else
            {
                _sErrorMessage += "\n";
            }
            _sErrorMessage += "File not found: " + sSourceFile_.getAbsolutePath();
            _thrwError = pIOException;

            throw pIOException;
        }

        String sTempErrorMessage = _sErrorMessage;
        try
        {
            // the same method but with a Reader
            _measureSource( reader );
        }
        catch ( Exception pParseException )
        {
            if ( sTempErrorMessage == null )
            {
                sTempErrorMessage = "";
            }
            sTempErrorMessage += "ParseException in " + sSourceFile_.getAbsolutePath() +
                   "\nLast useful checkpoint: \"" + _pJavaParser.getLastFunction() + "\"\n";
            sTempErrorMessage += pParseException.getMessage() + "\n";

            _sErrorMessage = sTempErrorMessage;
            _thrwError = pParseException;

            throw pParseException;
        }
        catch ( Error pTokenMgrError )
        {
            if ( sTempErrorMessage == null )
            {
                sTempErrorMessage = "";
            }
            sTempErrorMessage += "TokenMgrError in " + sSourceFile_.getAbsolutePath() +
                   "\n" + pTokenMgrError.getMessage() + "\n";
            _sErrorMessage = sTempErrorMessage;
            _thrwError = pTokenMgrError;

            throw pTokenMgrError;
        }
    }

    private void _measureSource( Reader reader )
        throws Exception, Error
    {
        log.fine( "_measureSource(Reader).ENTER" );

        try
        {
            // create a parser object
            if ( log.isLoggable( Level.FINE ) )
            {
                log.fine( "creating JavaParserDebug" );
                _pJavaParser = new JavaParserDebug( reader );
            }
            else
            {
                log.fine( "creating JavaParser" );
                _pJavaParser = new JavaParser( reader );
            }

            // execute the parser
            _pJavaParser.parse();
            log.fine( "Javancss._measureSource(DataInputStream).SUCCESSFULLY_PARSED" );

            _ncss += _pJavaParser.getNcss(); // increment the ncss
            _loc += _pJavaParser.getLOC(); // and loc
            // add new data to global vector
            _vFunctionMetrics.addAll( _pJavaParser.getFunction() );
            _vObjectMetrics.addAll( _pJavaParser.getObject() );
            Map<String, PackageMetric> htNewPackages = _pJavaParser.getPackage();

            /* List vNewPackages = new Vector(); */
            for ( Map.Entry<String, PackageMetric> entry : htNewPackages.entrySet() )
            {
                String sPackage = entry.getKey();

                PackageMetric pckmNext = htNewPackages.get( sPackage );
                pckmNext.name = sPackage;

                PackageMetric pckmPrevious = _htPackages.get( sPackage );
                pckmNext.add( pckmPrevious );

                _htPackages.put( sPackage, pckmNext );
            }
        }
        catch ( Exception pParseException )
        {
            if ( _sErrorMessage == null )
            {
                _sErrorMessage = "";
            }
            _sErrorMessage += "ParseException in STDIN";
            if ( _pJavaParser != null )
            {
                _sErrorMessage += "\nLast useful checkpoint: \"" + _pJavaParser.getLastFunction() + "\"\n";
            }
            _sErrorMessage += pParseException.getMessage() + "\n";
            _thrwError = pParseException;

            throw pParseException;
        }
        catch ( Error pTokenMgrError )
        {
            if ( _sErrorMessage == null )
            {
                _sErrorMessage = "";
            }
            _sErrorMessage += "TokenMgrError in STDIN\n";
            _sErrorMessage += pTokenMgrError.getMessage() + "\n";
            _thrwError = pTokenMgrError;

            throw pTokenMgrError;
        }
    }

    private void _measureFiles( List<File> vJavaSourceFiles_ )
        throws TokenMgrError
    {
        for ( File file : vJavaSourceFiles_ )
        {
            try
            {
                _measureSource( file );
            }
            catch ( Throwable pThrowable )
            {
                // hmm, do nothing? Use getLastError() or so to check for details.
                // error details have been written into lastError
            }
        }
    }

    /**
     * If arguments were provided, they are used, otherwise
     * the input stream is used.
     */
    private void _measureRoot( Reader reader )
        throws Exception, Error
    {
        _htPackages = new HashMap<String, PackageMetric>();

        // either there are argument files, or stdin is used
        if ( _vJavaSourceFiles == null )
        {
            _measureSource( reader );
        }
        else
        {
            // the collection of files get measured
            _measureFiles( _vJavaSourceFiles );
        }

        _vPackageMetrics = new ArrayList<PackageMetric>();
        for ( PackageMetric pkm : _htPackages.values() )
        {
            _vPackageMetrics.add( pkm );
        }
        Collections.sort( _vPackageMetrics );
    }

    public List<Object[]> getImports()
    {
        return _vImports;
    }

    /**
     * Return info about package statement.
     * First element has name of package,
     * then begin of line, etc.
     */
    public Object[] getPackage()
    {
        return _aoPackage;
    }

    /**
     * The same as getFunctionMetrics?!
     */
    public List<FunctionMetric> getFunctions()
    {
        return _vFunctionMetrics;
    }

    public void printObjectNcss( Writer w )
        throws IOException
    {
        getFormatter().printObjectNcss( w );
    }

    public void printFunctionNcss( Writer w )
        throws IOException
    {
        getFormatter().printFunctionNcss( w );
    }

    public void printPackageNcss( Writer w )
        throws IOException
    {
        getFormatter().printPackageNcss( w );
    }

    public void printJavaNcss( Writer w )
        throws IOException
    {
        getFormatter().printJavaNcss( w );
    }

    public void printStart( Writer pw )
        throws IOException
    {
        getFormatter().printStart( pw );
    }

    public void printEnd( Writer pw )
        throws IOException
    {
        getFormatter().printEnd( pw );
    }
    
    public Javancss( List<File> vJavaSourceFiles_ )
    {
        this( vJavaSourceFiles_, DEFAULT_ENCODING );
    }

    public Javancss( List<File> vJavaSourceFiles_, String encoding_ )
    {
        setEncoding( encoding_ );
        _vJavaSourceFiles = vJavaSourceFiles_;
        _measureRoot();
    }

    private void _measureRoot()
        throws Error
    {
        try
        {
            _measureRoot( newReader( System.in ) );
        }
        catch ( Throwable pThrowable )
        {
            log.fine( "Javancss._measureRoot().e: " + pThrowable );
            pThrowable.printStackTrace(System.err);
        }
    }

    public Javancss( File sJavaSourceFile_ )
    {
        this( sJavaSourceFile_, DEFAULT_ENCODING );
    }

    public Javancss( File sJavaSourceFile_, String encoding_ )
    {
        log.fine( "Javancss.<init>(String).sJavaSourceFile_: " + sJavaSourceFile_ );
        setEncoding( encoding_ );
        _sErrorMessage = null;
        _vJavaSourceFiles = new ArrayList<File>();
        _vJavaSourceFiles.add( sJavaSourceFile_ );
        _measureRoot();
    }

    /**
     * Only way to create object that does not immediately
     * start to parse.
     */
    public Javancss()
    {
        _sErrorMessage = null;
        _thrwError = null;
    }

    public boolean parseImports()
    {
        if ( _sJavaSourceFile == null )
        {
            log.fine( "Javancss.parseImports().NO_FILE" );

            return true;
        }
        Reader reader = createSourceReader( _sJavaSourceFile );
        if ( reader == null )
        {
            log.fine( "Javancss.parseImports().NO_DIS" );

            return true;
        }

        try
        {
            log.fine( "Javancss.parseImports().START_PARSING" );
            if ( !log.isLoggable( Level.FINE ) )
            {
                _pJavaParser = new JavaParser( reader );
            }
            else
            {
                _pJavaParser = new JavaParserDebug( reader );
            }
            _pJavaParser.parseImportUnit();
            _vImports = _pJavaParser.getImports();
            _aoPackage = _pJavaParser.getPackageObjects();
            log.fine( "Javancss.parseImports().END_PARSING" );
        }
        catch ( Exception pParseException )
        {
            log.fine( "Javancss.parseImports().PARSE_EXCEPTION" );
            if ( _sErrorMessage == null )
            {
                _sErrorMessage = "";
            }
            _sErrorMessage += "ParseException in STDIN";
            if ( _pJavaParser != null )
            {
                _sErrorMessage += "\nLast useful checkpoint: \"" + _pJavaParser.getLastFunction() + "\"\n";
            }
            _sErrorMessage += pParseException.getMessage() + "\n";
            _thrwError = pParseException;

            return true;
        }
        catch ( Error pTokenMgrError )
        {
            log.fine( "Javancss.parseImports().TOKEN_ERROR" );
            if ( _sErrorMessage == null )
            {
                _sErrorMessage = "";
            }
            _sErrorMessage += "TokenMgrError in STDIN\n";
            _sErrorMessage += pTokenMgrError.getMessage() + "\n";
            _thrwError = pTokenMgrError;

            return true;
        }

        return false;
    }

    public void setSourceFile( File javaSourceFile_ )
    {
        _sJavaSourceFile = javaSourceFile_;
        _vJavaSourceFiles = new ArrayList<File>();
        _vJavaSourceFiles.add( javaSourceFile_ );
    }

    public Javancss( Reader reader )
    {
        this( reader, DEFAULT_ENCODING );
    }

    public Javancss( Reader reader, String encoding_ )
    {
        setEncoding( encoding_ );
        try
        {
            _measureRoot( reader );
        }
        catch ( Throwable pThrowable )
        {
            log.fine( "Javancss.<init>(Reader).e: " + pThrowable );
            pThrowable.printStackTrace(System.err);
        }
    }

    /**
     * recursively adds *.java files
     * @param dir the base directory to search
     * @param v the list of file to add found files to
     */
    private static void _addJavaFiles( File dir, List<File> v )
    {
        File[] files = dir.listFiles();
        if ( files == null || files.length == 0 )
        {
            return;
        }

        for ( File file : files )
        {
            if ( file.isDirectory() )
            {
                // Recurse!!!
                _addJavaFiles( file, v );
            }
            else
            {
                if ( file.getName().endsWith( ".java" ) )
                {
                    v.add( file );
                }
            }
        }
    }

    private List<File> findFiles( List<String> filenames, boolean recursive )
        throws IOException
    {
        if ( log.isLoggable( Level.FINE ) )
        {
            log.fine( "filenames: " + filenames );
        }
        if ( filenames.size() == 0 )
        {
            if ( recursive )
            {
                // If no files then add current directory!
                filenames.add( "." );
            }
            else
            {
                return null;
            }
        }

        Set<String> _processedAtFiles = new HashSet<String>();
        List<File> newFiles = new ArrayList<File>();
        for ( String filename : filenames )
        {
            // if the file specifies other files...
            if ( filename.startsWith( "@" ) )
            {
                filename = filename.substring( 1 );
                if ( filename.length() > 1 )
                {
                    filename = normalizeFileName( filename );
                    if ( _processedAtFiles.add( filename ) )
                    {
                        String sJavaSourceFileNames = null;
                        try
                        {
                            sJavaSourceFileNames = readFile( filename );
                        }
                        catch( IOException pIOException )
                        {
                            _sErrorMessage = "File Read Error: " + filename;
                            _thrwError = pIOException;
                            throw pIOException;
                        }
                        String[] vTheseJavaSourceFiles = sJavaSourceFileNames.split( "\n" );
                        for ( String name : vTheseJavaSourceFiles )
                        {
                            newFiles.add( new File( name ) );
                        }
                    }
                }
            }
            else
            {
                filename = normalizeFileName( filename );
                File file = new File( filename );
                if ( file.isDirectory() )
                {
                    _addJavaFiles( file, newFiles );
                }
                else
                {
                    newFiles.add( file );
                }
            }
        }

        if ( log.isLoggable( Level.FINE ) )
        {
            log.fine( "resolved filenames: " + newFiles );
        }

        return newFiles;
    }

    /**
     * @deprecated use Javancss(String[]) instead, since the sRcsHeader_ parameter is not useful
     */
    @Deprecated
    public Javancss( String[] asArgs_, String sRcsHeader_ )
        throws IOException
    {
        this( asArgs_ );
    }

    /**
     * This is the constructor used in the main routine in
     * javancss.Main.
     * Other constructors might be helpful to use Javancss out
     * of other programs.
     */
    public Javancss( String[] asArgs_ )
        throws IOException
    {
        Init _pInit = new Init( this, asArgs_, "$Header: /javancss/Main.java,v 0.0 2001/01/01 00:00:00 clemens Exp clemens $", S_INIT__FILE_CONTENT );
        if ( _bExit )
        {
            return;
        }
        Map<String, String> htOptions = _pInit.getOptions();

        setEncoding( htOptions.get( "encoding" ) );
        setXML( htOptions.get( "xml" ) != null );

        // the arguments (the files) to be processed
        _vJavaSourceFiles = findFiles( _pInit.getArguments(), htOptions.get( "recursive" ) != null );

        if ( htOptions.get( "gui" ) != null )
        {
            final JavancssFrame pJavancssFrame = new JavancssFrame( _pInit );
            /* final Thread pThread = Thread.currentThread(); */
            pJavancssFrame.addWindowListener( new WindowAdapter()
            {
                @Override
                public void windowClosing( WindowEvent e_ )
                {
                    log.fine( "JavancssAll.run().WindowAdapter.windowClosing().1" );
                    pJavancssFrame.setVisible( false );
                    pJavancssFrame.dispose();
                }
            } );
            pJavancssFrame.setVisible( true );

            try
            {
                _measureRoot( newReader( System.in ) );
            }
            catch ( Throwable pThrowable )
            {
                // shouldn't we print something here?
                // error details have been written into lastError
            }

            pJavancssFrame.showJavancss( this );
            pJavancssFrame.setSelectedTab( JavancssFrame.S_PACKAGES );

            return;
        }

        // this initiates the measurement
        try
        {
            _measureRoot( newReader( System.in ) );
        }
        catch ( Throwable pThrowable )
        {
            log.fine( "Javancss.<init>(String[]).e: " + pThrowable );
            pThrowable.printStackTrace(System.err);
        }
        if ( getLastErrorMessage() != null )
        {
            System.err.println( getLastErrorMessage() + "\n" );
            if ( getNcss() <= 0 )
            {
                return;
            }
        }

        String sOutputFile = htOptions.get( "out" );
        OutputStream out = System.out;
        if ( sOutputFile != null )
        {
            try
            {
                out = new FileOutputStream( normalizeFileName( sOutputFile ) );
            }
            catch ( Exception exception )
            {
                System.err.println( "Error opening output file '" + sOutputFile + "': " + exception.getMessage() );
                sOutputFile = null;
            }
        }
        // TODO: encoding configuration support for result output
        final PrintWriter pw = useXML() ? new PrintWriter( new OutputStreamWriter( out, "UTF-8" ) ) : new PrintWriter( out );
        try {

            format( pw, htOptions );

        } finally {
            if ( sOutputFile != null )
            {
                pw.close();
            }
            else
            {
                // stdout is used: don't close but ensure everything is flushed
                pw.flush();
            }
        }
    }

    private void format( PrintWriter pw, Map<String, String> htOptions )
        throws IOException
    {
        printStart( pw );
   
        boolean bNoNCSS = false;
        if ( htOptions.get( "package" ) != null || htOptions.get( "all" ) != null )
        {
            printPackageNcss( pw );
            bNoNCSS = true;
        }
        if ( htOptions.get( "object" ) != null || htOptions.get( "all" ) != null )
        {
            if ( bNoNCSS )
            {
                pw.println();
            }
            printObjectNcss( pw );
            bNoNCSS = true;
        }
        if ( htOptions.get( "function" ) != null || htOptions.get( "all" ) != null )
        {
            if ( bNoNCSS )
            {
                pw.println();
            }
            printFunctionNcss( pw );
            bNoNCSS = true;
        }
        if ( !bNoNCSS )
        {
            printJavaNcss( pw );
        }
   
        printEnd( pw );
    }

    public int getNcss()
    {
        return _ncss;
    }

    public int getLOC()
    {
        return _loc;
    }

    public int getJvdc()
    {
        return _pJavaParser.getJvdc();
    }

    /**
     * JDCL stands for javadoc comment lines (while jvdc stands
     * for number of javadoc comments).
     */
    public int getJdcl()
    {
        return JavaParserTokenManager._iFormalComments;
    }

    public int getSl()
    {
        return JavaParserTokenManager._iSingleComments;
    }

    public int getMl()
    {
        return JavaParserTokenManager._iMultiComments;
    }

    public List<FunctionMetric> getFunctionMetrics()
    {
        return _vFunctionMetrics;
    }

    public List<ObjectMetric> getObjectMetrics()
    {
        return _vObjectMetrics;
    }

    /**
     * Returns list of packages in the form
     * PackageMetric objects.
     */
    public List<PackageMetric> getPackageMetrics()
    {
        return _vPackageMetrics;
    }

    public String getLastErrorMessage()
    {
        return _sErrorMessage;
    }

    public Throwable getLastError()
    {
        return _thrwError;
    }

    public void setExit()
    {
        _bExit = true;
    }

    private boolean _bXML = false;

    public void setXML( boolean bXML )
    {
        _bXML = bXML;
    }

    public boolean useXML()
    {
        return _bXML;
    }

    public Formatter getFormatter()
    {
        if ( useXML() )
        {
            return new XmlFormatter( this );
        }

        return new AsciiFormatter( this );
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    private Reader newReader( InputStream stream )
        throws UnsupportedEncodingException
    {
        return ( encoding == null ) ? new InputStreamReader( stream ) : new InputStreamReader( stream, encoding );
    }

    private Reader newReader( File file )
        throws FileNotFoundException, UnsupportedEncodingException
    {
        return newReader( new FileInputStream( file ) );
    }

    private String normalizeFileName( String filename )
    {
        String userdir = ( String ) System.getProperties().get( "user.dir" );

        filename = filename.trim();
        if ( filename.length() == 0 || filename.equals( "." ) )
        {
            filename = userdir;
        }
        else if ( !new File( filename ).isAbsolute() )
        {
            filename = new File( userdir, filename ).getPath();
        }

        try
        {
            return new File( filename ).getCanonicalPath();
        }
        catch ( IOException e )
        {
            return filename;
        }
    }

    private String readFile( String filename ) throws IOException
    {
        StringBuilder content = new StringBuilder( 100000 );

        BufferedReader in = null;
        try
        {
            in = new BufferedReader( new FileReader( filename ) );
            String line;
            while ( ( line = in.readLine() ) != null )
            {
                content.append( line ).append( '\n' );
            }
        }
        finally
        {
            if ( in != null )
            {
                in.close();
            }
        }

        return content.toString();
    }
}
