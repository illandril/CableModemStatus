package net.illandril.cableModem.status;

import java.time.ZonedDateTime;

public class ArrisTG1682G extends StatusPageParser {
    // Also works for ArrisTM1602AP2
    
    @Override
    public StatusPageData parse( ZonedDateTime statusPageRequestTime, String statusPageHTML ) {
        StatusPageData pageData = new StatusPageData();
        for ( String line : statusPageHTML.split( "\n" ) ) {
            if ( line.indexOf( "Downstream 1" ) != -1 ) {
                for ( String subline : line.replace( "<tr>", "" ).split( "</tr>" ) ) {
                    if ( subline.length() == 0 ) {
                        continue;
                    }
//                  0 Downstream #
//                  1 DCID
//                  2 Freq
//                  3 Power
//                  4 SNR
//                  5 Modulation
//                  6 Octets
//                  7 Correcteds
//                  8 Uncorrectables
                    String[] cols = subline.replace( "<td>", "" ).split( "</td>" );
                    if ( cols.length != 9 ) {
                        System.err.println( "Unexpected Downstream Column Count " + cols.length );
                        System.err.println( subline );
                        System.err.println( "==" );
                        for ( String col : cols ) {
                            System.err.println( col );
                        }
                        continue;
                    }
                    DownstreamData data = new DownstreamData( statusPageRequestTime, Integer.parseInt( cols[0].replaceAll( "Downstream ", "" ).trim() ) );
                    data.setChannelID( cols[1] );
                    data.setFrequency( cols[2] );
                    data.setPower( cols[3] );
                    data.setSNR( cols[4] );
                    data.setModulation( cols[5] );
                    data.setOctets( cols[6] );
                    data.setCorrecteds( cols[7] );
                    data.setUncorrectables( cols[8] );
                    pageData.add( data );
                }
            } else if ( line.indexOf( "Upstream 1" ) != -1 || line.indexOf( "Upstream 2" ) != -1 || line.indexOf( "Upstream 3" ) != -1 || line.indexOf( "Upstream 4" ) != -1 ) {
                for ( String subline : line.split( "<tr>" ) ) {
                    if ( subline.length() == 0 ) {
                        continue;
                    }
    //              0 Upstream #
    //              1 UCID
    //              2 Freq
    //              3 Power
    //              4 Channel Type
    //              5 Symbol Rate
    //              6 Modulation
                    String[] cols = subline.replace( "<td>", "" ).split( "</td>" );
                    if ( cols.length != 7 ) {
                        System.err.println( "Unexpected Upstream Column Count " + cols.length );
                        System.err.println( subline );
                        System.err.println( "==" );
                        for ( String col : cols ) {
                            System.err.println( col );
                        }
                        continue;
                    }
                    UpstreamData data = new UpstreamData( statusPageRequestTime, Integer.parseInt( cols[0].replaceAll( "Upstream ", "" ).trim() ) );
                    data.setChannelID( cols[1] );
                    data.setFrequency( cols[2] );
                    data.setPower( cols[3] );
                    data.setUSChannelType( cols[4].trim() );
                    data.setSymbolRate( cols[5] );
                    data.setModulation( cols[6].trim() );
                    pageData.add( data );
                }
            }
        }
        return pageData;
    }
    
}
