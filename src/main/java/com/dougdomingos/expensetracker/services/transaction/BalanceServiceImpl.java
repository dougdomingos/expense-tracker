package com.dougdomingos.expensetracker.services.transaction;

import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.dto.transaction.BalanceResponseDTO;

@Service
public class BalanceServiceImpl implements BalanceService {

    @Override
    public BalanceResponseDTO getCurrentBalance() {
        // TODO Auto-generated method stub
        return BalanceResponseDTO.builder()
                .build();
    }
}
