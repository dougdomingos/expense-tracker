package com.dougdomingos.expensetracker.services.transaction;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.auth.AuthUtils;
import com.dougdomingos.expensetracker.dto.transaction.BalanceResponseDTO;
import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.repositories.TransactionRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    @Override
    public BalanceResponseDTO getCurrentBalance() {

        User currentUser = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        String currentMonth = Calendar
                .getInstance()
                .getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        List<Transaction> transactions = transactionRepository.findByOwnerAndCreatedAtBetween(
                currentUser,
                getFirstDateOfCurrentMonth(),
                getLastDateOfCurrentMonth());

        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType().equals(TransactionType.EXPENSE)) {
                transaction.setAmount(transaction.getAmount() * -1);
            }

            total += transaction.getAmount();
        }

        return BalanceResponseDTO.builder()
                .balance(total)
                .currentMonth(currentMonth)
                .build();
    }

    private LocalDateTime getFirstDateOfCurrentMonth() {
        return LocalDateTime.of(YearMonth.now().atDay(1), LocalTime.MIN);
    }

    private LocalDateTime getLastDateOfCurrentMonth() {
        return LocalDateTime.of(YearMonth.now().atEndOfMonth(), LocalTime.MAX);
    }

}
