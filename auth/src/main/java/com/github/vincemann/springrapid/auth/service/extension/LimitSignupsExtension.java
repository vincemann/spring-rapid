package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.core.util.HttpServletRequestUtils;
import com.github.vincemann.springrapid.limitsaves.LimitActionsExtension;

/**
 *
 * ExampleConfig for activating this Extension:
 *
     @Configuration
     @Profile(Profiles.PROD)
     public class LimitSignupConfiguration extends UserServiceProxyConfigurer {

         private LimitSignupsExtension limitSignupsExtension;

         @Bean
         public LimitSignupsExtension limitSignupsExtension(){
             // 1 each hour max
            return new LimitSignupsExtension(1, 1000*60*60);
         }

         @Autowired
         public void injectLimitSignupsExtension(LimitSignupsExtension limitSignupsExtension) {
            this.limitSignupsExtension = limitSignupsExtension;
         }

         @Override
         public void configureSecured(ServiceExtensionProxy proxy) {
            proxy.addExtension(limitSignupsExtension);
         }
     }
 *
 */
public class LimitSignupsExtension extends LimitActionsExtension {

    public LimitSignupsExtension(int maxAmountActions, long timeInterval) {
        super(maxAmountActions, timeInterval);
    }

    @Override
    protected String getContactInformation() {
        return HttpServletRequestUtils.getRequest().getRemoteAddr();
    }
}
