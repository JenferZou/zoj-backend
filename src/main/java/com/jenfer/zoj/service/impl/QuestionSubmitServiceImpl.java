package com.jenfer.zoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jenfer.zoj.common.ErrorCode;
import com.jenfer.zoj.exception.BusinessException;
import com.jenfer.zoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.jenfer.zoj.model.entity.Question;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import com.jenfer.zoj.model.entity.User;
import com.jenfer.zoj.model.enums.QuestionSubmitLanguageEnum;
import com.jenfer.zoj.model.enums.QuestionSubmitStatusEnum;
import com.jenfer.zoj.service.QuestionService;
import com.jenfer.zoj.service.QuestionSubmitService;
import com.jenfer.zoj.service.QuestionSubmitService;
import com.jenfer.zoj.mapper.QuestionSubmitMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author Jenf
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-08-19 17:11:48
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Resource
    private QuestionService questionService;

    /**
     * 提交题目
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
       //判断编程语言合法
        String language = questionSubmitAddRequest.getLanguage();
        if (!QuestionSubmitLanguageEnum.getValues().contains(language)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        // 判断实体是否存在，根据类别获取实体
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);

        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据插入失败");
        }
        return questionSubmit.getId();
    }
}




