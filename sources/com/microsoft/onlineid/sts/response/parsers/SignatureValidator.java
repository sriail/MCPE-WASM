package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import android.util.Xml;
import com.microsoft.onlineid.sts.XmlSigner;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.exception.StsSignatureException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SignatureValidator {
    private final Map<String, String> _computedDigests = new HashMap();
    /* access modifiers changed from: private */
    public final Map<String, String> _parsedDigests = new HashMap();
    private final byte[] _sessionKey;
    private byte[] _signKeyNonce;
    /* access modifiers changed from: private */
    public String _signatureValue;
    /* access modifiers changed from: private */
    public String _signedInfoXml;
    private final XmlSigner _signer = new XmlSigner();

    public SignatureValidator(byte[] sessionKey) {
        this._sessionKey = sessionKey;
    }

    public XmlPullParser computeNodeDigest(BasePullParser parser) throws XmlPullParserException, IOException, StsParseException {
        XmlPullParser subParser = parser._parser;
        subParser.require(2, (String) null, (String) null);
        String id = subParser.getAttributeValue((String) null, "Id");
        if (TextUtils.isEmpty(id)) {
            return subParser;
        }
        String xml = parser.readRawOuterXml();
        String digest = this._signer.computeDigest(xml);
        if (this._computedDigests.containsKey(id)) {
            throw new StsSignatureException("Duplicate element for Id=\"" + id + "\"", new Object[0]);
        }
        this._computedDigests.put(id, digest);
        XmlPullParser subParser2 = Xml.newPullParser();
        subParser2.setInput(new StringReader(xml));
        return subParser2;
    }

    public void parseSignatureNode(BasePullParser parser) throws StsParseException, IOException, XmlPullParserException {
        new BasePullParser(parser._parser, XmlSigner.SignatureNamespace, "Signature") {
            /* access modifiers changed from: protected */
            public void onParse() throws XmlPullParserException, IOException, StsParseException {
                while (nextStartTagNoThrow()) {
                    String tag = getPrefixedTagName();
                    if ("SignedInfo".equals(tag)) {
                        SignatureValidator.this.parseSignedInfoNode(this);
                    } else if ("SignatureValue".equals(tag)) {
                        String unused = SignatureValidator.this._signatureValue = nextRequiredText();
                    } else {
                        skipElement();
                    }
                }
                if (TextUtils.isEmpty(SignatureValidator.this._signatureValue)) {
                    throw new StsSignatureException("<SignatureValue> node was missing.", new Object[0]);
                } else if (TextUtils.isEmpty(SignatureValidator.this._signedInfoXml)) {
                    throw new StsSignatureException("<SignedInfo> node was missing.", new Object[0]);
                }
            }
        }.parse();
    }

    /* access modifiers changed from: private */
    public void parseSignedInfoNode(BasePullParser parser) throws StsParseException, IOException, XmlPullParserException {
        this._signedInfoXml = parser.readRawOuterXml();
        XmlPullParser subParser = Xml.newPullParser();
        subParser.setInput(new StringReader(this._signedInfoXml));
        new BasePullParser(subParser, (String) null, "SignedInfo") {
            /* access modifiers changed from: protected */
            public void onParse() throws XmlPullParserException, IOException, StsParseException {
                while (nextStartTagNoThrow("Reference")) {
                    String id = this._parser.getAttributeValue((String) null, "URI");
                    NodeScope referenceScope = getLocation();
                    if (!referenceScope.nextStartTagNoThrow("DigestValue")) {
                        throw new StsSignatureException("Missing DigestValue for URI " + id, new Object[0]);
                    }
                    String digest = referenceScope.nextRequiredText();
                    if (TextUtils.isEmpty(id) || !id.startsWith("#")) {
                        throw new StsSignatureException("Invalid digest URI: " + id, new Object[0]);
                    } else if (TextUtils.isEmpty(digest)) {
                        throw new StsSignatureException("Invalid digest: " + digest, new Object[0]);
                    } else {
                        SignatureValidator.this._parsedDigests.put(id.substring(1), digest);
                    }
                }
            }
        }.parse();
    }

    public void setSignKeyNonce(byte[] signKeyNonce) {
        this._signKeyNonce = signKeyNonce;
    }

    public boolean canValidate() {
        return this._sessionKey != null && !TextUtils.isEmpty(this._signedInfoXml) && this._signKeyNonce != null && !TextUtils.isEmpty(this._signatureValue);
    }

    public void validate() throws StsSignatureException {
        for (Map.Entry<String, String> entry : this._computedDigests.entrySet()) {
            if (this._parsedDigests.containsKey(entry.getKey())) {
                String digest = this._parsedDigests.remove(entry.getKey());
                if (!digest.equals(entry.getValue())) {
                    throw new StsSignatureException(String.format(Locale.US, "Digest mismatch: id=\"%s\", expected=\"%s\", actual=\"%s\"", new Object[]{entry.getKey(), digest, entry.getValue()}), new Object[0]);
                }
            }
        }
        if (!this._parsedDigests.isEmpty()) {
            throw new StsSignatureException("Failed to compute digests for element ids " + Arrays.toString(this._parsedDigests.keySet().toArray()), new Object[0]);
        } else if (TextUtils.isEmpty(this._signedInfoXml)) {
            throw new StsSignatureException("<SignedInfo> node was missing.", new Object[0]);
        } else if (this._signKeyNonce == null || this._signKeyNonce.length == 0) {
            throw new StsSignatureException("SignKey nonce was missing or invalid.", new Object[0]);
        } else {
            String signature = this._signer.computeSignatureForResponse(this._sessionKey, this._signKeyNonce, this._signedInfoXml);
            if (!this._signatureValue.equals(signature)) {
                throw new StsSignatureException(String.format(Locale.US, "Signature mismatch: expected=\"%s\", actual=\"%s\"", new Object[]{this._signatureValue, signature}), new Object[0]);
            }
        }
    }
}
