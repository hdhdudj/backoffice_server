package io.spring.model.napi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeywordTool {
	String relKeyword;
	String monthlyPcQcCnt;
	String monthlyMobileQcCnt;
	String monthlyAvePcClkCnt;
	String monthlyAveMobileClkCnt;
	String monthlyAvePcCtr;
	String monthlyAveMobileCtr;
	String plAvgDepth;
	String compIdx;

}
