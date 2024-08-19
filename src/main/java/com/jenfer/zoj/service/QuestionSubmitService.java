package com.jenfer.zoj.service;

import com.jenfer.zoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jenfer.zoj.model.entity.User;

/**
* @author Jenf
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-08-19 17:11:48
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 提交题目信息
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);
}
