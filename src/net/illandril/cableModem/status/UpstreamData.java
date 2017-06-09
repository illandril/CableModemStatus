package net.illandril.cableModem.status;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;

public class UpstreamData extends Data {
    private String lockStatus = null;
    private String usChannelType = null;
    private Integer symbolRatekSyms = null;
    private String modulation = null;
    
    public UpstreamData( ZonedDateTime time, int channel ) {
        super( time, channel );
    }
    
    public final void setLockStatus( String lockStatus ) {
        this.lockStatus = lockStatus;
    }
    
    public final void setUSChannelType( String usChannelType ) {
        this.usChannelType = usChannelType;
    }
    
    public final void setSymbolRatekSyms( Integer symbolRate ) {
        this.symbolRatekSyms = symbolRate;
    }
    
    public final void setSymbolRate( String symbolRate ) {
        if ( symbolRate == null || symbolRate.replace( "-", "" ).trim().isEmpty() ) {
            setSymbolRatekSyms( (Integer)null );
        } else {
            final String symbolRateNumberOnly;
            final int multiplier;
            if ( symbolRate.endsWith( "Msym/sec" ) ) {
                symbolRateNumberOnly = symbolRate.substring( 0, symbolRate.length() - 8 ).trim();
                multiplier = 1000;
            } else if ( symbolRate.endsWith( "kSym/s" ) ) {
                symbolRateNumberOnly = symbolRate.substring( 0, symbolRate.length() - 6 ).trim();
                multiplier = 1;
            } else {
                symbolRateNumberOnly = symbolRate;
                multiplier = 1;
            }
            try {
                // kSym/s should always be an even integer
                setSymbolRatekSyms( Math.round( Float.parseFloat( symbolRateNumberOnly ) * multiplier ) );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse symbolRate[" + symbolRate + "]... expected '# kSym/s'" );
            }
        }
    }
    
    public final void setModulation( String modulation ) {
        this.modulation = modulation;
    }
    
    public final static void writeHeader( Writer out ) throws IOException {
        out.write( "up,Time,Channel,Lock Status,US Channel Type,Channel ID,Symbol Rate (kSym/s),Frequency (Hz),Power (dBmV),Modulation\n" );
    }
    
    public final static void initDBTable( Connection conn ) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute( "CREATE TABLE IF NOT EXISTS upstream ( id integer PRIMARY KEY,"
                + "time datetime,"
                + "channel integer,"
                + "lockStatus text,"
                + "usChannelType text,"
                + "channelID integer,"
                + "symbolRate integer,"
                + "frequency float,"
                + "power float,"
                + "modulation text );" );
        
        statement = conn.createStatement();
        statement.execute( "create index if not exists utimeIndex on upstream (time);" );
    }
    
    public final void write( Connection conn ) throws SQLException {
        PreparedStatement statement = conn.prepareStatement( "INSERT INTO upstream ( "
                + "time,"
                + "channel,"
                + "lockStatus,"
                + "usChannelType,"
                + "channelID,"
                + "symbolRate,"
                + "frequency,"
                + "power,"
                + "modulation )"
                + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? );" );
        
        statement.setTimestamp( 1, java.sql.Timestamp.valueOf( getTime().toLocalDateTime() ) );
        statement.setInt( 2, getChannel() );
        statement.setString( 3, lockStatus );
        statement.setString( 4, usChannelType );
        setInteger( statement, 5, getChannelID() );
        setInteger( statement, 6, symbolRatekSyms );
        setFloat( statement, 7, getFrequencyMHz() );
        setFloat( statement, 8, getPowerdBmV() );
        statement.setString( 9, modulation );
        statement.executeUpdate();
    }
    
    public final void write( Writer out ) throws IOException {
        out.write( "up," );
        out.write( getTimeString() );
        out.write( "," );
        out.write( getChannelString() );
        out.write( "," );
        if ( lockStatus != null ) {
            out.write( lockStatus );
        }
        out.write( "," );
        if ( usChannelType != null ) {
            out.write( usChannelType );
        }
        out.write( "," );
        out.write( getChannelIDString() );
        out.write( "," );
        if ( symbolRatekSyms != null ) {
            out.write( symbolRatekSyms.toString() );
        }
        out.write( "," );
        out.write( getFrequencyMHzString() );
        out.write( "," );
        out.write( getPowerdBmVString() );
        out.write( "," );
        if ( modulation != null ) {
            out.write( modulation );
        }
        out.write( "\n" );
    }
}
