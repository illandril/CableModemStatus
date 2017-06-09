package net.illandril.cableModem.status;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrisSB6141 extends StatusPageParser {
    
    @Override
    public StatusPageData parse( ZonedDateTime statusPageRequestTime, String statusPageHTML ) {
        StatusPageData pageData = new StatusPageData();
        boolean inDownstream = false;
        boolean inUpstream = false;
        StringBuilder upstreamTable = new StringBuilder();
        StringBuilder downstreamTable = new StringBuilder();
        for ( String line : statusPageHTML.split( "\n" ) ) {
            if ( line.indexOf( "<FONT color=#ffffff>Downstream" ) != -1 ) {
                inDownstream = true;
                inUpstream = false;
            } else if ( line.indexOf( "<FONT color=#ffffff>Upstream" ) != -1 ) {
                inDownstream = false;
                inUpstream = true;
            } else if ( inDownstream || inUpstream ){
                if ( line.indexOf( "Bonding Channel Value" ) != -1 ) {
                    // ignore the line
                } else if ( line.indexOf( "</TABLE></CENTER>" ) != -1 ) {
                    inDownstream = false;
                    inUpstream = false;
                } else {
                    line = line.replace( "<TABLE border=0 cellPadding=0 cellSpacing=0 width=300>        <TBODY><TR>          <TD align=left><SMALL>The Downstream Power Level reading is a             snapshot taken at the time this page was requested. Please             Reload/Refresh this Page for a new reading           </SMALL></TD></TR></TBODY></TABLE>", "" );
                    line = line.replace( "&nbsp;", "" ).replace( " ", "" ).replace( "</TD>", "" ).replace( "<TD>", "|" ).replace( "</TR>", "" ).replace( "<TR>", ";" );
                    if ( inUpstream ) {
                        upstreamTable.append( line );
                    } else {
                        downstreamTable.append( line );
                    }
                }
            }
        }

        List<DownstreamData> dDataRows = new ArrayList<DownstreamData>( 8 );
        for ( int i = 1; i <= 8; i++ ) {
            dDataRows.add( new DownstreamData( statusPageRequestTime, i ) );
        }
        for ( String row : downstreamTable.toString().split( ";" ) ) {
            Iterator<DownstreamData> dDataRowsIterator = dDataRows.iterator();
            for ( String col : row.split( "\\|" ) ) {
                if ( col.equals( "" ) ) {
                    continue;
                }
                if ( row.indexOf( "ChannelID" ) != -1 ) {
                    if ( col.indexOf( "ChannelID" ) == -1 ) {
                        DownstreamData data = dDataRowsIterator.next();
                        data.setChannelID( Integer.parseInt( col.trim() ) );
                    }
                } else if ( row.indexOf( "Frequency" ) != -1 ) {
                    if ( col.indexOf( "Frequency" ) == -1 ) {
                        DownstreamData data = dDataRowsIterator.next();
                        data.setFrequency( col );
                    }
                } else if ( row.indexOf( "SignaltoNoiseRatio" ) != -1 ) {
                    if ( col.indexOf( "SignaltoNoiseRatio" ) == -1 ) {
                        DownstreamData data = dDataRowsIterator.next();
                        data.setSNR( col );
                    }
                } else if ( row.indexOf( "DownstreamModulation" ) != -1 ) {
                    if ( col.indexOf( "DownstreamModulation" ) == -1 ) {
                        DownstreamData data = dDataRowsIterator.next();
                        data.setModulation( col );
                    }
                } else if ( row.indexOf( "PowerLevel" ) != -1 ) {
                    if ( col.indexOf( "PowerLevel" ) == -1 ) {
                        DownstreamData data = dDataRowsIterator.next();
                        data.setPower( col );
                    }
                }
            }
        }
        for ( DownstreamData dData : dDataRows ) {
            pageData.add( dData );
        }
        
        List<UpstreamData> uDataRows = new ArrayList<UpstreamData>( 4 );
        for ( int i = 1; i <= 4; i++ ) {
            uDataRows.add( new UpstreamData( statusPageRequestTime, i ) );
        }
        for ( String row : upstreamTable.toString().split( ";" ) ) {
            Iterator<UpstreamData> uDataRowsIterator = uDataRows.iterator();
            for ( String col : row.split( "\\|" ) ) {
                if ( col.equals( "" ) ) {
                    continue;
                }
                if ( row.indexOf( "ChannelID" ) != -1 ) {
                    if ( col.indexOf( "ChannelID" ) == -1 ) {
                        UpstreamData data = uDataRowsIterator.next();
                        data.setChannelID( Integer.parseInt( col.trim() ) );
                    }
                } else if ( row.indexOf( "Frequency" ) != -1 ) {
                    if ( col.indexOf( "Frequency" ) == -1 ) {
                        UpstreamData data = uDataRowsIterator.next();
                        data.setFrequency( col );
                    }
                } else if ( row.indexOf( "SymbolRate" ) != -1 ) {
                    if ( col.indexOf( "SymbolRate" ) == -1 ) {
                        UpstreamData data = uDataRowsIterator.next();
                        data.setSymbolRate( col );
                    }
                } else if ( row.indexOf( "PowerLevel" ) != -1 ) {
                    if ( col.indexOf( "PowerLevel" ) == -1 ) {
                        UpstreamData data = uDataRowsIterator.next();
                        data.setPower( col );
                    }
                }
            }
        }
        for ( UpstreamData uData : uDataRows ) {
            pageData.add( uData );
        }
        return pageData;
    }
    
}
