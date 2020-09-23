package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingInfo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class RapidDtoClassLocator implements DtoClassLocator {

    /**
     * Ignores role if no role was configured for this MappingContext.
     * Otherwise role and all other properties of {@link DtoMappingInfo} must match for a match.
     * If no match was found with a role, it falls back on searching for dtoClass without Role Information.
     *
     * @param info
     * @return
     */
    @Override
    //@LogInteraction
    public Class<?> find(DtoMappingInfo info, DtoMappingContext context) {
        Map<DtoMappingInfo, Class<?>> mappingEntries = context.getMappingEntries();
        Set<DtoMappingInfo> endpointMatches = findEndpointMatches(info,context);
        MatchSet roleMatchSet = findRoleMatchSet(info, endpointMatches);
        Set<DtoMappingInfo> roleFilteredEntries = roleMatchSet.matches.isEmpty() ? roleMatchSet.criteriaIndifferent : roleMatchSet.matches;
        MatchSet principalMatchSet = findPrincipalMatches(info, roleFilteredEntries);
        Set<DtoMappingInfo> matches = principalMatchSet.matches;
        Set<DtoMappingInfo> inDifferentPrincipalMatches = principalMatchSet.criteriaIndifferent;
        if (!matches.isEmpty()) {
            Assert.isTrue(matches.size() == 1, "Ambigious Mapping, found multiple Dto Matches: " + matches);
            DtoMappingInfo match = matches.stream().findFirst().get();
            log.debug("Matching DtoMappingEntry: " + match);
            return mappingEntries.get(match);
        } else {
            if (inDifferentPrincipalMatches.isEmpty()) {
                throw new IllegalArgumentException("No DtoClass mapped for info: " + info);
            }
            Assert.isTrue(inDifferentPrincipalMatches.size() == 1, "Ambigious Mapping, found multiple Dto Matches: " + inDifferentPrincipalMatches);
            DtoMappingInfo match = inDifferentPrincipalMatches.stream().findFirst().get();
            log.debug("Matching DtoMappingEntry: " + match);
            return mappingEntries.get(match);
        }
    }

    private MatchSet findPrincipalMatches(DtoMappingInfo userMappingInfo, Set<DtoMappingInfo> entries) {
        MatchSet principalMatchSet = new MatchSet();
        entries.stream().forEach(info -> {
            if (info.getPrincipal().equals(DtoMappingInfo.Principal.ALL)) {
                principalMatchSet.criteriaIndifferent.add(info);
            } else if (userMappingInfo.getPrincipal().equals(info.getPrincipal())) {
                principalMatchSet.matches.add(info);
            }
        });
        return principalMatchSet;
    }


    private Set<DtoMappingInfo> findEndpointMatches(DtoMappingInfo userMappingInfo,DtoMappingContext context) {
        return context.getMappingEntries().keySet().stream()
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
                        if (!hasRole) {
                            hasNeededRoles = false;
                        }
                    }
                    if (hasNeededRoles && !info.getAuthorities().isEmpty()) {
                        roleMatchSet.matches.add(info);
                    } else if (info.getAuthorities().isEmpty()) {
                        roleMatchSet.criteriaIndifferent.add(info);
                    }
                });
        return roleMatchSet;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Setter
    static class MatchSet {
        Set<DtoMappingInfo> matches = new HashSet<>();
        Set<DtoMappingInfo> criteriaIndifferent = new HashSet<>();
    }
}
