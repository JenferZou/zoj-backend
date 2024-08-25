package com.jenfer.zoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jenfer.zoj.common.BaseResponse;
import com.jenfer.zoj.common.ErrorCode;
import com.jenfer.zoj.common.ResultUtils;
import com.jenfer.zoj.exception.BusinessException;
import com.jenfer.zoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.jenfer.zoj.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import com.jenfer.zoj.model.entity.User;
import com.jenfer.zoj.model.vo.QuestionSubmitVO;
import com.jenfer.zoj.service.QuestionSubmitService;
import com.jenfer.zoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 
 */
//@RestController
//@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

//    @Resource
//    private QuestionSubmitService questionSubmitService;
//
//    @Resource
//    private UserService userService;
//
//    /**
//     * 提交题目
//     *
//     * @param questionSubmitAddRequest
//     * @param request
//     * @return resultNum 本次点赞变化数
//     */
//    @PostMapping("/")
//    public BaseResponse<Long> doSubmitQuesiton(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
//            HttpServletRequest request) {
//        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        final User loginUser = userService.getLoginUser(request);
//        Long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
//        return ResultUtils.success(questionSubmitId);
//    }
//
//
//    /**
//     * 分页获取已提交列表
//     *
//     * @param questionSubmitQueryRequest
//     * @param request
//     * @return resultNum 本次点赞变化数
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
//                                                                         HttpServletRequest request) {
//
//        long current = questionSubmitQueryRequest.getCurrent();
//        long pageSize = questionSubmitQueryRequest.getPageSize();
//        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, pageSize), questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
//        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,userService.getLoginUser(request)));
//    }

}
