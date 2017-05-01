package net.illandril.cableModem.status;

public class StatusPageParserFactory {
    private final static StatusPageParser ArrisSB6141 = new ArrisSB6141();
    private final static StatusPageParser ArrisSB6183 = new ArrisSB6183();
    private final static StatusPageParser ArrisTG1682G = new ArrisTG1682G();
    
    public final static StatusPageParser get( String statusPageHTML ) {
        if ( statusPageHTML.indexOf( "Touchstone Status" ) != -1 ) {
            return ArrisTG1682G;
        } else if ( statusPageHTML.indexOf( "signaldata.html" ) != -1 ) {
            return ArrisSB6141;
        } else if ( statusPageHTML.indexOf( "Status" ) != -1 ) {
            return ArrisSB6183;
        } else {
            System.err.println( "Couldn't determine the modem type from the HTML" );
            return null;
        }
    }
}
