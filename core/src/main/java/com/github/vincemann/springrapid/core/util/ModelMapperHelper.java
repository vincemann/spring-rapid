package com.github.vincemann.springrapid.core.util;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.config.Configuration;
//
//public class ModelMapperHelper {
//
//    private static Configuration initialConfig;
//
//
//    public static void copyInitialConfig(ModelMapper modelMapper) {
//        // todo maybe deepclone of config or whole initial modelmapper instance needed
//        ModelMapperHelper.initialConfig = modelMapper.getConfiguration().copy();
//    }
//
//    public static void resetModelMapper(ModelMapper modelMapper) {
//        modelMapper.getTypeMaps().clear();
//        applyCopiedConfiguration(modelMapper,initialConfig);
//    }
//
//    private static void applyCopiedConfiguration(ModelMapper target, Configuration source) {
//        target.getConfiguration()
//                .setSourceNamingConvention(source.getSourceNamingConvention())
//                .setDestinationNamingConvention(source.getDestinationNamingConvention())
//                .setMatchingStrategy(source.getMatchingStrategy())
//                .setPropertyCondition(source.getPropertyCondition())
//                .setFieldMatchingEnabled(source.isFieldMatchingEnabled())
//                .setFieldAccessLevel(source.getFieldAccessLevel())
//                .setMethodAccessLevel(source.getMethodAccessLevel())
//                .setAmbiguityIgnored(source.isAmbiguityIgnored())
//                .setFullTypeMatchingRequired(source.isFullTypeMatchingRequired())
//                .setImplicitMappingEnabled(source.isImplicitMappingEnabled())
//                .setSkipNullEnabled(source.isSkipNullEnabled())
//                .setCollectionsMergeEnabled(source.isCollectionsMergeEnabled())
//                .setUseOSGiClassLoaderBridging(source.isUseOSGiClassLoaderBridging());
//    }
//
//}
//
