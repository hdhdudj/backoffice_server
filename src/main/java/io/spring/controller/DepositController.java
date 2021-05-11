package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.deposit.JpaDepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value="/deposit")
@RequiredArgsConstructor
public class DepositController {
    private final JpaDepositService jpaDepositService;
    private final JpaCommonService jpaCommonService;

    @PostMapping(path="/insertbyjpa")
    public ResponseEntity insertDepositJpa(@RequestBody DepositInsertRequestData depositInsertRequestData){
        String depositNo = jpaCommonService.getStrNumberId(StringFactory.getDUpperStr(), depositInsertRequestData.getDepositNo(), StringFactory.getStrDepositNo(), StringFactory.getIntEight());
        depositInsertRequestData.setDepositNo(depositNo); // deposit no 채번
        jpaDepositService.sequenceInsertDeposit(depositInsertRequestData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), depositNo);
        return ResponseEntity.ok(res);
    }
}
