package com.dougdomingos.expensetracker.services.transaction;

import com.dougdomingos.expensetracker.dto.transaction.BalanceResponseDTO;

public interface BalanceService {

    public BalanceResponseDTO getCurrentBalance();
}
