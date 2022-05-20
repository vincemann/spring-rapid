//package com.github.vincemann.springrapid.autobidir.advice;
//
//import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
//import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;
//import com.github.vincemann.springrapid.autobidir.util.BiDirJpaUtils;
//import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
//import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
//import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
//import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
//import com.github.vincemann.springrapid.autobidir.RelationalEntityManagerUtil;
//import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Collection;
//import java.util.Set;
//
//@Aspect
//@Slf4j
///**
// * Advice that keeps BiDirRelationships intact for Repo save operations (also update)
// */
//public class BiDirEntitySaveAdvice extends BiDirEntityAdvice {
//
//
//    @Autowired
//    public BiDirEntitySaveAdvice(CrudServiceLocator crudServiceLocator, RelationalEntityManagerUtil relationalEntityManagerUtil) {
//        super(crudServiceLocator, relationalEntityManagerUtil);
//    }
//
//
//    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
//            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
//            "args(entity)")
//    public void prePersistEntity(IdentifiableEntity entity) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
//        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(entity.getClass());
//        // subscribedUsers is 1 instead of 0
//        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)){
//            // also filter for class obj stored in annotation, so if I update only one BiDirChildCollection, only init this one
//            // with the right class
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildCollection.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildEntity.class);
//            // subscribedUsers is 1 instead of 0
//            if (entity.getId() == null) {
//                //create
//                log.debug("pre persist biDirParent hook reached for: " + entity);
//                setChildrensParentRef(entity);
//            } else {
//                // update
//                log.debug("pre update biDirParent hook reached for: " + entity);
//                updateBiDirParentRelations(entity);
//                // need to replace child here for partial update entity situation (replace detached child with session attached child (this))
//                replaceChildrensParentRef(entity);
//                // needs to be done to prevent detached error when adding entity to child via full update or save
//                mergeParentsChildren(entity);
//            }
//        }
//
//        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)){
////            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentEntity.class);
////            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentCollection.class);
//            if ( entity.getId() == null) {
//                //create
//                log.debug("pre persist biDirChild hook reached for: " + entity);
//                setParentsChildRef(entity);
//            } else {
//                // update
//                log.debug("pre update biDirChild hook reached for: " + entity);
//                updateBiDirChildRelations(entity);
//                // need to replace child here for partial update parent situation (replace detached child with session attached child (this))
//                replaceParentsChildRef(entity);
//                // needs to be done to prevent detached error when adding parent to child via full update or save
//                mergeChildrensParents(entity);
//            }
//        }
//
//    }
//
//
//
//
//}
