package com.microsoft.xbox.service.network.managers;

import android.support.v4.view.ViewCompat;
import com.google.gson.annotations.SerializedName;

public class ProfilePreferredColor {
    private int primary = -1;
    @SerializedName("primaryColor")
    private String primaryColorString;
    private int secondary = -1;
    @SerializedName("secondaryColor")
    private String secondaryColorString;
    private int tertiary = -1;
    @SerializedName("tertiaryColor")
    private String tertiaryColorString;

    public int getPrimaryColor() {
        if (this.primary < 0) {
            this.primary = convertColorFromString(this.primaryColorString);
        }
        return this.primary;
    }

    public int getSecondaryColor() {
        if (this.secondary < 0) {
            this.secondary = convertColorFromString(this.secondaryColorString);
        }
        return this.secondary;
    }

    public int getTertiaryColor() {
        if (this.tertiary < 0) {
            this.tertiary = convertColorFromString(this.tertiaryColorString);
        }
        return this.tertiary;
    }

    public static int convertColorFromString(String colorString) {
        if (colorString == null) {
            return 0;
        }
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
        }
        int color = Integer.parseInt(colorString, 16);
        if ((color >> 24) == 0) {
            return color | ViewCompat.MEASURED_STATE_MASK;
        }
        return color;
    }
}
