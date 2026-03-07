package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public final class AppInviteContent implements ShareModel {
    public static final Parcelable.Creator<AppInviteContent> CREATOR = new Parcelable.Creator<AppInviteContent>() {
        public AppInviteContent createFromParcel(Parcel in) {
            return new AppInviteContent(in);
        }

        public AppInviteContent[] newArray(int size) {
            return new AppInviteContent[size];
        }
    };
    private final String applinkUrl;
    private final String previewImageUrl;
    private final String promoCode;
    private final String promoText;

    private AppInviteContent(Builder builder) {
        this.applinkUrl = builder.applinkUrl;
        this.previewImageUrl = builder.previewImageUrl;
        this.promoCode = builder.promoCode;
        this.promoText = builder.promoText;
    }

    AppInviteContent(Parcel in) {
        this.applinkUrl = in.readString();
        this.previewImageUrl = in.readString();
        this.promoText = in.readString();
        this.promoCode = in.readString();
    }

    public String getApplinkUrl() {
        return this.applinkUrl;
    }

    public String getPreviewImageUrl() {
        return this.previewImageUrl;
    }

    public String getPromotionCode() {
        return this.promoCode;
    }

    public String getPromotionText() {
        return this.promoText;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.applinkUrl);
        out.writeString(this.previewImageUrl);
        out.writeString(this.promoText);
        out.writeString(this.promoCode);
    }

    public static class Builder implements ShareModelBuilder<AppInviteContent, Builder> {
        /* access modifiers changed from: private */
        public String applinkUrl;
        /* access modifiers changed from: private */
        public String previewImageUrl;
        /* access modifiers changed from: private */
        public String promoCode;
        /* access modifiers changed from: private */
        public String promoText;

        public Builder setApplinkUrl(String applinkUrl2) {
            this.applinkUrl = applinkUrl2;
            return this;
        }

        public Builder setPreviewImageUrl(String previewImageUrl2) {
            this.previewImageUrl = previewImageUrl2;
            return this;
        }

        public Builder setPromotionDetails(String promotionText, String promotionCode) {
            if (!TextUtils.isEmpty(promotionText)) {
                if (promotionText.length() > 80) {
                    throw new IllegalArgumentException("Invalid promotion text, promotionText needs to be between1 and 80 characters long");
                } else if (!isAlphanumericWithSpaces(promotionText)) {
                    throw new IllegalArgumentException("Invalid promotion text, promotionText can only contain alphanumericcharacters and spaces.");
                } else if (!TextUtils.isEmpty(promotionCode)) {
                    if (promotionCode.length() > 10) {
                        throw new IllegalArgumentException("Invalid promotion code, promotionCode can be between1 and 10 characters long");
                    } else if (!isAlphanumericWithSpaces(promotionCode)) {
                        throw new IllegalArgumentException("Invalid promotion code, promotionCode can only contain alphanumeric characters and spaces.");
                    }
                }
            } else if (!TextUtils.isEmpty(promotionCode)) {
                throw new IllegalArgumentException("promotionCode cannot be specified without a valid promotionText");
            }
            this.promoCode = promotionCode;
            this.promoText = promotionText;
            return this;
        }

        public AppInviteContent build() {
            return new AppInviteContent(this);
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public Builder readFrom(AppInviteContent content) {
            return content == null ? this : setApplinkUrl(content.getApplinkUrl()).setPreviewImageUrl(content.getPreviewImageUrl()).setPromotionDetails(content.getPromotionText(), content.getPromotionCode());
        }

        public Builder readFrom(Parcel parcel) {
            return readFrom((AppInviteContent) parcel.readParcelable(AppInviteContent.class.getClassLoader()));
        }

        private boolean isAlphanumericWithSpaces(String str) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (!Character.isDigit(c) && !Character.isLetter(c) && !Character.isSpaceChar(c)) {
                    return false;
                }
            }
            return true;
        }
    }
}
