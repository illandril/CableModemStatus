package net.illandril.cableModem.status;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;

public class DownstreamData extends Data {
    private String lockStatus = null;
    private Float snrdB = null;
    private String modulation = null;
    private Long correcteds = null;
    private Long uncorrectables = null;
    private Long octets = null;
    
    public DownstreamData( ZonedDateTime time, int channel ) {
        super( time, channel );
    }
    
    public final void setLockStatus( String lockStatus ) {
        this.lockStatus = lockStatus;
    }
    
    public final void setCorrecteds( Long correcteds ) {
        this.correcteds = correcteds;
    }
    
    public final void setCorrecteds( String correcteds ) {
        if ( correcteds == null || correcteds.replace( "-", "" ).trim().isEmpty() ) {
            setCorrecteds( (Long)null );
        } else {
            try {
                setCorrecteds( Long.parseLong( correcteds ) );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse correcteds[" + correcteds + "]" );
            }
        }
    }
    
    public final Long getCorrecteds() {
        return correcteds;
    }
    
    public final void setUncorrectables( Long uncorrectables ) {
        this.uncorrectables = uncorrectables;
    }
    
    public final void setUncorrectables( String uncorrectables ) {
        if ( uncorrectables == null || uncorrectables.replace( "-", "" ).trim().isEmpty() ) {
            setUncorrectables( (Long)null );
        } else {
            try {
                setUncorrectables( Long.parseLong( uncorrectables ) );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse uncorrectables[" + uncorrectables + "]" );
            }
        }
    }
    
    public final Long getUncorrectables() {
        return uncorrectables;
    }
    
    public final void setOctets( Long octets ) {
        this.octets = octets;
    }
    
    public final void setOctets( String octets ) {
        if ( octets == null || octets.replace( "-", "" ).trim().isEmpty() ) {
            setOctets( (Long)null );
        } else {
            try {
                setOctets( Long.parseLong( octets ) );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse octets[" + octets + "]" );
            }
        }
    }
    
    public final void setSNRdB( Float snr ) {
        this.snrdB = snr;
    }
    
    public final void setSNR( String snr ) {
        if ( snr == null || snr.replace( "-", "" ).trim().isEmpty() ) {
            setSNRdB( (Float)null );
        } else {
            final String snrNumberOnly;
            if ( snr.endsWith( "dB" ) ) {
                snrNumberOnly = snr.substring( 0, snr.length() - 2 ).trim();
            } else {
                snrNumberOnly = snr;
            }
            try {
                setSNRdB( Float.parseFloat( snrNumberOnly ) );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse snr[" + snr + "]... expected '# dB'" );
            }
        }
    }
    
    public final Float getSNRdB() {
        return snrdB;
    }
    
    public final void setModulation( String modulation ) {
        this.modulation = modulation;
    }
    
    public final static void writeHeader( Writer out ) throws IOException {
        out.write( "down,Time,Channel,Lock Status,Modulation,Channel ID,Frequency (Hz),Power (dBmV),SNR (dB),Correcteds,Uncorrectables,Octets\n" );
    }

    public final static void initDBTable( Connection conn ) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute( "CREATE TABLE IF NOT EXISTS downstream ( id integer PRIMARY KEY,"
                + "time datetime,"
                + "channel integer,"
                + "lockStatus text,"
                + "modulation text,"
                + "channelID integer,"
                + "frequency float,"
                + "power float,"
                + "snr float,"
                + "correcteds bigint,"
                + "uncorrectables bigint,"
                + "octets bigint );" );
        
        statement = conn.createStatement();
        statement.execute( "create index if not exists dtimeIndex on downstream (time);" );
    }
    
    public final void write( Connection conn ) throws SQLException {
        PreparedStatement statement = conn.prepareStatement( "INSERT INTO downstream ( "
                + "time,"
                + "channel,"
                + "lockStatus,"
                + "modulation,"
                + "channelID,"
                + "frequency,"
                + "power,"
                + "snr,"
                + "correcteds,"
                + "uncorrectables,"
                + "octets )"
                + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );" );
        
        statement.setTimestamp( 1, java.sql.Timestamp.valueOf( getTime().toLocalDateTime() ) );
        statement.setInt( 2, getChannel() );
        statement.setString( 3, lockStatus );
        statement.setString( 4, modulation );
        setInteger( statement, 5, getChannelID() );
        setFloat( statement, 6, getFrequencyMHz() );
        setFloat( statement, 7, getPowerdBmV() );
        setFloat( statement, 8, snrdB );
        setLong( statement, 9, correcteds );
        setLong( statement, 10, uncorrectables );
        setLong( statement, 11, octets );
        statement.executeUpdate();
    }
    
    public final void write( Writer out ) throws IOException {
        out.write( "down," );
        out.write( getTimeString() );
        out.write( "," );
        out.write( getChannelString() );
        out.write( "," );
        if ( lockStatus != null ) {
            out.write( lockStatus );
        }
        out.write( "," );
        if ( modulation != null ) {
            out.write( modulation );
        }
        out.write( "," );
        out.write( getChannelIDString() );
        out.write( "," );
        out.write( getFrequencyMHzString() );
        out.write( "," );
        out.write( getPowerdBmVString() );
        out.write( "," );
        if ( snrdB != null ) {
            out.write( snrdB.toString() );
        }
        out.write( "," );
        if ( correcteds != null ) {
            out.write( correcteds.toString() );
        }
        out.write( "," );
        if ( uncorrectables != null ) {
            out.write( uncorrectables.toString() );
        }
        out.write( "," );
        if ( octets != null ) {
            out.write( octets.toString() );
        }
        out.write( "\n" );
    }
}
