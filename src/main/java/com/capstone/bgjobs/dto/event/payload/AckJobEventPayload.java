package com.capstone.bgjobs.dto.event.payload;

import com.capstone.bgjobs.model.JobStatus;

public class AckJobEventPayload {
    private Long jobId;
    private JobStatus jobStatus;

    public AckJobEventPayload() {}

    public AckJobEventPayload(Long jobId, JobStatus jobStatus) {
        this.jobId = jobId;
        this.jobStatus = jobStatus;
    }
    public Long getJobId() {
        return jobId;
    }
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public JobStatus getJobStatus() {
        return jobStatus;
    }
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

}
