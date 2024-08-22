package com.jenfer.zoj.judge.codesandbox.strategy;

import com.jenfer.zoj.model.dto.questionSubmit.JudgeInfo;
import org.springframework.stereotype.Service;

@Service
public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext judgeContext);
}
