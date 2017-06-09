package net.illandril.cableModem.status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StatusPageData {
    private final List<UpstreamData> upstreamData = new ArrayList<>();
    private final List<DownstreamData> downstreamData = new ArrayList<>();
    
    StatusPageData() {}
    
    void add( UpstreamData upstreamDatum ) {
        upstreamData.add( upstreamDatum );
    }
    
    void add( DownstreamData downstreamDatum ) {
        downstreamData.add( downstreamDatum );
    }
    
    public List<UpstreamData> getUpstreamData() {
        return Collections.unmodifiableList( upstreamData );
    }
    
    public List<DownstreamData> getDownstreamData() {
        return Collections.unmodifiableList( downstreamData );
    }
    
    public ZonedDateTime getTime() {
        if ( upstreamData.size() > 0 ) {
            return upstreamData.get( 0 ).getTime();
        } else if ( downstreamData.size() > 0 ) {
            return downstreamData.get( 0 ).getTime();
        } else {
            return null;
        }
    }
    
    public final static void initDBTable( Connection conn ) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute( "CREATE TABLE IF NOT EXISTS statusdata ( id integer PRIMARY KEY,"
                + "time datetime,"
                + "json text );" );
        
        statement = conn.createStatement();
        statement.execute( "create index if not exists sdtimeIndex on statusdata (time);" );
    }
    
    public final void write( Connection conn ) throws SQLException {
        PreparedStatement statement = conn.prepareStatement( "INSERT INTO statusdata ( "
                + "time,"
                + "json )"
                + " VALUES ( ?, ? );" );
        
        statement.setTimestamp( 1, java.sql.Timestamp.valueOf( getTime().toLocalDateTime() ) );
        statement.setString( 2, toJSON() );
        statement.executeUpdate();
    }
    
    public String toJSON() {
        StringBuilder json = new StringBuilder();
        json.append( "{\"date\":" );
        json.append( getTime().toEpochSecond() ).append( "000" /* second to ms */ );
        
        Float upowerMin = null;
        Float upowerMax = null;
        for ( UpstreamData data : upstreamData ) {
            if ( data.getPowerdBmV() != null ) {
                json.append( ",\"up" ).append( data.getChannel() ).append( "\":" ).append( data.getPowerdBmV().toString() );
                if ( upowerMin == null ) {
                    upowerMin = data.getPowerdBmV();
                    upowerMax = data.getPowerdBmV();
                } else {
                    upowerMin = Math.min( upowerMin, data.getPowerdBmV() );
                    upowerMax = Math.max( upowerMax, data.getPowerdBmV() );
                }
            }
            if ( data.getFrequencyMHz() != null ) {
                json.append( ",\"uf" ).append( data.getChannel() ).append( "\":" ).append( data.getFrequencyMHz().toString() );
            }
        }
        if ( upowerMin != null ) {
            json.append( ",\"upMin\":" ).append( upowerMin.toString() );
            json.append( ",\"upMax\":" ).append( upowerMax.toString() );
        }
        Float dpowerMin = null;
        Float dpowerMax = null;
        Float dsnrMin = null;
        Float dsnrMax = null;
        for ( DownstreamData data : downstreamData ) {
            if ( data.getPowerdBmV() != null ) {
                json.append( ",\"dp" ).append( data.getChannel() ).append( "\":" ).append( data.getPowerdBmV().toString() );
                if ( dpowerMin == null ) {
                    dpowerMin = data.getPowerdBmV();
                    dpowerMax = data.getPowerdBmV();
                } else {
                    dpowerMin = Math.min( dpowerMin, data.getPowerdBmV() );
                    dpowerMax = Math.max( dpowerMax, data.getPowerdBmV() );
                }
            }
            if ( data.getSNRdB() != null ) {
                json.append( ",\"dsnr" ).append( data.getChannel() ).append( "\":" ).append( data.getSNRdB().toString() );
                if ( dsnrMin == null ) {
                    dsnrMin = data.getSNRdB();
                    dsnrMax = data.getSNRdB();
                } else {
                    dsnrMin = Math.min( dsnrMin, data.getSNRdB() );
                    dsnrMax = Math.max( dsnrMax, data.getSNRdB() );
                }
            }
            if ( data.getFrequencyMHz() != null ) {
                json.append( ",\"df" ).append( data.getChannel() ).append( "\":" ).append( data.getFrequencyMHz().toString() );
            }
            if ( data.getCorrecteds() != null ) {
                json.append( ",\"dc" ).append( data.getChannel() ).append( "\":" ).append( data.getCorrecteds().toString() );
            }
            if ( data.getUncorrectables() != null ) {
                json.append( ",\"du" ).append( data.getChannel() ).append( "\":" ).append( data.getUncorrectables().toString() );
            }
        }
        if ( dpowerMin != null ) {
            json.append( ",\"dpMin\":" ).append( dpowerMin.toString() );
            json.append( ",\"dpMax\":" ).append( dpowerMax.toString() );
        }
        if ( dsnrMin != null ) {
            json.append( ",\"dsnrMin\":" ).append( dsnrMin.toString() );
            json.append( ",\"dsnrMax\":" ).append( dsnrMax.toString() );
        }
        json.append( "}" );
        return json.toString();
    }
}
