package com.inha.coinkaraoke.ledgerApi.entityUtils;

import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.Optional;

public class EntityManager {

    private EntityManager() {}

    @Transaction(intent = TYPE.SUBMIT)
    public void saveEntity(ChaincodeStub stub, Entity entity) {

        CompositeKey compositeKey = stub.createCompositeKey(entity.getSimpleClassName(),
                Entity.splitKey(entity.getKey()));
        byte[] data = ObjectMapperHolder.serialize(entity);
        stub.putState(compositeKey.toString(), data);
    }

    @Transaction(intent = TYPE.SUBMIT)
    public void updateEntity(ChaincodeStub stub, Entity entity) {

        CompositeKey compositeKey = stub.createCompositeKey(entity.getSimpleClassName(),
                Entity.splitKey(entity.getKey()));
        byte[] data = ObjectMapperHolder.serialize(entity);
        stub.putState(compositeKey.toString(), data);
    }

    @Transaction(intent = TYPE.EVALUATE)
    public Optional<Entity> getById(ChaincodeStub stub, String key, Class<?> cls) {

        CompositeKey compositeKey = stub.createCompositeKey(cls.getSimpleName(), Entity.splitKey(key));
        byte[] data = stub.getState(compositeKey.toString());

        Entity object = null;
        if(data != null && data.length > 0)
            object = ObjectMapperHolder.deserialize(data, cls);

        return Optional.ofNullable(object);
    }


    public static class Factory {

        static final EntityManager entityManager = new EntityManager();

        public static EntityManager getInstance() {

            return entityManager;
        }
    }

}
