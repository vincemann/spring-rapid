package io.github.vincemann.springrapid.core.controller.dtoMapper.context;

import io.github.vincemann.springrapid.core.advice.log.LogInteraction;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
/**
 * Represents the Context that contains the information when which dto class should be used for mapping.
 * Offers a find method, for finding the right Dto class for the current situation (represented by {@link DtoMappingInfo}).
 */
@Slf4j
public class DtoMappingContext {
    private Map<DtoMappingInfo, Class<?>> mappingEntries = new HashMap<>();

    DtoMappingContext() {
    }

    /**
     * Ignores role if no role was configured for this MappingContext.
     * Otherwise role and all other properties of {@link DtoMappingInfo} must match for a match.
     * If no match was found with a role, it falls back on searching for dtoClass without Role Information.
     *
     * @param info
     * @return
     */
    @LogInteraction
    public Class<?> find(DtoMappingInfo info) {
        Set<DtoMappingInfo> endpointMatches = findEndpointMatches(info, mappingEntries.keySet());
        MatchSet roleMatchSet = findRoleMatchSet(info, endpointMatches);
        Set<DtoMappingInfo> roleFilteredEntries = roleMatchSet.matches.isEmpty() ? roleMatchSet.criteriaIndifferent : roleMatchSet.matches;
        MatchSet principalMatchSet = findPrincipalMatches(info, roleFilteredEntries);
        Set<DtoMappingInfo> matches = principalMatchSet.matches;
        Set<DtoMappingInfo> inDifferentPrincipalMatches = principalMatchSet.criteriaIndifferent;
        if (!matches.isEmpty()){
            Assert.isTrue(matches.size()==1,"Ambigious Mapping, found multiple Dto Matches: " + matches);
            return mappingEntries.get(matches.stream().findFirst().get());
        }else {
            if (inDifferentPrincipalMatches.isEmpty()){
                throw new IllegalArgumentException("No DtoClass mapped for info: " + info);
            }
            Assert.isTrue(inDifferentPrincipalMatches.size()==1,"Ambigious Mapping, found multiple Dto Matches: " + inDifferentPrincipalMatches);
            return mappingEntries.get(inDifferentPrincipalMatches.stream().findFirst().get());
        }
    }

    private MatchSet findPrincipalMatches(DtoMappingInfo userMappingInfo,Set<DtoMappingInfo> entries){
        MatchSet principalMatchSet = new MatchSet();
        entries.stream().forEach(info -> {
            if (info.getPrincipal().equals(DtoMappingInfo.Principal.ALL)){
                principalMatchSet.criteriaIndifferent.add(info);
            }else if (userMappingInfo.getPrincipal().equals(info.getPrincipal())){
                principalMatchSet.matches.add(info);
            }
        });
        return principalMatchSet;
    }


    private Set<DtoMappingInfo> findEndpointMatches(DtoMappingInfo userMappingInfo, Set<DtoMappingInfo> entries) {
        return mappingEntries.keySet().stream()
                .filter(info -> info.getDirection().equals(userMappingInfo.getDirection())
                        && info.getEndpoint().equals(userMappingInfo.getEndpoint()))
                .collect(Collectors.toSet());
    }


    private MatchSet findRoleMatchSet(DtoMappingInfo userMappingInfo, Set<DtoMappingInfo> entries) {
        MatchSet roleMatchSet = new MatchSet();
        entries.stream()
                .forEach(info -> {
                    boolean hasNeededRoles = true;
                    for (String neededRole : info.getAuthorities()) {
                        boolean hasRole = userMappingInfo.getAuthorities().contains(neededRole);
                        if (!hasRole){
                            hasNeededRoles=false;
                        }
                    }
                    if (hasNeededRoles && !info.getAuthorities().isEmpty()){
                        roleMatchSet.matches.add(info);
                    }else if (info.getAuthorities().isEmpty()){
                        roleMatchSet.criteriaIndifferent.add(info);
                    }
                });
        return roleMatchSet;
    }

    @Getter @AllArgsConstructor @NoArgsConstructor @Builder @Setter
    static class MatchSet {
        Set<DtoMappingInfo> matches = new HashSet<>();
        Set<DtoMappingInfo> criteriaIndifferent = new HashSet<>();
    }

    Map<DtoMappingInfo, Class<?>> getMappingEntries() {
        return mappingEntries;
    }

    void setMappingEntries(Map<DtoMappingInfo, Class<?>> mappingEntries) {
        this.mappingEntries = mappingEntries;
    }

}
