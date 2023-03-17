package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.google.common.collect.Sets;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class RapidTestUtil {

    public static String[] dtoIdProperties(Class<?> entityClass) {
        List<String> idFields = new ArrayList<>();
        ReflectionUtils.doWithFields(entityClass, field -> {
            if (IdPropertyNameUtils.isIdField(field.getName()) ||
                    IdPropertyNameUtils.isCollectionIdField(field.getName())){
                idFields.add(field.getName());
            }
        });
        return Sets.newHashSet(idFields).toArray(new String[0]);
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
