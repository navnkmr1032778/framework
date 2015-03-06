/***
 * 
 * This class has been added to solve the phantomjs driver start issue as per the comment provided in detro/ghostdriver#397 and 
 * Deprecated this class once the official phantomjs-driver-1.2.1 changes are released along with next selenium release (2.44.0 fix or latest version) 
 * 
 */

package org.openqa.selenium.browserlaunchers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;

public class Proxies {
    public static Proxy extractProxy(Capabilities capabilities) {
        return Proxy.extractFrom(capabilities);
    }
}