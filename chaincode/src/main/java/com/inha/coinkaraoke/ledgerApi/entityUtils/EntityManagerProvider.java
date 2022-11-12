package com.inha.coinkaraoke.ledgerApi.entityUtils;

import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager.EntityManagerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * EntityManagerProvider has singleton EntityManagers.
 */
public class EntityManagerProvider {

    private final static EntityManagerProvider INSTANCE = new EntityManagerProvider();

    private Map<Class, EntityManager> managerStore = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityManager<T> getInstance(Class<T> cls) {

        if (!INSTANCE.managerStore.containsKey(cls)) {

            EntityManager<T> manager = new EntityManagerFactory().factory(cls);
            INSTANCE.managerStore.put(cls, manager);
            return manager;

        } else {

            return INSTANCE.managerStore.get(cls);
        }
    }

    private EntityManagerProvider() {
    }
}
