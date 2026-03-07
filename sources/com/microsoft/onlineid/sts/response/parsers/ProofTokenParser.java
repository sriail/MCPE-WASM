package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ProofTokenParser extends BasePullParser {
    private byte[] _sessionKey;

    public ProofTokenParser(XmlPullParser underlyingParser) {
        super(underlyingParser, AbstractSoapRequest.WstNamespace, "RequestedProofToken");
    }

    /* access modifiers changed from: protected */
    public void onParse() throws XmlPullParserException, IOException, StsParseException {
        boolean z;
        boolean z2;
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("EncryptedKey")) {
                new EncryptedSoapNodeParser(this._parser, "EncryptedKey").parse();
                if (this._sessionKey == null) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                Assertion.check(z2, "Only one of EncryptedKey or wst:BinarySecret is expected");
                this._sessionKey = null;
            } else if (prefixedTagName.equals("wst:BinarySecret")) {
                if (this._sessionKey == null) {
                    z = true;
                } else {
                    z = false;
                }
                Assertion.check(z, "Only one of EncryptedKey or wst:BinarySecret is expected");
                this._sessionKey = TextParsers.parseBase64(nextRequiredText());
            } else {
                skipElement();
            }
        }
    }

    public byte[] getSessionKey() {
        verifyParseCalled();
        return this._sessionKey;
    }
}
