package com.jenfer.zoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jenfer.zoj.model.dto.question.JudgeConfig;
import com.jenfer.zoj.model.dto.questionSubmit.JudgeInfo;
import com.jenfer.zoj.model.entity.Question;
import com.jenfer.zoj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * questionVo
 */
@Data
public class QuestionSubmitVO {
    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;




    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);

        JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
        if (judgeInfo != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }

        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionVO);
        String judgeInfoStr = questionSubmit.getJudgeInfo();
        questionVO.setJudgeInfo(JSONUtil.toBean(judgeInfoStr, JudgeInfo.class));
        return questionVO;
    }
}
