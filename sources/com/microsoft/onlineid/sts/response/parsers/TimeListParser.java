package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.DateParser;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TimeListParser extends BasePullParser {
    private Date _expires;

    public TimeListParser(XmlPullParser underlyingParser) {
        super(underlyingParser, (String) null, (String) null);
    }

    /* access modifiers changed from: protected */
    public void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("wsu:Expires");
        DateParser parser = new DateParser(this._parser, DateParser.DateType.Iso8601DateTimeIgnoreTimeZone);
        parser.parse();
        this._expires = parser.getDate();
    }

    public Date getExpires() {
        verifyParseCalled();
        return this._expires;
    }
}
