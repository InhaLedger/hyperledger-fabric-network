package com.inha.coinkaraoke.accounts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.inha.coinkaraoke.AccountContract;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import java.security.Principal;
import java.security.cert.X509Certificate;
import org.assertj.core.api.Assertions;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        given(accountService.getBalance(any(), anyString())).willReturn(0.0d);

        //when
        Double returnedBalance = contract.getBalance(ctx);

        //then
        Assertions.assertThat(returnedBalance).isEqualTo(0.0d);
        then(accountService).should(times(1))
                .getBalance(any(), anyString());
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
