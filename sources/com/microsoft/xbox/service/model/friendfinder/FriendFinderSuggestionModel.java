package com.microsoft.xbox.service.model.friendfinder;

import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import java.net.URI;

public class FriendFinderSuggestionModel {
    public String gamerTag;
    public URI imageUri;
    public String presence;
    public String realName;
    public IPeopleHubResult.RecommendationType recommendationType;

    public static FriendFinderSuggestionModel fromPeopleHubSummary(IPeopleHubResult.PeopleHubPersonSummary summary) {
        FriendFinderSuggestionModel model = new FriendFinderSuggestionModel();
        model.imageUri = URI.create(summary.displayPicRaw);
        model.gamerTag = summary.gamertag;
        model.realName = summary.realName;
        model.recommendationType = summary.recommendation != null ? summary.recommendation.getRecommendationType() : IPeopleHubResult.RecommendationType.Unknown;
        model.presence = summary.presenceText;
        return model;
    }
}
