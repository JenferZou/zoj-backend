package com.jenfer.zoj.judge.codesandbox.strategy;

import com.jenfer.zoj.judge.codesandbox.model.JudgeInfo;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class JudgeManageJudgeStrategy {


    /**
     * 判题策略的选择
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
