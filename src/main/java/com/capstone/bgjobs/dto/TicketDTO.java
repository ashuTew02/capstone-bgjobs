package com.capstone.bgjobs.dto;

public class TicketDTO {
    private String ticketId;
    private String issueTypeName;
    private String issueTypeDescription;
    private String summary;
    private String statusName;

    // NEW: the ES finding ID associated with this ticket
    private String esFindingId;

    public TicketDTO() {}

    public TicketDTO(String ticketId, String issueTypeName, String issueTypeDescription,
                     String summary, String statusName) {
        this.ticketId = ticketId;
        this.issueTypeName = issueTypeName;
        this.issueTypeDescription = issueTypeDescription;
        this.summary = summary;
        this.statusName = statusName;
    }

    // getters & setters
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getIssueTypeName() { return issueTypeName; }
    public void setIssueTypeName(String issueTypeName) { this.issueTypeName = issueTypeName; }

    public String getIssueTypeDescription() { return issueTypeDescription; }
    public void setIssueTypeDescription(String issueTypeDescription) {
        this.issueTypeDescription = issueTypeDescription;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public String getEsFindingId() { return esFindingId; }
    public void setEsFindingId(String esFindingId) { this.esFindingId = esFindingId; }
}
