package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import com.google.common.collect.Sets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.util.*;

public class TransactionalRapidTestUtil {

    private static TransactionalTemplate transactionalTemplate;

    public static void setTransactionalTestTemplate(TransactionalTemplate transactionalTemplate) {
        TransactionalRapidTestUtil.transactionalTemplate = transactionalTemplate;
    }


    public static <E extends IdentifiableEntity> E mustBePresentIn(CrudRepository repo, Serializable id){
        Optional byId = repo.findById(id);
        if (byId.isEmpty()){
            throw new IllegalArgumentException("No Entity found with id: " + id);
        }
        return (E) byId.get();
    }

    public static String[] dtoIdProperties(Class<?> entityClass) {
        List<String> idFields = new ArrayList<>();
        ReflectionUtils.doWithFields(entityClass,field -> {
            if (IdPropertyNameUtils.isIdField(field.getName()) ||
            IdPropertyNameUtils.isCollectionIdField(field.getName())){
                idFields.add(field.getName());
            }
        });
        return Sets.newHashSet(idFields).toArray(new String[0]);
    }

    public static <E extends IdentifiableEntity> E mustBePresentIn(CrudService service, Serializable id){
        Optional byId = null;
        try {
            byId = service.findById(id);
        } catch (BadEntityException e) {
            throw new RuntimeException(e);
        }
        if (byId.isEmpty()){
            throw new IllegalArgumentException("No Entity found with id: " + id);
        }
        return (E) byId.get();
    }

    public static void clear(CrudService crudService){
        transactionalTemplate.doInTransaction(() -> {
            for (IdentifiableEntity entity : (Collection<IdentifiableEntity>) crudService.findAll()) {
                System.err.println("removing entity: " + entity);
                try {
                    crudService.deleteById(entity.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void clear(JpaRepository jpaRepository){
        transactionalTemplate.doInTransaction(() -> {
            for (IdentifiableEntity entity : (Collection<IdentifiableEntity>) jpaRepository.findAll()) {
                System.err.println("removing entity: " + entity);
                try {
                    jpaRepository.deleteById(entity.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    public static String createUpdateJsonLine(String operation, String path, String value){
        return "  {\"op\": \""+operation+"\", \"path\": \""+path+"\", \"value\": \""+value+"\"}";
    }

    public static String createUpdateJsonLine(String operation, String path){
        return "  {\"op\": \""+operation+"\", \"path\": \""+path+"\"}";
    }

    public static String createUpdateJsonRequest(String... lines){
        StringBuilder sb = new StringBuilder()
                .append("[\n" );
        boolean oneLiner = lines.length == 1;
        boolean lastLine = false;
        int count = 0;
        for (String line : lines) {
            if (count+1==lines.length){
                lastLine=true;
            }
            sb.append(line);
            if (!oneLiner && !lastLine){
                sb.append(",");
            }
            sb.append("\n");
            count++;
        }
        sb.append("]");
        return sb.toString();
    }
}
