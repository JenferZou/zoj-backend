package com.jenfer.zoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jenfer.zoj.common.ErrorCode;
import com.jenfer.zoj.constant.CommonConstant;
import com.jenfer.zoj.exception.BusinessException;
import com.jenfer.zoj.judge.JudgeService;
import com.jenfer.zoj.model.dto.question.QuestionQueryRequest;
import com.jenfer.zoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.jenfer.zoj.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.jenfer.zoj.model.entity.Question;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import com.jenfer.zoj.model.entity.User;
import com.jenfer.zoj.model.enums.QuestionSubmitLanguageEnum;
import com.jenfer.zoj.model.enums.QuestionSubmitStatusEnum;
import com.jenfer.zoj.model.vo.QuestionSubmitVO;
import com.jenfer.zoj.model.vo.QuestionVO;
import com.jenfer.zoj.model.vo.UserVO;
import com.jenfer.zoj.service.QuestionService;
import com.jenfer.zoj.service.QuestionSubmitService;
import com.jenfer.zoj.service.QuestionSubmitService;
import com.jenfer.zoj.mapper.QuestionSubmitMapper;
import com.jenfer.zoj.service.UserService;
import com.jenfer.zoj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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


    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

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

        Long questionSubmitId = questionSubmit.getId();
        //执行判题服务
        CompletableFuture.runAsync(()->{
            judgeService.doJudge(questionSubmitId);
        });

        return questionSubmitId;
    }


    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.like(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status)!=null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long questionId = questionSubmit.getId();
        //脱敏、只有本人和管理员才能看见提交代码的答案
        Long userId = loginUser.getId();
        if (!Objects.equals(userId, questionSubmit.getUserId()) && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage =
                new Page<>(questionSubmitPage.getCurrent(),
                        questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(
                questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser)
        ).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




