package net.illandril.cableModem.status;

import java.time.LocalDateTime;

public class ArrisSB6183 extends StatusPageParser {
    @Override
    public StatusPageData parse( LocalDateTime statusPageRequestTime, String statusPageHTML ) {
        StatusPageData pageData = new StatusPageData();
        boolean inDownstream = false;
        boolean inUpstream = false;
        for ( String line : statusPageHTML.split( "\n" ) ) {
            if ( line.indexOf( "Downstream Bonded Channels" ) != -1 ) {
                inDownstream = true;
                inUpstream = false;
            } else if ( line.indexOf( "Upstream Bonded Channels" ) != -1 ) {
                inDownstream = false;
                inUpstream = true;
            } else if ( inDownstream || inUpstream ){
                if ( line.indexOf( "</table>" ) != -1 ) {
                    inDownstream = false;
                    inUpstream = false;
                } else if ( line.indexOf( "<tr><td>" ) != -1 ) {
                    String[] cols = line.replace( "<tr>", "" ).replace( "</tr>", "").replace( "</td><td>", "," ).replace("<td>","").replace( "</td>", "" ).trim().split(",");
                    if ( inUpstream ) {
                        // 0 Lock Status
                        // 1 US Channel Type
                        // 2 Channel ID
                        // 3 Symbol Rate
                        // 4 Frequency
                        // 5 Power
                        UpstreamData data = new UpstreamData( statusPageRequestTime, Integer.parseInt( cols[0].replaceAll( "Downstream ", "" ).trim() ) );
                        data.setLockStatus( cols[0].trim() );
                        data.setUSChannelType( cols[1].trim() );
                        data.setChannelID( cols[2] );
                        data.setSymbolRate( cols[3] );
                        data.setFrequency( cols[4] );
                        data.setPower( cols[5] );
                        pageData.add( data );
                    } else {
                        // 0 Lock Status
                        // 1 Modulation
                        // 2 Channel ID
                        // 3 Frequency
                        // 4 Power
                        // 5 SNR
                        // 6 Correcteds
                        // 7 Uncorrectables
                        DownstreamData data = new DownstreamData( statusPageRequestTime, Integer.parseInt( cols[0].replaceAll( "Downstream ", "" ).trim() ) );
                        data.setLockStatus( cols[0].trim() );
                        data.setModulation( cols[1].trim() );
                        data.setChannelID( cols[2] );
                        data.setFrequency( cols[3] );
                        data.setPower( cols[4] );
                        data.setSNR( cols[5] );
                        data.setCorrecteds( cols[6] );
                        data.setUncorrectables( cols[7] );
                        pageData.add( data );
                    }
                }
            }
        }
        return pageData;
    }
    
}
