package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.account.type.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSuccess()
    {
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pororo1").build();


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("10000015").build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        AccountDto accountDto = accountService.createAccount(1L,1000L);

        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDto.getUserId());
        assertEquals("10000000", accountDto.getAccountNum());

    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패 ")
    void createAccount_UserNotFound()
    {
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,() ->
        accountService.createAccount(1L,1000L));

        assertEquals(USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 해지 실패 ")
    void deleteAccount_UserNotFound()
    {
        AccountUser user = AccountUser.builder()
                        .id(12L)
                        .name("pororo10").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,() ->
                accountService.createAccount(1L,1000L));

        assertEquals(ACCOUNT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("계좌 소유주 다름")
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
                accountService.createAccount(1L,12345L));

        assertEquals(USER_ACCOUNT_UNMATCHED, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 당 최대 계좌 수는 10개 ")
    void createAccount_maxAccountIs10()
    {
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pororo1").build();

        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        AccountException exception = assertThrows(AccountException.class,() ->
                accountService.createAccount(1L,1000L));

        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_ID, exception.getErrorCode());


    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 합니다 ")
    void deleteAccountFailed_balanceNotEmpty()
    {
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pororo1").build();

        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(100L)
                        .accountNumber("100000012").build()
                ));

        AccountException exception = assertThrows(AccountException.class,() ->
                accountService.deleteAccount(100L,"100000012"));

        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_ID, exception.getErrorCode());


    }

    @Test
    @DisplayName("이미 해지된 계좌")
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
                accountService.createAccount(1L,1000L));

        assertEquals(ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }
//
//    @Test
//    @DisplayName("유저 당 최대 계좌 수는 10개 ")
//    void createAccount_maxAccountIs10()
//    {
//        AccountUser user = AccountUser.builder()
//                .id(15L)
//                .name("pororo1").build();
//
//        given(accountRepository.countByAccountUser(any()))
//                .willReturn(10);
//
//        AccountException exception = assertThrows(AccountException.class,() ->
//                accountService.createAccount(1L,1000L));
//
//        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_ID, exception.getErrorCode());
//
//
//    }

    @Test
    void successGetAccountByUserId(){
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("pobi").build();
        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(user)
                        .accountNumber("0987654321")
                        .balance(900L)
                        .build(),
                Account.builder()
                        .accountUser(user)
                        .accountNumber("1111111111")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(user)
                        .accountNumber("2222222222")
                        .balance(4000L)
                        .build()
        );
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);
        //when
        List<AccountDto> accountDtos = accountService.getAccountByUserId(1L);
        //then
        assertEquals(3,accountDtos.size());
        assertEquals("1111111111",accountDtos.get(1).getAccountNum());
        assertEquals(1000,accountDtos.get(1).getBalance());

        assertEquals("2222222222",accountDtos.get(2).getAccountNum());
        assertEquals(4000,accountDtos.get(2).getBalance());

        assertEquals("0987654321",accountDtos.get(0).getAccountNum());
        assertEquals(900,accountDtos.get(0).getBalance());
    }


    @Test
    void failedToGetAccount(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,() ->
                accountService.getAccountByUserId(1L));
        //then

        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }
}