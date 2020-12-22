package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.core.util.HttpServletRequestUtils;
import com.github.vincemann.springrapid.limitsaves.LimitActionsExtension;

/**
 * To activate add bean of this class to context.
 *
 * e.g.:
 * @Configuration
 * @Profile(Profiles.PROD)
 * public class LimitSignupConfiguration {
 *
 *     @Bean
 *     public LimitSignupsExtension limitSignupsExtension(){
 *         return new LimitSignupsExtension();
 *     }
 * }
 *
 */
public class LimitSignupsExtension extends LimitActionsExtension {

    public LimitSignupsExtension(int maxAmountActions, long timeInterval) {
        super(maxAmountActions, timeInterval);
    }

    @Override
    protected String getPrincipal() {
        return HttpServletRequestUtils.getRequest().getRemoteAddr();
    }
}
