package com.inha.coinkaraoke.services;

import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager;
import com.inha.coinkaraoke.ledgerApi.impl.AccountServiceImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private EntityManager entityManager;

    private Context ctx;

    @BeforeEach
    public void setUp() {
        ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
    }

    @Nested
    public class AccountInfoTest {
        @Test
        @DisplayName("ledger 에 아무 정보가 없을 때, 잔고를 0으로 표시해서 응답한다.")
        public void successToMakeAccountInfoTest() {

            Account expectedAccount = new Account("user1");
            given(entityManager.getById(any(), any(), any())).willReturn(Optional.empty());

            Account returned = accountService.getBalance(ctx, "user1");

            assertThat(returned)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedAccount);
            then(entityManager).should(times(1)).getById(any(), any(), any());
        }

        @Test
        @DisplayName("정상적으로 잔고 정보를 조회한다.")
        public void successToGetAccountInfoTest() {

            Account existedAccount = new Account("user1");
            existedAccount.receive(543.365);
            existedAccount.stake(23.1);
            given(entityManager.getById(any(), eq("user1"), any())).willReturn(Optional.of(existedAccount));

            Account returned = accountService.getBalance(ctx, "user1");

            assertThat(returned)
                    .usingRecursiveComparison()
                    .isEqualTo(existedAccount);
            then(entityManager).should(times(1)).getById(any(), any(), any());
        }
    }


    @Nested
    public class TransferTest {

        @Test
        @DisplayName("잔고가 충분하지 않으면, 오류 발생.")
        public void transferTest() {

            assertThrows(
                    ChaincodeException.class,
                    () -> accountService.transfer(ctx, "sender1", "receiver", 12313243L, 13.2));
        }

        @Test
        @DisplayName("정상적으로 송금을 완료")
        public void successToTransferTest() {

            String senderId = "sender";
            String receiverId = "receiver";

            Account sender = new Account(senderId);
            sender.receive(100.0);
            Account receiver = new Account(receiverId);
            given(entityManager.getById(any(), eq(senderId), any())).willReturn(Optional.of(sender));
            given(entityManager.getById(any(), eq(receiverId), any())).willReturn(Optional.of(receiver));
            doNothing().when(entityManager).saveEntity(any(), any());

            accountService.transfer(ctx, senderId, receiverId, 12313243L, 13.2);

            then(entityManager).should(times(2)).updateEntity(any(), any());
            then(entityManager).should(times(1)).saveEntity(any(), any());
            assertThat(receiver.getAvailableBalance()).isEqualTo(13.2);
            assertThat(sender.getAvailableBalance()).isEqualTo(86.8);
        }
    }
}
