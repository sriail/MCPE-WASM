package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

public class SubmitFeedbackRequest {
    public String evidenceId;
    public FeedbackType feedbackType;
    public String sessionRef;
    public String textReason;
    public String voiceReasonId;
    public long xuid;

    public SubmitFeedbackRequest(long xuid2, String sessionRef2, FeedbackType feedbackType2, String textReason2, String voiceReasonId2, String evidenceId2) {
        this.xuid = xuid2;
        this.sessionRef = sessionRef2;
        this.feedbackType = feedbackType2;
        this.textReason = textReason2;
        this.voiceReasonId = voiceReasonId2;
        this.evidenceId = evidenceId2;
    }

    public static String getSubmitFeedbackRequestBody(SubmitFeedbackRequest request) {
        return GsonUtil.toJsonString(request);
    }
}
