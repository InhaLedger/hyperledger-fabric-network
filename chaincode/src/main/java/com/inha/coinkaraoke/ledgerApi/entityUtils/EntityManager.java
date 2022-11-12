package com.inha.coinkaraoke.ledgerApi.entityUtils;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Optional;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.json.JSONObject;

public class EntityManager<T extends Entity> {

    private final String typeName;

    private EntityManager(String typeName) {
        this.typeName = typeName;
    }

    @Transaction(intent = TYPE.SUBMIT)
    public void saveEntity(ChaincodeStub stub, T entity) {

        CompositeKey compositeKey = stub.createCompositeKey(typeName,
                Entity.splitKey(entity.getKey()));
        byte[] data = entity.serialize();
        stub.putState(compositeKey.toString(), data);
    }

    @Transaction(intent = TYPE.SUBMIT)
    public void updateEntity(ChaincodeStub stub, T entity) {

        CompositeKey compositeKey = stub.createCompositeKey(typeName,
                Entity.splitKey(entity.getKey()));
        byte[] data = entity.serialize();
        stub.putState(compositeKey.toString(), data);
    }

    @Transaction(intent = TYPE.EVALUATE)
    public Optional<T> getById(ChaincodeStub stub, String key) {

        CompositeKey compositeKey = stub.createCompositeKey(typeName, Entity.splitKey(key));
        byte[] data = stub.getState(compositeKey.toString());
        String json = new JSONObject(data).toString();

        T object = null;
        try {
            object = new Gson().fromJson(json, (Type) Class.forName(typeName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(object);
    }



    public static class EntityManagerFactory {

        public EntityManagerFactory() {}

        public <T extends Entity> EntityManager<T> factory(Class<T> cls) {

            return new EntityManager<>(cls.getName());
        }
    }

}
