package com.jenfer.zoj.judge;

import com.jenfer.zoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}
