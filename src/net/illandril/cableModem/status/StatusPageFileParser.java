package net.illandril.cableModem.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;

public class StatusPageFileParser {
    private final static ZonedDateTime filenameToTime( File file ) {
        String fileDateStr = file.getName().substring( 0, file.getName().indexOf( ".html" ) );
        String[] fileDateParts = fileDateStr.split( "\\." );
        if ( fileDateParts.length == 3 ) {
            fileDateStr = file.getParentFile().getName() + "." + fileDateStr;
            fileDateParts = fileDateStr.split( "[\\.-]" );
        }
        return ZonedDateTime.of( Integer.parseInt( fileDateParts[0] ),
                                 Integer.parseInt( fileDateParts[1] ),
                                 Integer.parseInt( fileDateParts[2] ),
                                 Integer.parseInt( fileDateParts[3] ),
                                 Integer.parseInt( fileDateParts[4] ),
                                 Integer.parseInt( fileDateParts[5] ),
                                 0 /* nano */, ZoneOffset.systemDefault() );
    }
    
    /**
     * Converts a directory full of HTML files, named "yyyy.MM.dd.HH.mm.ss.html", into a SQLite database of modem status data
     * @param directory
     * @param outputCSV
     */
    public final static void convertDirectoryToSQLite( File directory, File outputDB ) {
        String url = "jdbc:sqlite:" + outputDB.getAbsolutePath();
        try ( Connection conn = DriverManager.getConnection( url ) ) {
            StatusPageData.initDBTable( conn );
//            DownstreamData.initDBTable( conn );
//            UpstreamData.initDBTable( conn );
            conn.setAutoCommit( false );
            File[] files = directory.listFiles();
            Arrays.sort( files, new Comparator<File>(){
                @Override
                public int compare( File o1, File o2 ) {
                    return o1.getName().compareTo( o2.getName() );
                }
                
            });
            for ( File file : files ) {
                if ( file.getName().endsWith( ".html" ) ) {
                    appendFileToSQLite( file, conn );
                }
            }
            conn.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println( "Done with " + directory.getName() );
    }
    
    /**
     * Converts a directory full of HTML files, named "yyyy.MM.dd.HH.mm.ss.html", into a SQLite database of modem status data
     * @param directory
     * @param outputCSV
     */
    public final static void appendFileToSQLite( File file, File outputDB ) {
        String url = "jdbc:sqlite:" + outputDB.getAbsolutePath();
        try ( Connection conn = DriverManager.getConnection( url ) ) {
            StatusPageData.initDBTable( conn );
//            DownstreamData.initDBTable( conn );
//            UpstreamData.initDBTable( conn );
            conn.setAutoCommit( false );
            appendFileToSQLite( file, conn );
            conn.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println( "Done with " + file.getName() );
    }
    
    private final static void appendFileToSQLite( File file, Connection conn ) throws IOException, SQLException {
        ZonedDateTime fileDate = filenameToTime( file );
        StringBuilder fileHTML = new StringBuilder();
        BufferedReader in = new BufferedReader( new FileReader( file ) );
        while( in.ready() ) {
            fileHTML.append( in.readLine() );
            fileHTML.append( "\n" );
        }
        in.close();
        
        String statusPageHTML = fileHTML.toString().trim();
        if ( statusPageHTML.isEmpty() ) {
            System.err.println( "Skipping " + file.getName() + " - empty HTML (probably means the modem was resetting, or not connected)" );
        } else {
            StatusPageParser parser = StatusPageParserFactory.get( statusPageHTML );
            if ( parser == null ) {
                System.err.println( "Skipping " + file.getName() + " (couldn't tell what modem it was from... could be a bad GET request, or could be from a modem not yet supported)" );
            } else {
                StatusPageData data = parser.parse( fileDate, statusPageHTML );
                data.write( conn );
//                    for ( UpstreamData upData : data.getUpstreamData() ) {
//                        upData.write( conn );
//                    }
//                    for ( DownstreamData downData : data.getDownstreamData() ) {
//                        downData.write( conn );
//                    }
            }
        }
    }
    
    /**
     * Converts a directory full of HTML files, named "yyyy.MM.dd.HH.mm.ss.html", into a CSV of modem status data
     */
    public final static void convertDirectoryToCSV( File directory, File outputCSV ) {
        try {
            outputCSV.delete();
            outputCSV.createNewFile();
            FileWriter out = new FileWriter( outputCSV );
            UpstreamData.writeHeader( out );
            DownstreamData.writeHeader( out );
            File[] files = directory.listFiles();
            Arrays.sort( files, new Comparator<File>(){
                @Override
                public int compare( File o1, File o2 ) {
                    return o1.getName().compareTo( o2.getName() );
                }
                
            });
            for ( File file : files ) {
                if ( file.getName().endsWith( ".html" ) ) {
                    ZonedDateTime fileDate = filenameToTime( file );
                    StringBuilder fileHTML = new StringBuilder();
                    BufferedReader in = new BufferedReader( new FileReader( file ) );
                    while( in.ready() ) {
                        fileHTML.append( in.readLine() );
                        fileHTML.append( "\n" );
                    }
                    in.close();
                    
                    String statusPageHTML = fileHTML.toString().trim();
                    if ( statusPageHTML.isEmpty() ) {
                        System.err.println( "Skipping " + file.getName() + " - empty HTML (probably means the modem was resetting, or not connected)" );
                    } else {
                        StatusPageParser parser = StatusPageParserFactory.get( statusPageHTML );
                        if ( parser == null ) {
                            System.err.println( "Skipping " + file.getName() + " (couldn't tell what modem it was from... could be a bad GET request, or could be from a modem not yet supported)" );
                        } else {
                            StatusPageData data = parser.parse( fileDate, statusPageHTML );
                            for ( UpstreamData upData : data.getUpstreamData() ) {
                                upData.write( out );
                            }
                            for ( DownstreamData downData : data.getDownstreamData() ) {
                                downData.write( out );
                            }
                        }
                    }
                }
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println( "Done with " + directory.getName() );
    }
}
