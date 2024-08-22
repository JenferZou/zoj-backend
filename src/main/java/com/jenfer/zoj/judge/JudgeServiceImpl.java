package com.jenfer.zoj.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jenfer.zoj.common.ErrorCode;
import com.jenfer.zoj.exception.BusinessException;
import com.jenfer.zoj.judge.codesandbox.CodeSandbox;
import com.jenfer.zoj.judge.codesandbox.CodeSandboxFactory;
import com.jenfer.zoj.judge.codesandbox.strategy.JudgeManageJudgeStrategy;
import com.jenfer.zoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.jenfer.zoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.jenfer.zoj.judge.codesandbox.strategy.DefaultJudgeStrategy;
import com.jenfer.zoj.judge.codesandbox.strategy.JudgeContext;
import com.jenfer.zoj.judge.codesandbox.strategy.JudgeStrategy;
import com.jenfer.zoj.model.dto.question.JudgeCase;
import com.jenfer.zoj.model.dto.questionSubmit.JudgeInfo;
import com.jenfer.zoj.model.entity.Question;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import com.jenfer.zoj.model.enums.QuestionSubmitStatusEnum;
import com.jenfer.zoj.proxy.CodeSandboxProxy;
import com.jenfer.zoj.service.QuestionService;
import com.jenfer.zoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;


    @Resource
    private JudgeManageJudgeStrategy judgeManageJudgeStrategy;

    @Value("${codesandbox.type}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //传入题目id(获取对应题目信息，同时获取用户提交的代码等数据)
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if(Objects.isNull(questionSubmit)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if(Objects.isNull(question)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }
        //校验当前题目状态，如果是不为等待中的话就不必执行了
        if(!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "正在判题中");
        }

        //更改判题状态为判题中
        boolean update = questionSubmitService.update(
                new LambdaUpdateWrapper<QuestionSubmit>()
                        .eq(QuestionSubmit::getId, questionSubmitId)
                        .set(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.RUNNING.getValue())
        );
        if(!update){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "判题状态更新失败");
        }

        //调用沙箱进行判题并且获取执行结构
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .inputList(inputList)
                .language(language)
                .code(code)
                .build();
        //获取沙箱执行结果
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        //根据策略模式进行判题
        List<String> outputList = executeCodeResponse.getOutputList();
        //获取判题逻辑所需上下文
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setInputList(inputList);
        judgeContext.setQuestion(question);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManageJudgeStrategy.doJudge(judgeContext);

        //更新数据库中判题信息
        boolean questionSubmitUpdate = questionSubmitService.update(
                new LambdaUpdateWrapper<QuestionSubmit>()
                        .eq(QuestionSubmit::getId, questionSubmitId)
                        .set(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.SUCCEED.getValue())
                        .set(QuestionSubmit::getJudgeInfo, JSONUtil.toJsonStr(judgeInfo))
        );
        if(!questionSubmitUpdate){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "判题状态更新失败");
        }
        QuestionSubmit result = questionSubmitService.getById(questionSubmitId);
        return result;
    }
}
