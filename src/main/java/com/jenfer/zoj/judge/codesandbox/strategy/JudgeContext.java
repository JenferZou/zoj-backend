package com.jenfer.zoj.judge.codesandbox.strategy;

import com.jenfer.zoj.model.dto.question.JudgeCase;
import com.jenfer.zoj.judge.codesandbox.model.JudgeInfo;
import com.jenfer.zoj.model.entity.Question;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private Question question;

    private QuestionSubmit questionSubmit;

    private List<JudgeCase> judgeCaseList;

}
