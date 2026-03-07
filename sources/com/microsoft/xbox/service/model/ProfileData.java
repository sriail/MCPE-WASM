package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.IUserProfileResult;

public class ProfileData {
    private IUserProfileResult.UserProfileResult profileResult;
    private boolean shareRealName;
    private String shareRealNameStatus;
    private boolean sharingRealNameTransitively;

    public ProfileData(IUserProfileResult.UserProfileResult profileResult2, boolean shareRealName2) {
        this.profileResult = profileResult2;
        this.shareRealName = shareRealName2;
        this.shareRealNameStatus = null;
    }

    public ProfileData(IUserProfileResult.UserProfileResult profileResult2, boolean shareRealName2, String shareRealNameStatus2, boolean sharingRealNameTransitively2) {
        this.profileResult = profileResult2;
        this.shareRealName = shareRealName2;
        this.shareRealNameStatus = shareRealNameStatus2;
        this.sharingRealNameTransitively = sharingRealNameTransitively2;
    }

    public IUserProfileResult.UserProfileResult getProfileResult() {
        return this.profileResult;
    }

    public boolean getShareRealName() {
        return this.shareRealName;
    }

    public String getShareRealNameStatus() {
        return this.shareRealNameStatus;
    }

    public boolean getSharingRealNameTransitively() {
        return this.sharingRealNameTransitively;
    }
}
