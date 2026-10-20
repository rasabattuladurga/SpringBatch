package com.chaitanya.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobInfo {
	private String jobname;
	private long jobId;
	private List<ExecutionInfo> executions;
}
