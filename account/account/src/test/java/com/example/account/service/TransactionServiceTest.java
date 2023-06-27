package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.account.type.AccountStatus.IN_USE;
import static com.example.account.type.ErrorCode.*;
import static com.example.account.type.TransactionResultType.F;
import static com.example.account.type.TransactionResultType.S;
import static com.example.account.type.TransactionType.USE;
import static com.google.common.base.CharMatcher.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TransactionServiceTest {
    public static final long USE_AMOUNT = 200L;
    public static final long CANCEL_AMOUNT = 200L;
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void successUserBalance(){
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pororo1").build();

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(1000L)
                .accountNumber("10000015").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(ArgumentMatchers.any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("trans_id")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(900L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        TransactionDto transactionDto = transactionService.useBalance(1L,"10000000",USE_AMOUNT );

        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(USE_AMOUNT, captor.getValue().getAmount());
        assertEquals(9800L, captor.getValue().getBalanceSnapshot());

        assertEquals(S,transactionDto.getTransactionResultType());
        assertEquals(USE,transactionDto.getTransactionType());
        assertEquals(900L, transactionDto.getBalanceSnapshot());
        assertEquals(1000L, transactionDto.getAmount());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패 ")
    void useBalance_UserNotFound()
    {
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,() ->
                transactionService.useBalance(1L,"10000015",1000L));

        assertEquals(USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 실패 ")
    void deleteAccount_AccountNotFound()
    {
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("pororo10").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,() ->
                transactionService.useBalance(
                        1L,"10000000000",1000L));

        assertEquals(ACCOUNT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("계좌 소유주 다름 - 잔액 사용 실패  ")
    void deleteAccountFailed_userUnMatched()
    {
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("pororo10").build();
        AccountUser user2 = AccountUser.builder()
                .id(13L)
                .name("pobi")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user2)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        AccountException exception = assertThrows(AccountException.class,() ->
                transactionService.useBalance(1L,"1234567890",1000L));

        assertEquals(USER_ACCOUNT_UNMATCHED, exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 해지된 계좌는 사용할 수 없다 ")
    void deleteAccountFailed_alreadyUnregistered()
    {
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pororo1").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        AccountException exception = assertThrows(AccountException.class,() ->
                transactionService.useBalance(1L,"1234567890",1000L));

        assertEquals(ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }


    @Test
    @DisplayName("거래 금액이 잔액보다 큰 경우")
    void exceedAmount_useBalance(){
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pororo1").build();

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(100L)
                .accountNumber("10000015").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        AccountException exception = assertThrows(AccountException.class,() ->
                transactionService.useBalance(
                        1L,"1234567890",1000L));

        assertEquals(AMOUNT_EXCEED_BALANCE, exception.getErrorCode());

        verify(transactionRepository,times(0)).save(any());

    }



    @Test
    @DisplayName("실제 트렌젝션 저장 성공")
    void saveFailedUseTransaction(){
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pororo1").build();

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(1000L)
                .accountNumber("10000015").build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(ArgumentMatchers.any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("trans_id")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(900L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        transactionService.useBalance(
                1L,"10000000",USE_AMOUNT );

        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(USE_AMOUNT, captor.getValue().getAmount());
        assertEquals(10000L, captor.getValue().getBalanceSnapshot());

        assertEquals(F,captor.getValue().getTransactionResultType());

    }

    @Test
    @DisplayName("원 사용 거래 없음 - 잔액 사용 취소 실패 ")
    void cancelTransaction_transactionNotFound(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId","1234567890",1000L));
        //then
        assertEquals(TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void successQueryTransaction(){
        //given

        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("abc").build();

        Account account = Account.builder()
                .id(1L)
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000012")
                .balance(1000L).build();

        Transaction  transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transaction_id")
                .transactedAt(LocalDateTime.now().minusYears(1))
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        //when
        TransactionDto transactionDto = transactionService.queryTransaction(
                "trxId");


        //then
        assertEquals(USE,transactionDto.getTransactionType());
        assertEquals(S,transactionDto.getTransactionResultType());
        assertEquals(CANCEL_AMOUNT,transactionDto.getAmount());
        assertEquals("transaction_id",transactionDto.getTransactionId());
    }

    @Test
    @DisplayName("원 거래 없음 - 거래 조회 실패 ")
    void queryTransaction_transactionNotFound(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.queryTransaction("transactionId"));
        //then
        assertEquals(TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }



}