package net.illandril.cableModem.status;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

abstract class Data {
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern( "yyyy.MM.dd.HH.mm.ss" );
    
    private final ZonedDateTime time;
    private final int channel;
    private Integer channelID = null;
    private Float frequencyMHz = null;
    private Float powerdBmV = null;
    
    Data( ZonedDateTime time, int channel ) {
        this.time = time;
        this.channel = channel;
    }
    
    public final ZonedDateTime getTime() {
        return time;
    }
    
    public final String getTimeString() {
        return time.format( TIME_FORMATTER );
    }
    
    public final String getChannelString() {
        return Integer.toString( channel );
    }
    
    public final int getChannel() {
        return channel;
    }
    
    public final void setChannelID( Integer channelID ) {
        this.channelID = channelID;
    }
    
    public final void setChannelID( String channelID ) {
        if ( channelID == null || channelID.replace( "-", "" ).trim().isEmpty() ) {
            setChannelID( (Integer)null );
        } else {
            try {
                setChannelID( Integer.parseInt( channelID ) );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse channelID[" + channelID + "]" );
            }
        }
    }
    
    public final Integer getChannelID() {
        return channelID;
    }
    public final String getChannelIDString() {
        return channelID == null ? "" : channelID.toString();
    }
    
    public final void setFrequencyMHz( Float frequency ) {
        this.frequencyMHz = frequency;
    }
    
    public final void setFrequency( String frequency ) {
        if ( frequency == null || frequency.replace( "-", "" ).trim().isEmpty() ) {
            setFrequencyMHz( (Float)null );
        } else {
            final String frequencyNumberOnly;
            final int multiplier;
            if ( frequency.endsWith( "MHz" ) ) {
                frequencyNumberOnly = frequency.substring( 0, frequency.length() - 3 ).trim();
                multiplier = 1;
            } else if ( frequency.endsWith( "KHz" ) ) {
                frequencyNumberOnly = frequency.substring( 0, frequency.length() - 3 ).trim();
                multiplier = 1000;
            } else if ( frequency.endsWith( "Hz" ) ) {
                frequencyNumberOnly = frequency.substring( 0, frequency.length() - 2 ).trim();
                multiplier = 1000000;
            } else {
                frequencyNumberOnly = frequency;
                multiplier = 1;
            }
            try {
                setFrequencyMHz( Float.parseFloat( frequencyNumberOnly ) / multiplier );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse frequency[" + frequency + "]... expected '# MHz', '# KHz', or '# Hz'" );
            }
        }
    }
    
    public final String getFrequencyMHzString() {
        return frequencyMHz == null ? "" : frequencyMHz.toString();
    }
    
    public final Float getFrequencyMHz() {
        return frequencyMHz;
    }
    
    public final void setPowerdBmV( Float power ) {
        this.powerdBmV = power;
    }
    
    public final Float getPowerdBmV() {
        return powerdBmV;
    }
    
    public final void setPower( String power ) {
        if ( power == null || power.replace( "-", "" ).trim().isEmpty() ) {
            setPowerdBmV( (Float)null );
        } else {
            final String powerNumberOnly;
            if ( power.endsWith( "dBmV" ) ) {
                powerNumberOnly = power.substring( 0, power.length() - 4 ).trim();
            } else {
                powerNumberOnly = power;
            }
            try {
                setPowerdBmV( Float.parseFloat( powerNumberOnly ) );
            } catch( NumberFormatException nfe ) {
                System.err.println( "Couldn't parse power[" + power + "]... expected '# dBmV'" );
            }
        }
    }
    
    public final String getPowerdBmVString() {
        return powerdBmV == null ? "" : powerdBmV.toString();
    }
    
    protected final static void setInteger( PreparedStatement stmt, int index, Integer value ) throws SQLException {
        if ( value == null ) {
            stmt.setNull( index, java.sql.Types.INTEGER );
        } else {
            stmt.setInt( index, value.intValue() );
        }
    }
    
    protected final static void setFloat( PreparedStatement stmt, int index, Float value ) throws SQLException {
        if ( value == null ) {
            stmt.setNull( index, java.sql.Types.FLOAT );
        } else {
            stmt.setFloat( index, value.floatValue() );
        }
    }
    
    protected final static void setLong( PreparedStatement stmt, int index, Long value ) throws SQLException {
        if ( value == null ) {
            stmt.setNull( index, java.sql.Types.BIGINT );
        } else {
            stmt.setLong( index, value.longValue() );
        }
    }
}
