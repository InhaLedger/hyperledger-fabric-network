package com.inha.coinkaraoke.contracts;

import com.inha.coinkaraoke.AccountContract;
import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import org.assertj.core.api.Assertions;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.security.cert.X509Certificate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountContractTest {

    @InjectMocks
    private AccountContract contract;

    @Mock(name = "accountService")
    private AccountService accountService;

    private Context ctx;

    @BeforeEach
    public void setUp() {

        ctx = mock(Context.class);
        ClientIdentity identity = mock(ClientIdentity.class);
        X509Certificate certificate = mock(X509Certificate.class);
        Principal principal = mock(Principal.class);

        when(ctx.getClientIdentity()).thenReturn(identity);
        when(identity.getX509Certificate()).thenReturn(certificate);
        when(certificate.getSubjectDN()).thenReturn(principal);
        when(principal.getName()).thenReturn("user1");
    }

    @Test
    public void getBalanceTest() {

        //given
        Account account = new Account("user1");
        given(accountService.getAccount(any(), anyString())).willReturn(account);

        //when
        Account returnedAccount = contract.getAccount(ctx);

        //then
        Assertions.assertThat(returnedAccount).isEqualTo(account);
        then(accountService).should(times(1))
                .getAccount(any(), anyString());
    }

    @Test
    public void transferTest() {

        //given
        doNothing().when(accountService).transfer(any(), anyString(), anyString(), any(), any());

        //when
        contract.transfer(ctx, "receiverId", 123129473L, 123.3d);

        //then
        then(accountService).should(times(1))
                .transfer(any(), anyString(), anyString(), any(), any());
    }
}
