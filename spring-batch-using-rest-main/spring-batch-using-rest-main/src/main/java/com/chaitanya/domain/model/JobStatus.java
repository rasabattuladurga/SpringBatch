package com.chaitanya.domain.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class JobStatus {
	private long jodId;
	private long exeutionId;
	private String status;
	private String batchStatus;
	private Date startTime;
	private Date endTime;
	private int processed;
	private int skipped;
	private List<String> failureExceptions;
}
