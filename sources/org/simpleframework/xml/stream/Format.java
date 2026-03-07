package org.simpleframework.xml.stream;

public class Format {
    private final int indent;
    private final String prolog;
    private final Style style;
    private final Verbosity verbosity;

    public Format() {
        this(3);
    }

    public Format(int indent2) {
        this(indent2, (String) null, (Style) new IdentityStyle());
    }

    public Format(String prolog2) {
        this(3, prolog2);
    }

    public Format(int indent2, String prolog2) {
        this(indent2, prolog2, (Style) new IdentityStyle());
    }

    public Format(Verbosity verbosity2) {
        this(3, verbosity2);
    }

    public Format(int indent2, Verbosity verbosity2) {
        this(indent2, (Style) new IdentityStyle(), verbosity2);
    }

    public Format(Style style2) {
        this(3, style2);
    }

    public Format(Style style2, Verbosity verbosity2) {
        this(3, style2, verbosity2);
    }

    public Format(int indent2, Style style2) {
        this(indent2, (String) null, style2);
    }

    public Format(int indent2, Style style2, Verbosity verbosity2) {
        this(indent2, (String) null, style2, verbosity2);
    }

    public Format(int indent2, String prolog2, Style style2) {
        this(indent2, prolog2, style2, Verbosity.HIGH);
    }

    public Format(int indent2, String prolog2, Style style2, Verbosity verbosity2) {
        this.verbosity = verbosity2;
        this.prolog = prolog2;
        this.indent = indent2;
        this.style = style2;
    }

    public int getIndent() {
        return this.indent;
    }

    public String getProlog() {
        return this.prolog;
    }

    public Style getStyle() {
        return this.style;
    }

    public Verbosity getVerbosity() {
        return this.verbosity;
    }
}
