package com.inha.coinkaraoke.ledgerApi.entityUtils;

import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.Optional;

public class EntityManager<T extends Entity> {

    private final Class<T> type;

    private EntityManager(Class<T> type) {
        this.type = type;
    }

    @Transaction(intent = TYPE.SUBMIT)
    public void saveEntity(ChaincodeStub stub, T entity) {

        CompositeKey compositeKey = stub.createCompositeKey(type.getSimpleName(),
                Entity.splitKey(entity.getKey()));
        byte[] data = ObjectMapperHolder.serialize(entity);
        stub.putState(compositeKey.toString(), data);
    }

    @Transaction(intent = TYPE.SUBMIT)
    public void updateEntity(ChaincodeStub stub, T entity) {

        CompositeKey compositeKey = stub.createCompositeKey(type.getSimpleName(),
                Entity.splitKey(entity.getKey()));
        byte[] data = ObjectMapperHolder.serialize(entity);
        stub.putState(compositeKey.toString(), data);
    }

    @Transaction(intent = TYPE.EVALUATE)
    public Optional<T> getById(ChaincodeStub stub, String key) {

        CompositeKey compositeKey = stub.createCompositeKey(type.getSimpleName(), Entity.splitKey(key));
        byte[] data = stub.getState(compositeKey.toString());

        T object = null;
        if(data != null && data.length > 0)
            object = ObjectMapperHolder.deserialize(data, type);

        return Optional.ofNullable(object);
    }



    public static class EntityManagerFactory {

        public EntityManagerFactory() {}

        public <T extends Entity> EntityManager<T> factory(Class<T> cls) {

            return new EntityManager<>(cls);
        }
    }

}
