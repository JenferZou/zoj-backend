package com.jenfer.zoj.judge.codesandbox;

import com.jenfer.zoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.jenfer.zoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

@Service
public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequestion);

}
