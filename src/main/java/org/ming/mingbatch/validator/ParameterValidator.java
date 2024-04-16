package org.ming.mingbatch.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class ParameterValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        // 파일명 체크
        String fileName = jobParameters.getString("fileName");

        if (!StringUtils.hasText(fileName)) {
            throw new JobParametersInvalidException("fileName parameter is missing!");
        }
        if (!StringUtils.endsWithIgnoreCase(fileName, "csv")) {
            throw new JobParametersInvalidException("fileName parameter does use csv file extension");
        }

        // 날짜 체크
        String currentDate = jobParameters.getString("currentDate");
        if (!StringUtils.hasText(currentDate)) {
            throw new JobParametersInvalidException("currentDate parameter is missing!");
        }
    }
}
